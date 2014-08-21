package org.bigmouth.ticket4j.impl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.bigmouth.ticket4j.Ticket4jDefaults;
import org.bigmouth.ticket4j.User;
import org.bigmouth.ticket4j.entity.Response;
import org.bigmouth.ticket4j.entity.response.LoginSuggestResponse;
import org.bigmouth.ticket4j.http.Ticket4jHttpClient;
import org.bigmouth.ticket4j.http.Ticket4jHttpResponse;
import org.bigmouth.ticket4j.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultUser extends AccessSupport implements User {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUser.class);
    
    private String uriLogin = Ticket4jDefaults.URI_LOGIN;
    
    private final String username;
    private final String password;
    
    public DefaultUser(Ticket4jHttpClient ticket4jHttpClient, String username, String password) {
        super(ticket4jHttpClient);
        this.username = username;
        this.password = password;
    }

    @Override
    public Response login(String passCode, Ticket4jHttpResponse ticket4jHttpResponse) {
        LoginSuggestResponse result = new LoginSuggestResponse();
        HttpClient httpClient = ticket4jHttpClient.buildHttpClient();
        HttpPost post = ticket4jHttpClient.buildPostMethod(uriLogin, ticket4jHttpResponse);
        
        try {
            addPair(post, new NameValuePair[] {
                    new BasicNameValuePair("loginUserDTO.user_name", username) ,
                    new BasicNameValuePair("userDTO.password", password),
                    new BasicNameValuePair("randCode", passCode)
            });
            if (LOGGER.isInfoEnabled()) 
                LOGGER.info("正在登录...");
            HttpResponse response = httpClient.execute(post);
            String body = HttpClientUtils.getResponseBody(response);
            result = fromJson(body, LoginSuggestResponse.class);
            if (!result.isContinue()) {
                result.printMessage();
            }
        }
        catch (Exception e) {
            LOGGER.error("登录失败,错误原因：{}", e.getMessage());
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
}
