package org.daijie.shiro.oauth2;

import javax.servlet.http.HttpServletRequest;

import org.daijie.core.result.ModelResult;
import org.daijie.core.util.http.CookieUtil;
import org.daijie.core.util.http.HttpConversationUtil;
import org.daijie.shiro.configure.ShiroProperties;
import org.daijie.shiro.oauth2.configure.ShiroOauth2Properties;
import org.daijie.shiro.oauth2.excption.ShiroOauth2MatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.baomidou.kisso.security.token.SSOToken;

/**
 * 用户匹配器实现类
 * 通过请求其它shiro实现的登录服务
 * properties需要配置参数：
 * 		shiro.oauth2.loginUrl
 * 		shiro.oauth2.loginMethod
 * @author daijie_jay
 * @since 2017年12月27日
 */
public class RequestAuthenticationMatch implements AuthenticationMatch {
	
	private static final String HEADER_COOKIE_KEY = "Set-Cookie";
	
	@Autowired
	private ShiroOauth2Properties shiroOauth2Properties;
	
	@Autowired
	private ShiroProperties shiroProperties;

	@Override
	public Boolean match(String username, String password) {
		String url = shiroOauth2Properties.getLoginUrl();
		String method = shiroOauth2Properties.getLoginMethod();
		if(url.contains("{username}") || url.contains("{password}")){
			url.replaceAll("{username}", username);
			url.replaceAll("{password}", password);
		}else{
			if(url.contains("?")){
				url += "&";
			}else{
				url += "?";
			}
			url += "username=" + username + "&password=" + password;
		}
		@SuppressWarnings("rawtypes")
		ResponseEntity<ModelResult> result = null;
		RestTemplate restTemplate = new RestTemplate();
		switch (RequestMethod.valueOf(method.toUpperCase())) {
		case GET:
			result = restTemplate.getForEntity(url, ModelResult.class);
			break;
		case POST:
			result = restTemplate.postForEntity(url, null, ModelResult.class);
			break;
		default:
			throw new ShiroOauth2MatchException("只能是GET或POST请求");
		}
		HttpServletRequest request = HttpConversationUtil.getRequest();
		for (String key : result.getHeaders().keySet()) {
			for(String value : result.getHeaders().get(key)){
				if(key.contains(HEADER_COOKIE_KEY) && value.contains(HttpConversationUtil.TOKEN_NAME)){
					String token = value.split(";")[0].split("=")[1];
					CookieUtil.set(HttpConversationUtil.TOKEN_NAME, token, null);
					if(shiroProperties.getKissoEnable()){
						request.setAttribute(HttpConversationUtil.TOKEN_NAME, SSOToken.parser(token, false).getIssuer());
					}else{
						request.setAttribute(HttpConversationUtil.TOKEN_NAME, token);
					}
				}
			}
		}
		return result.getBody().isSuccess();
	}

}
