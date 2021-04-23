package top.lothar.contentcenter.controller.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/22 19:23
 */
@Slf4j
@RestController
// 动态更新配置中心对配置
@RefreshScope
public class TestController {
    @Value("${your.config}")
    private String yourConfig;

    @GetMapping("/test-config")
    public String testConfiguration() {
        return this.yourConfig;
    }
}
