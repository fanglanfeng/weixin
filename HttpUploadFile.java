package com.igoxin.weixin.custom;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;

import org.junit.Test;

/**
 *  java通过模拟post方式提交表单实现图片上传功能实例 其他文件类型可以传入 contentType 实现
 * @author fanglanfeng
 *
 */
public class HttpUploadFile {
	
	/**
	 * 上传图片 - 模拟post表单提交
	 * 
	 * @param urlStr
	 * @param textMap
	 * @param fileMap
	 * @param contentType
	 *            没有传入文件类型默认采用application/octet-stream
	 *            contentType非空采用filename匹配默认的图片类型
	 * @return 返回response数据
	 */
	@SuppressWarnings("rawtypes")
	public static String formUpload(String urlStr, Map<String, String> textMap, Map<String, String> fileMap,
			String contentType) {
		String res = "";
		HttpURLConnection conn = null;
		// boundary就是request头和上传文件内容的分隔符
		String BOUNDARY = "---------------------------123821742118716";
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}
			// file
			if (fileMap != null) {
				Iterator iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();

					// 没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
					contentType = new MimetypesFileTypeMap().getContentType(file);
					// contentType非空采用filename匹配默认的图片类型
					if (!"".equals(contentType)) {
						if (filename.endsWith(".png")) {
							contentType = "image/png";
						} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
								|| filename.endsWith(".jpe")) {
							contentType = "image/jpeg";
						} else if (filename.endsWith(".gif")) {
							contentType = "image/gif";
						} else if (filename.endsWith(".ico")) {
							contentType = "image/image/x-icon";
						}
					}
					if (contentType == null || "".equals(contentType)) {
						contentType = "application/octet-stream";
					}
					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
					out.write(strBuf.toString().getBytes());
					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();
			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();

			StringBuffer strBufTemp = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
				strBufTemp.append(line);
			}
			// res = strBuf.toString();
			res = strBufTemp.toString();

			System.out.println("res==strBufTemp==" + res);
			reader.close();
			reader = null;
		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}
	
	//=================== 下面的内容是测试用的 =================== 
	/**
	 * TODO - 
	 * 主要是做测试用途，发布的时候建议屏蔽
	 * @param args
	 */
//	public static void main(String[] args) {
	public void mainTest(){
		//这里可以根据官网测试，拿到token - 下面的是微信官网的测试 - 1.接口类型：“基础支持”  2.接口列表：获取access_token接口/token
		//https://mp.weixin.qq.com/debug/cgi-bin/apiinfo?t=index&type=自定义菜单&form=自定义菜单创建接口%20/menu/create
		//TODO --- 
		
		String access_token = ""; 
		//拿到微信返回的数据，主要是拿到 media_id ,因为可以通过它从微信拿到图片
		String res = uploadImage(access_token, "/image/01.png");
		System.out.println("res====" + res);
		String jadonMedias = res;
		jadonMedias = jadonMedias.substring(1, jadonMedias.length() - 2);
		System.out.println("jadonMedias===" + jadonMedias);
		String[] strings = jadonMedias.split(",");
		System.out.println("strings===" + strings);
		Object media = "";
		Map<String, Object> mediasMap = new HashMap<String, Object>();
		for (String sss : strings) {
			String[] ms = sss.split(":");			
			ms[0] = ms[0].substring(1, ms[0].length()-1);
			ms[1] = ms[1].substring(1, ms[1].length()-1);

			System.out.println("ms[0]===" + ms[0] + "ms[1]===" + ms[1]);
			if("media_id".equals(ms[0])){
				media = ms[1];
			}
			mediasMap.put(ms[0], ms[1]);
		}
		System.out.println("mediasMap.toString() === " + mediasMap.toString());
		Object media_id = mediasMap.get("media_id");
		//查看下面的两个值是否一致，主要是做验证处理
		System.out.println("media_id========" + media_id);
		System.out.println("media========" + media);
	}
	/**
	 * 测试上传png图片
	 * 
	 */
	public static String uploadImage(String access_token, String fileName){
		//上传图片的URL
		String url = "https://api.weixin.qq.com/cgi-bin/media/upload?";
		//测试路径  - 由于本人是用Mac 电脑开发的，使用windows开发是一定要更换路径
//		fileName = "/Users/admin/Pictures/001.jpg"; 
		Map<String, String> textMap = new HashMap<String, String>();
		// 可以设置多个input的name，value
		textMap.put("access_token", access_token);
		textMap.put("type", "image");
		// 设置file的name，路径
		Map<String, String> fileMap = new HashMap<String, String>();
		fileMap.put("upfile", fileName);
		String contentType = "image/png";// image/png
		String ret = formUpload(url, textMap, fileMap, contentType);
		System.out.println("ret==" + ret);
		return ret;
	}
	//=================== 上面的内容是测试用的 =================== 
	
	
}