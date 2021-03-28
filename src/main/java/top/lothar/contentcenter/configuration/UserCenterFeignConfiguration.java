package top.lothar.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 14:33
 * 注：不用使用@Configuration注解避免上下文重叠「否则必须挪启动类扫描的包以外」，边为全局feign的配置
 */
public class UserCenterFeignConfiguration {

    /**
     *         NONE, 不记录日志
     *         BASIC, 记录请求方法 URL 相应状态码 执行时间
     *         HEADERS, BASIC基础上多HEADER
     *         FULL;    记录请求响应header body和元数据
     * @return
     */
    @Bean
    public Logger.Level level(){
        // 让feign打印所有请求的细节
        return Logger.Level.FULL;
    }

}
