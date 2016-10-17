package com.igoxin.weixin.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.igoxin.base.UrlRoute;
import com.igoxin.base.WeiXinConfig;
import com.igoxin.bean.vo.PassportVo;
import com.igoxin.util.CommonUtil;
import com.igoxin.weixin.controller.WeiXinFlowerController;

import net.sf.json.JSONObject;
import x7.base.util.HttpClientUtil;
import x7.base.util.JsonX;

/**
 * 微信公众号的自定义处理Controller
 * 
 * 整体是微信公众号的自定义操作 1.自定义回复 2.自定义界面布局
 * 
 * @author fanglanfeng
 *
 */
@Controller
@RequestMapping(UrlRoute.WEIXIN)
public class CustomController {

	// private Map<ThreadLocal<Integer>> cache = null;
	private int markNumber = 1;

	// 主要是做再次发送推送的测试代码， - 暂时不用
	public void analyticalWechatMag_two(String xml, String openid, HttpServletRequest request,
			HttpServletResponse response) {

		/** 解析xml数据 */
		ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);
		/** 回复内容 */
		String result = "";// "详情请咨询购信官网:http://www.igoxin.com";
		String msgType = "";
		if (xmlEntity != null) {
			msgType = xmlEntity.getMsgType();
		}
		result = processingString("++");
		System.out.println("analyticalWechatMag=====result====" + result);

		result = new WechatProcess().processWechatMagtText(xml, openid, result);
		try {
			OutputStream os = response.getOutputStream();
			os.write(result.getBytes("UTF-8"));
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("flowerMessage==================result" + result);
	}

	// 解析微信传递过来的内容，并处理需要返回的数据
	public String analyticalWechatMag(String xml, String openid, HttpServletRequest request,
			HttpServletResponse response) {

		/** 解析xml数据 */
		ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);
		/** 回复内容 */
		String result = "直接在公众号内留言即可，我们会第一时间为您处理留言格式为：订单号 + 收货人姓名 + 手机号码 + 问题";// "详情请咨询购信官网:http://www.igoxin.com";
		String msgType = "";
		if (xmlEntity != null) {
			msgType = xmlEntity.getMsgType();
		}
		/*
		 * TODO - 后期做扩展用 // 文本消息 if
		 * (msgType.equals(MessageType.REQ_MESSAGE_TYPE_TEXT)) { result =
		 * "您发送的是文本消息！"; } // 图片消息 else if
		 * (msgType.equals(MessageType.REQ_MESSAGE_TYPE_IMAGE)) { result =
		 * "您发送的是图片消息！"; } // 地理位置消息 else if
		 * (msgType.equals(MessageType.REQ_MESSAGE_TYPE_LOCATION)) { result =
		 * "您发送的是地理位置消息！"; } // 链接消息 else if
		 * (msgType.equals(MessageType.REQ_MESSAGE_TYPE_LINK)) { result =
		 * "您发送的是链接消息！"; } // 音频消息 else if
		 * (msgType.equals(MessageType.REQ_MESSAGE_TYPE_VOICE)) { result =
		 * "您发送的是音频消息！"; }
		 */

