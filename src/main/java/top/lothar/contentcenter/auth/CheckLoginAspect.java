package top.lothar.contentcenter.auth;

import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.lothar.contentcenter.util.JwtOperator;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/21 22:00
 */
@Aspect
@Component
public class CheckLoginAspect {

    @Autowired
    private JwtOperator jwtOperator;

    @Around("@annotation(top.lothar.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) {
        try {
            // 从header中获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("X-Token");
            // 校验token
            Boolean isVaild = jwtOperator.validateToken(token);
            if (!isVaild) {
                throw new SecurityException("Token不合法");
            }
            // 用户信息放request
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));
            // 放行
            return point.proceed();
        } catch (Throwable throwable) {
            throw new SecurityException("token不合法");
        }
    }


}
