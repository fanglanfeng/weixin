package com.igoxin.weixin.custom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.swetake.util.Qrcode;

/**
 *  实现生成二维码
 * @author fanglanfeng
 *
 */
public class QRCodeUtils {
	
	  /**  - 这个项目 - 现在使用的是这个 - 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param imgPath 图片路径 
     * @param imgType 图片类型 
     * @param size 二维码尺寸 
     */ 
    public void encoderQRCode(String content, String imgPath, String imgType, int size) {  
        try {  
            BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);  
            composeImgae(bufImg,imgPath);
            //TODO - 先注释掉测试上面的方法
            //File imgFile = new File(imgPath);  
            // 生成二维码QRCode图片  
            //ImageIO.write(bufImg, imgType, imgFile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    /** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param output 输出流 
     * @param imgType 图片类型 
     * @param size 二维码尺寸 
     */ 
    public void encoderQRCode(String content, OutputStream output, String imgType, int size) {  
        try {  
            BufferedImage bufImg = this.qRCodeCommon(content,imgType,size);  
            // 生成二维码QRCode图片  
            ImageIO.write(bufImg, imgType, output);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    // 图片尺寸  
    private int imageWidth = 160;//128 + 12 * (7 - 1);
    
    private void composeImgae(BufferedImage imagebf, String imgPath) throws Exception{

		InputStream bgImage = new FileInputStream("imagebg/GroupBJ.jpg");
		BufferedImage bfImage = ImageIO.read(bgImage);
		BufferedImage image2 = imagebf;//ImageIO.read(imagein2);
		Graphics g = bfImage.getGraphics();
		
		int centerX = (bfImage.getWidth()/2-image2.getWidth()/2)-15;
        int centerY = centerX;  
        g.drawImage(image2, centerX, centerY, imageWidth, imageWidth, null);       
        
		OutputStream outImage = new FileOutputStream(imgPath);//"保存的路径地址"
		JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(outImage);
		enc.encode(bfImage);
		bgImage.close();
		outImage.close();
    }
    
    /**
     * 在二维码中间加小图标
     * @param bufImg
     * @return
     */
    private BufferedImage createPhotoAtCenter(BufferedImage bufImg){
    	 Image im=null;
		try {
			//原始：  二维码做背景
			im = ImageIO.read(new File("image/GroupBJ.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         Graphics2D g = bufImg.createGraphics();  
         // 获取bufImg的中间位置  
         int centerX = bufImg.getMinX() + bufImg.getWidth() / 2 - imageWidth / 2;  
         int centerY = bufImg.getMinY() + bufImg.getHeight() / 2 - imageWidth   / 2;  
         g.drawImage(im, centerX, centerY, imageWidth, imageWidth, null);           
         g.dispose();  
         bufImg.flush();  
         return bufImg;  
    }
	/** 
     * 生成二维码(QRCode)图片的公共方法 
     * @param content 存储内容 
     * @param imgType 图片类型 
     * @param size 二维码尺寸 
     * @return 
     */ 
    public BufferedImage qRCodeCommon(String content,String imgType, int size) {  
        BufferedImage bufImg = null;  
        try {  
            Qrcode qrcodeHandler = new Qrcode();  
            // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小  
            qrcodeHandler.setQrcodeErrorCorrect('M');  
            qrcodeHandler.setQrcodeEncodeMode('B');  
            // 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大  
            qrcodeHandler.setQrcodeVersion(9);  
            // 获得内容的字节数组，设置编码格式  
            byte[] contentBytes = content.getBytes("utf-8");  
            // 图片尺寸  
            int imgSize = imageWidth;//128 + 12 * (7 - 1);
            
            System.out.println("imgSize======"+imgSize);
            
            bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);  
            
            Graphics2D gs = bufImg.createGraphics();  
            
            // 设置背景颜色  
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, imgSize, imgSize);  
   
            // 设定图像颜色> BLACK  
            gs.setColor(Color.BLACK);  
            // 设置偏移量，不设置可能导致解析出错  
            int pixoff = 2;  
            // 输出内容> 二维码  
            if (contentBytes.length > 0 && contentBytes.length < 800) {  
                boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);  
                for (int i = 0; i < codeOut.length; i++) {  
                    for (int j = 0; j < codeOut.length; j++) {  
                        if (codeOut[j][i]) {  
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);  
                        }  
                    }  
                }  
            } else {  
                throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");  
            }  
            gs.dispose();  
            bufImg.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return bufImg;  
    }  
    
	/** 下面的代码都是测试代码 - 只做为开发阶段测试使用 - 生产中不能使用
	 * 测试代码 - 只是简单的生成二维码图片
	 */
	@Test
	public void test(){
		String content = "http://163.com";
		String imagePath = "image/01.png";
		encoderQRCode(content, imagePath, "png", 7);
	}

    //这里是测试代码
	@Test
	public static void drawPaintThroughBufferedImageAndGraphics() throws Exception{
		
		//1.这里是获取到指定的图片，可以在图片上面操作
//		InputStream inputImage = new FileInputStream("imagebg/GroupBJ.jpg");
//		BufferedImage bufImg = ImageIO.read(inputImage);
		
		//2.这里是初始化一个背景，操作
		BufferedImage bufImg = new BufferedImage(200, 200,BufferedImage.TYPE_INT_RGB); 
		
		Graphics g = bufImg.getGraphics();
		g.setColor(Color.BLUE);
		
		//画圆 - 只有线型
		g.drawArc(0, 0, 200, 200, 0,360 );
		
		//画线
		g.setColor(Color.GREEN);
		g.drawLine(10, 10, 30, 70);
		
		//画圆 - 可以填充内部
		g.setColor(Color.YELLOW);
		g.fillArc(40, 40, 40, 80, 0, 360);
		
		//3.做关闭处理
		g.dispose();
		bufImg.flush();
		
		//4.写入图片的路径
		File file = new File("imagebg/text.png");
		//将图片写入到指定路径
		ImageIO.write(bufImg, "png", file);
	}

}
