package org.daijie.social.login;

import org.daijie.core.util.ApplicationContextHolder;
import org.daijie.social.login.ali.AliLoginTool;
import org.daijie.social.login.ali.callback.AliLoginCallback;
import org.daijie.social.login.ali.service.AliLoginService;
import org.daijie.social.login.baidu.BaiduLoginTool;
import org.daijie.social.login.baidu.callback.BaiduLoginCallback;
import org.daijie.social.login.baidu.service.BaiduLoginService;
import org.daijie.social.login.qq.QQLoginTool;
import org.daijie.social.login.qq.callback.QQLoginCallback;
import org.daijie.social.login.qq.service.QQLoginService;
import org.daijie.social.login.sina.SinaLoginTool;
import org.daijie.social.login.sina.callback.SinaLoginCallback;
import org.daijie.social.login.sina.service.SinaLoginService;
import org.daijie.social.login.weixin.WeixinLoginTool;
import org.daijie.social.login.weixin.callback.WeixinLoginCallback;
import org.daijie.social.login.weixin.service.WeixinLoginService;

@SuppressWarnings("unchecked")
public class LoginTool {

	/**
	 * 获取第三方登录属性配置
	 * @param <T> 第三方登录配置
	 * @param socialLogin 第三方登录枚举
	 * @return Object
	 */
	public static <T extends LoginProperties> T getProperties(SocialLoginType socialLogin){
		if(getService(socialLogin) == null){
			return null;
		}else{
			return (T) getService(socialLogin).getProperties();
		}
	}
	
	private static <T extends LoginService> T getService(SocialLoginType socialLogin){
		switch (socialLogin) {
		case ALI:
			return (T) ApplicationContextHolder.getBean("aliLoginService", AliLoginService.class);
		case BAIDU:
			return (T) ApplicationContextHolder.getBean("baiduLoginService", BaiduLoginService.class);
		case QQ:
			return (T) ApplicationContextHolder.getBean("qqLoginService", QQLoginService.class);
		case SINA:
			return (T) ApplicationContextHolder.getBean("sinaLoginService", SinaLoginService.class);
		case WEIXIN:
			return (T) ApplicationContextHolder.getBean("weixinLoginService", WeixinLoginService.class);
		}
		return null;
	}
	
	/**
	 * 第三方登录
	 * @param appAuthCode 第三方临时code
	 * @param socialLogin 第三方名称
	 * @param callback 回调函数
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static String login(String appAuthCode, SocialLoginType socialLogin, LoginCallback callback){
		switch (socialLogin) {
		case ALI:
			return AliLoginTool.login(appAuthCode, (AliLoginCallback) callback);
		case BAIDU:
			return BaiduLoginTool.login(appAuthCode, (BaiduLoginCallback) callback);
		case QQ:
			return QQLoginTool.login(appAuthCode, (QQLoginCallback) callback);
		case SINA:
			return SinaLoginTool.login(appAuthCode, (SinaLoginCallback) callback);
		case WEIXIN:
			return WeixinLoginTool.login(appAuthCode, (WeixinLoginCallback) callback);
		}
		return null;
	}
	
	/**
	 * 加载第三方二维码扫码授权获取appAuthCode
	 * @param state 数据声明
	 * @param socialLogin 第三方登录枚举
	 * @return String
	 */
	public static String loadQrcode(String state, SocialLoginType socialLogin){
		return getService(socialLogin).loadQrcode(state);
	}
	
	/**
	 * 加载第三方认证页授权获取appAuthCode
	 * @param state 数据声明
	 * @param socialLogin 第三方登录枚举
	 * @return String
	 */
	public static String loadAuthPage(String state, SocialLoginType socialLogin){
		return getService(socialLogin).loadAuthPage(state);
	}
}
