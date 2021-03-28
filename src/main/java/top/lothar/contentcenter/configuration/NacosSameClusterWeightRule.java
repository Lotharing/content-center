package top.lothar.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h1>Ribbon 同一集群优先调用,「北京内容中心 - 北京用户中心」</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 11:50
 */
@Slf4j
public class NacosSameClusterWeightRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            // 拿配置文件中的集群名称 BJ
            String clusterName = nacosDiscoveryProperties.getClusterName();
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的微服务名称
            String name = loadBalancer.getName();
            // 服务发现的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();


            // 1.找到服务的所有健康实例 A
            List<Instance> instances = namingService.selectInstances(name, true);
            // 2.过滤相同集群下所有实例 B
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());
            // 3.如果b是空，就用A
            List<Instance> instancesToBeChosen = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instancesToBeChosen = instances;
                log.warn("发生跨地区集群的调用: name = {} , clusterName = {}, instances = {} ", name, clusterName, instances);
            } else {
                instancesToBeChosen = sameClusterInstances;
            }
            // 4.基于nacos权重的负载均衡算法，返回一个实例
            Instance instance = ExtendBalancer.getHostByRandomWeightCustomize(instancesToBeChosen);
            log.info("选择的实例是: port = {} , instance = {} ", instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生异常: {}" ,e);
            return null;
        }
    }
}

/**
 * 利用负载均衡源码，自己包装一下使用
 */
class ExtendBalancer extends Balancer{
    // 包装不能被直接使用的Balancer提供的负载均衡算法
    public static Instance getHostByRandomWeightCustomize(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
