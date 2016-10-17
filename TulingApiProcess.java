package com.igoxin.weixin.custom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;


/**
 * 调用图灵机器人api接口，获取智能回复内容 - 暂时没有接入进来
 * @author fanglanfeng
 *
 */
public class TulingApiProcess {
	
	
	/**
	 * 调用图灵机器人api接口，获取智能回复内容，解析获取自己所需结果
	 * @param content
	 * @return
	 */
	public String getTulingResult(String content){
		/** 此处为图灵api接口，参数key需要自己去注册申请，先以11111111代替 */
//		5bd75873fd7a4b8ca290dceb3d7074e6  == own_test
//		fbf65977ec474ae1b0a073837cced1d8  == gouxin
		String apiUrl = "http://www.tuling123.com/openapi/api?key=fbf65977ec474ae1b0a073837cced1d8&userid=12345678&info=";
		String param = "";
		
		System.out.println("TulingApiProcess==apiUrl==" + apiUrl.toString());
		try {
			param = apiUrl+URLEncoder.encode(content,"utf-8");
		} catch (UnsupportedEncodingException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //将参数转为url编码
		/** 发送httpget请求 */
		HttpGet request = new HttpGet(param);
		String result = "";
		try {
			HttpResponse response = HttpClients.createDefault().execute(request);
			if(response.getStatusLine().getStatusCode()==200){
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		// 文档地址 [http://www.tuling123.com/help/h_cent_webapi.jhtml?nav=doc]
		/** 请求失败处理 */
		if(null==result){
			return "对不起，你说的话真是太高深了……";
		}
		try {
			JSONObject json = new JSONObject(result);
			//以code=100000为例，参考图灵机器人api文档
			if(100000==json.getInt("code") || 40001==json.getInt("code") || 40002==json.getInt("code") || 40003==json.getInt("code")){
				result = json.getString("text");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("TulingApiProcess ============= "+result);
		
		return result;
	}
}
