package top.lothar.contentcenter.auth;

import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.lothar.contentcenter.util.JwtOperator;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/21 22:00
 */
@Aspect
@Component
public class AuthAspect {

    @Autowired
    private JwtOperator jwtOperator;

    /**
     * token验证
     * @param point
     * @return
     */
    @Around("@annotation(top.lothar.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        this.checkToken();
        // 放行
        return point.proceed();
    }

    private void checkToken() {
        try{
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
        }catch (Throwable throwable){
            throw new SecurityException("Token不合法");
        }

    }

    /**
     * 校验token和角色信息
     * @param point
     * @return
     */
    @Around("@annotation(top.lothar.contentcenter.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint point) throws Throwable {
        try{
            this.checkToken();
            // 从header中获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String role = (String) request.getAttribute("role");
            // 拿到注解中值
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Method method = methodSignature.getMethod();
            CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);
            // 方法注解上角色信息
            String annotationValue = annotation.value();
            if (!Objects.equals(role,annotationValue)) {
                throw new SecurityException("用户没有权限访问 ！");
            }
        }catch (Throwable throwable){
            throw new SecurityException("用户没有权限访问 ！", throwable);
        }
        return point.proceed();
    }

}
