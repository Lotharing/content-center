package top.lothar.contentcenter.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <h1>角色权限校验</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/22 15:38
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAuthorization {

    String value();
}
