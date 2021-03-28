package top.lothar.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;

/**
 * <h1>实现自己的负载均衡规则，支持Nacos权重</h1>
 *
 *  because: spring cloud commons --> 定义了标准
 *           spring cloud loadBalancer --> 没有权重
 *  so:      spring cloud alibaba 遵循这个标准,整合了Ribbon
 *
 *  // TODO 去查实现负载均衡的其他方式
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 11:06
 */
@Slf4j
public class NacosWeightedRule extends AbstractLoadBalancerRule {

    /**
     * NacosClient提供权重的负载均衡算法
     */
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;


    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件并初始化 NacosWeightedRule

    }

    @Override
    public Server choose(Object o) {
        try {
            // Ribbon的入口
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            log.info("loadBalancer = {}", loadBalancer);
            // 想要请求的微服务名称
            String name = loadBalancer.getName();
            // 服务发现的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            // nacosClient自动通过基于权重「在nacos控制台实例配置」的负载均衡算法，给我们选择一个实例
            Instance instance = namingService.selectOneHealthyInstance(name);
            log.info("选择的实例是: port:{}, instance: {}", instance.getPort(),instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }
    }
}
