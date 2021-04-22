package top.lothar.contentcenter.feignclient.intercept;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>FeignIntercept传递token拦截</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/22 09:54
 */
public class TokenRelayRequestIntercept implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1.获取token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("X-Token");
        // 2.token传递
        if (StringUtils.isNotBlank(token)) {
            template.header("X-Token",token);
        }
    }

}
