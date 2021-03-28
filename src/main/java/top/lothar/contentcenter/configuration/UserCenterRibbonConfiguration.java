package top.lothar.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * <h1>Ribbon 负载均衡规则自定义配置器</h1>
 * {@link RibbonClient name 用户中心 configuraion 指定的Ribbon负载均衡实现}
 * {@link RibbonClients defaultConfiguration 指定全局的Ribbon负载均衡实现}
 * @author LuTong.Zhao
 * @Date 2021/3/28 00:17
 */
@Configuration
@RibbonClient(name = "user-center",configuration = RibbonConfiguration.class)
// @RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {

}
