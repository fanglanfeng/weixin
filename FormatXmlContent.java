package com.igoxin.weixin.custom;

import java.util.Date;


/**
 * 被动回复的内容
 * @author fanglanfeng
 *
 */
public class FormatXmlContent {
	
	/**
	 * 文字类的返回消息
	 * @param toUser
	 * @param fromUser
	 * @param content
	 * @return
	 */
	public String formatXmlText(String toUser,String fromUser,String content){	
		StringBuffer sb = new StringBuffer();
		Date date = new Date();
		sb.append("<xml><ToUserName><![CDATA[");
		sb.append(toUser);
		sb.append("]]></ToUserName><FromUserName><![CDATA[");
		sb.append(fromUser);
		sb.append("]]></FromUserName><CreateTime>");
		sb.append(date.getTime());
		sb.append("</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[");
		sb.append(content);
		sb.append("]]></Content><FuncFlag>0</FuncFlag></xml>");
		return sb.toString();
	}
	
	/**
	 * 图片类的返回消息
	 * @param toUser
	 * @param fromUser
	 * @param image
	 * @param media_id
	 * @return
	 */
	public String formatXmlImage(String toUser,String fromUser,String media_id){	
		
		StringBuffer sb = new StringBuffer();
		Date date = new Date();
		
		sb.append("<xml><ToUserName><![CDATA[");
		sb.append(toUser);
		sb.append("]]></ToUserName><FromUserName><![CDATA[");
		sb.append(fromUser);
		sb.append("]]></FromUserName><CreateTime>");
		sb.append(date.getTime());
		sb.append("</CreateTime><MsgType><![CDATA[");
		sb.append("image");
		sb.append("]]></MsgType>");
		sb.append("<Image><MediaId><![CDATA[");
		sb.append(media_id);
		sb.append("]]></MediaId></Image></xml>");
		
		return sb.toString();
	}

}
