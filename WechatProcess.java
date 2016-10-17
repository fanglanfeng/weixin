package com.igoxin.weixin.custom;

/**
 * 微信xml消息处理流程逻辑类
 * @author fanglanfeng
 *
 */
public class WechatProcess{
	
	/**
	 * 解析处理xml、回复文本信息
	 * @param xml 接收到的微信数据
	 * @return	最终的解析结果（xml格式数据）
	 */
	public String processWechatMagtText(String xml,String openid,String str){
		
		/** 解析xml数据 */
		ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);
		/** 回复内容 */
		String result = "";
		result = new FormatXmlContent().formatXmlText(xmlEntity.getFromUserName(), xmlEntity.getToUserName(), str);
		/** 
		 *  因为最终回复给微信的也是xml格式的数据，所有需要将其封装为 -文本类型- 返回消息
		 * */
		System.out.println("processWechatMagtText=====result===="+result);
		return result;
	}
	
	/**
	 * 解析处理xml、回复二维码图片信息
	 * @param xml 接收到的微信数据
	 * @return	最终的解析结果（xml格式数据）
	 */
	public String processWechatMagImage(String xml,String openid,String media_id){
		/** 解析xml数据 */
		ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);
		/** 回复内容 */
		String result = "";		
		System.out.println("xmlEntity.getFromUserName()"+xmlEntity.getFromUserName());
		System.out.println("xmlEntity.getToUserName()"+xmlEntity.getToUserName());
		
		result = new FormatXmlContent().formatXmlImage(xmlEntity.getFromUserName(), xmlEntity.getToUserName(), media_id);
		
		/** 
		 *  因为最终回复给微信的也是xml格式的数据，所有需要将其封装为文本类型返回消息
		 * */
		System.out.println("processWechatMagImage=====result===="+result);
		return result;
	}
}