		// 判断事件(自定义菜单点击，首次)
		if (MessageType.REQ_MESSAGE_TYPE_EVENT.endsWith(msgType)) {
			// 点击了自定义菜单点击事件
			if ("CLICK".endsWith(xmlEntity.getEvent())) {
				if ("customerService".endsWith(xmlEntity.getEventKey())) {
					result = processingString("+");// =
													// "直接在公众号内留言即可，我们会第一时间为您处理留言格式为：订单号
													// + 收货人姓名 + 手机号码 + 问题";
				}
				// 这里是关注和取消关注
			} else {
				result = processingString("+");
				/*
				 * result = "客官好！终于等到您了。点击【购信拼团】，开启品质新生活。" +
				 * "【购信鲜花】现已正式上线，推出单品包月，混合花束包月，首单赠送精美条纹花瓶。" +
				 * "点击【购信鲜花】，同您一起open happiness1！" +
				 * "~TIPS:1.售后问题请直接联系服务号，给服务号留言即可；" +
				 * "或直接拨打客服电话：400-033-06122.购信拼团每日为您精挑细选15款高品质拼团商品。";
				 */
			}
		}
		// 事件推送之外的所有处理
		else {
			String str = xmlEntity.getContent();
			result = processingString(str);
		}
		System.out.println("analyticalWechatMag=====result====" + result);
		return result;
	}

	// TODO - 回复处理 - 为了后期扩展用，单独处理
	private String processingString(String str) {

		System.out.println("processingString=====weixin===content====" + str);

		StringBuffer buffer = new StringBuffer();

		System.out.println("WechatProcess==The content you entered is====" + str);
		if (str.contains("+")) {
			buffer.append("终于等到您啦 客官，我是小G，请回复数字选择服务：").append("\n");
			buffer.append("1  拼团失败，如何退款").append("\n");
			buffer.append("2  拼团成功，何时发货").append("\n");
			buffer.append("3  拼团商品质量问题").append("\n");
			buffer.append("4  订单物流查询").append("\n");
			buffer.append("5  成为商家").append("\n");
			buffer.append("6  鲜花配送区域").append("\n");
			buffer.append("7  鲜花种类").append("\n");
			buffer.append("8  鲜花基本养护").append("\n");
			buffer.append("9  鲜花改时间，改地址").append("\n");
			buffer.append("10  鲜花收到不满意").append("\n");
			buffer.append("11  我的专属鲜花码").append("\n");
			// buffer.append("回复数字，小G即刻为您解答");
			return buffer.toString();

			// 拼团回复
		} else if (str.equals("1")) {
			buffer.append("客官，您好，感谢亲对海航购信平台的支持~ ").append("\n");
			buffer.append("很抱歉，您的好友正堵在路上，拼团未成功，钱款将于24小时内退回您的账户哦！").append("\n");
			buffer.append("您可以继续选购其余商品，血拼哦~~ ").append("\n");
			return buffer.toString();
		} else if (str.equals("2")) {
			buffer.append("客官，您好，感谢亲对海航购信平台的支持~恭喜亲拼团成功").append("\n");
			buffer.append("我们将在拼团成功后的24小时内发货哦，到货时间需根据客官的住址范围而定哦").append("\n");
			buffer.append("麻烦亲耐心等待哦，谢谢。").append("\n");
			return buffer.toString();
		} else if (str.equals("3")) {
			buffer.append("客官，您好，感谢亲对海航购信平台的支持~ ").append("\n");
			buffer.append("很抱歉，我们的商品给您带来困扰。麻烦是否可以告知您的订单号+实物图+支付宝账号？").append("\n");
			buffer.append("我们会按比例，将坏品的款项赔偿给您。谢谢。").append("\n");
			return buffer.toString();
		} else if (str.equals("4")) {
			buffer.append("客官，您好，感谢亲对海航购信平台的支持~ ").append("\n");
			buffer.append("在我们的页面右下角，点击“个人中心”，之后可以在”拼团订单”中查询订单信息哦~谢谢。").append("\n");
			return buffer.toString();
		} else if (str.equals("5")) {
			buffer.append("客官，您好，感谢亲对海航购信平台的支持~ ").append("\n");
			buffer.append("麻烦留下亲简述想要合作的事宜哦，并希望可以留下您的联系方式哦，").append("\n");
			buffer.append("我们会在1个工作日内电话联系您呢，谢谢。").append("\n");
			return buffer.toString();
		}
		// 鲜花回复
		else if (str.equals("6")) {
			buffer.append("亲爱的小主，您好，感谢您选择购信鲜花，期待我们每周为您带来的小确幸哦。").append("\n");
			buffer.append("我们的配送范围包含京津冀江浙沪皖粤地区。其余地区，我们正在玩命争取中哦，小主们敬请期待哦！").append("\n");
			return buffer.toString();

		} else if (str.equals("7")) {
			buffer.append("亲爱的小主，您好，感谢您选择购信鲜花哦。").append("\n");
			buffer.append("我们一个月配送四次，每周都不同，品种随机发送。花艺师会根据当季的优质鲜花精心搭配。").append("\n");
			buffer.append("下单时，可以备注忌讳的鲜花哦，我们尽量避免发送。").append("\n");
			return buffer.toString();
		} else if (str.equals("8")) {
			buffer.append("亲爱的小主，您好，感谢您选择购信鲜花哦。").append("\n");
			buffer.append("我们在每次鲜花送达给您前，已为您的鲜花进行保湿处理，若收货时有极少量焉萎的情况产生，您可以在收货后尽快将鲜花插瓶，花束会很快恢复活力度哦~").append("\n");
			return buffer.toString();
		} else if (str.equals("9")) {
			buffer.append("亲爱的小主，您好，感谢您选择购信鲜花哦。").append("\n");
			buffer.append("我们的鲜花在下单后，仍有三次可以改时间改收货地址的机会哦。").append("\n");
			buffer.append("若是不方便收花的话，请以收花日零点为起点，提前48小时在订单页面修改信息哦。").append("\n");
			return buffer.toString();
		} else if (str.equals("10")) {
			buffer.append("亲爱的小主，您好，感谢您选择购信鲜花哦。").append("\n");
			buffer.append("若收到鲜花如有明显质量问题，您可以在我们的公众号内留言哦，客服会在第一时间为您处理").append("\n");
			buffer.append("签收24小时后将不再受理质量问题哦，麻烦小主知悉~~~").append("\n");
			return buffer.toString();
		} else if (str.equals("11")) {
			buffer.append("image");
			return buffer.toString();
		} else{
			
			/*
			// 如果没有指定的关键字，统一回复为鲜花二维码图片
			System.out.println("TulingApiProcess==TulingApiProcess==" + str);
			TulingApiProcess tuling = new TulingApiProcess();
			System.out.println("TulingApiProcess==tuling==" + tuling.toString());
			String strtemp = "";
			strtemp = tuling.getTulingResult(str);
			*/
			//TODO - 代替智能回复
			
			buffer.append("客官，您好。感谢您对海航购信的支持！").append("\n");
			buffer.append("若以上问题仍无法解决您的困扰的话，欢迎添加购信官方客服微信号：igoxinvip，我们会有专员进行解答哦~谢谢。").append("\n");
			buffer.append("下单时，可以备注忌讳的鲜花哦，我们尽量避免发送。").append("\n");
			System.out.println("tuling=====controller ===== " + buffer.toString());
			
			return buffer.toString();
		}
	}

	/**
	 * - - 公众号消息被动回复 - -
	 */
	@RequestMapping(UrlRoute.WEIXIN_MESSAGE_XIAO)
	@ResponseBody
	public void xiaoMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setAttribute("xiao", "xiao");
		flowerMessage(request, response);
	}

	@RequestMapping(UrlRoute.WEIXIN_MESSAGE_FLOWER)
	@ResponseBody
	public void flowerMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {

		/** 判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回 */
		String echostr = request.getParameter("echostr");
		// 判断是否是第一次
		if (echostr != null && echostr.length() > 1) {
			System.out.println("For the first time ======one====");
			echostrMessageVerification(request, response);
		} else {

			System.out.println("For the not first time ======notOne====");
			System.out.println("flowerMessage=========request==========" + request);

			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");

			String openid = request.getParameter("openid");

			/** 读取接收到的xml消息 */
			StringBuffer sb = new StringBuffer();
			InputStream is = request.getInputStream();

			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			String xml = sb.toString(); // 为接收到微信端发送过来的xml数据
			// 对微信发过来的数据进行解析
			String xmlString = analyticalWechatMag(xml, openid, request, response);

			// 最后返回给微信的xml数据
			String result = "";

			// 2.1 - 拿到access_token
			String appid = WeiXinConfig.APPID;
			String secret = WeiXinConfig.APPSECRET;
			String get_token = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid
					+ "&secret=" + secret;
			// 请求 token
			JSONObject jsonObjectToken = CommonUtil.httpsRequest(get_token, "GET", null);
			String access_token = (String) jsonObjectToken.get("access_token");

			if (!xmlString.contains("image")) {
				result = new WechatProcess().processWechatMagtText(xml, openid, xmlString);
			}
			// ====================== 统一回复为鲜花二维码图片 ======================
			else {

				// (1)创建用户
				WeiXinFlowerController wxController = new WeiXinFlowerController();
				Map<String, Object> resultWxMapController = wxController.getUserInfoFlower(request);
				resultWxMapController.put("token3", openid);
				resultWxMapController.put("tokenType", "WX_TOKEN");
				resultWxMapController.put("type", "WEIXIN");

				System.out.println("userInfo ======== WeiXinFlowerController===== " + resultWxMapController.toString());
				// 生产
				String requestUserInfo = "http://ps.gocent.net/packedSale/user/signInOrUp";
				// 测试环境
				// String requestUserInfo =
				// "http://test.flower.gocent.net/packedSale/user/signInOrUp";
				String resUserInfo = HttpClientUtil.post(requestUserInfo, resultWxMapController);

				Map<String, Object> map = JsonX.toMap(resUserInfo);

				JSONArray ja = (JSONArray) map.get("result");

				List<PassportVo> list = JsonX.toList(ja.toJSONString(), PassportVo.class);

				PassportVo passpoerVo = null;
				if (!list.isEmpty()) {
					passpoerVo = (PassportVo) list.get(0);
				}

				long shareId = passpoerVo.getGroupId();

				/*
				 * System.out.println("userInfo ======== resUserInfo===== "
				 * +resUserInfo.toString()); System.out.println(
				 * "userInfo ======== map===== "+map.toString());
				 * System.out.println("userInfo ======== ja===== "
				 * +ja.toString()); System.out.println(
				 * "userInfo ======== list===== "+list.toString());
				 * System.out.println("userInfo ======== passpoerVo===== "
				 * +passpoerVo.toString());
				 */

				System.out.println("userInfo ============ groupId==================" + shareId);

				// 2.开始正常的处理
				// String url =
				// "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";
				// 2.2 - 生成图片
				// 二维码图片的内容
				String content = "http://packsale.gocent.net/index.html#/tab/index?sharerId=" + shareId;
				String imagePath = "image/" + openid + ".png";

				QRCodeUtils qrcode = new QRCodeUtils();

				// BufferedImage bufferIamge = qrcode.qRCodeCommon(content,
				// "png",7);

				// 2.2.2这里会将图片临时存放在项目的一个目录中，可随时删除处理，由于一个图片只有5k左右，所以在现有项目中的量不是很大
				qrcode.encoderQRCode(content, imagePath, "png", 7);

				// 2.3 - 将生成的图片上传到微信素材中心

				/**
				 * 上传（这里是根据项目做的临时的图片上传处理），获得media * access_token
				 * 获取到的access_token * imagePath 图片的位置（相对位置）
				 */

				// 2.3.1 - 微信返回的数据 - 这里最主要是获取到图片在微信中的 media_id,
				// 因为在发送图片给用户的时候，需要media_id(在微信素材库的唯一标识),而不是图片的url(更不是二进制数据)
				String jadonMedias = HttpUploadFile.uploadImage(access_token, imagePath);

				jadonMedias = jadonMedias.substring(1, jadonMedias.length() - 2);
				System.out.println("jadonMedias===" + jadonMedias);
				String[] strings = jadonMedias.split(",");

				System.out.println("strings===" + strings);

				Object media = "";

				Map<String, Object> mediasMap = new HashMap<String, Object>();
				for (String sss : strings) {
					String[] ms = sss.split(":");
					ms[0] = ms[0].substring(1, ms[0].length() - 1);
					ms[1] = ms[1].substring(1, ms[1].length() - 1);
					System.out.println("ms[0]===" + ms[0] + "ms[1]===" + ms[1]);
					if ("media_id".equals(ms[0])) {
						media = ms[1];
					}
					mediasMap.put(ms[0], ms[1]);
				}
				System.out.println("mediasMap.toString() === " + mediasMap.toString());
				String media_id = (String) mediasMap.get("media_id");
				// Object created_at = mediasMap.get("created_at");
				System.out.println("flowerMessage=====media_id========" + media);
				result = new WechatProcess().processWechatMagImage(xml, openid, media_id);
			}
			// 2.4 - 判断用户提交的内容、触发的事件，判断处理，返回给用户对应的信息
			try {
				OutputStream os = response.getOutputStream();
				os.write(result.getBytes("UTF-8"));
				os.flush();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("flowerMessage==================result" + result);
		}
	}

	// ===================== -下面是--第一次在公众号里面配置的时候用--- ==================
	/**
	 * - - 公众号消息被动回复 - 验证token 第一次配置的时候用 - 也可以直接将接口写在下面使用
	 * 
	 */
	public void echostrMessageVerification(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String signature = request.getParameter("signature");
		String echostr = request.getParameter("echostr");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");

		PrintWriter out = response.getWriter();
		if (checkSignature(signature, timestamp, nonce)) {
			System.out.println("check ok");
			out.print(echostr);
		}
		out.close();
		System.out.println("flowerMessage=========================" + request);
	}

	private boolean checkSignature(String signature, String timestamp, String nonce) {
		String token = "xiao2016";
		String[] arr = new String[] { token, timestamp, nonce };
		Arrays.sort(arr);
		String content = arr[0] + arr[1] + arr[2];
		MessageDigest md = null;
		String result = "";
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] date = md.digest(content.toString().getBytes());
			// 将字节数组转换成字符串
			result = bytesToStr(date);
			System.out.println("加密后的" + result);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result != null ? (result.equals(signature.toUpperCase())) : false;
	}

	// 将直接数组转换成十六进制字符串
	private static String bytesToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	// 将一个字节转换成16进制字符串
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] temp1 = new char[2];
		temp1[0] = Digit[mByte >>> 4 & 0X0F];
		temp1[1] = Digit[mByte & 0X0F];
		String str = new String(temp1);
		return str;
	}
	// ===================== -上面是:--第一次在公众号里面配置的时候用--- ==================
}
