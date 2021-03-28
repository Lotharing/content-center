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
import org.apache.commons.lang.StringUtils;
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
            // https://www.imooc.com/article/288674
            //优先选择同集群下，符合metadata的实例
            //如果同集群加没有符合metadata的实例，就选择所有集群下，符合metadata的实例

            // 拿配置文件中的集群名称 BJ
            String clusterName = nacosDiscoveryProperties.getClusterName();
            // 目标服务的元数据信息版本 v1
            String targetVersion = this.nacosDiscoveryProperties.getMetadata().get("target-version");


            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的微服务名称
            String name = loadBalancer.getName();
            // 服务发现的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // 1.找到服务的所有健康实例 A
            List<Instance> instances = namingService.selectInstances(name, true);

            List<Instance> metadataMatchInstances = instances;
            // 如果配置了版本映射，那么只调用元数据匹配的实例
            if (StringUtils.isNotBlank(targetVersion)) {
                metadataMatchInstances = instances.stream()
                        .filter(instance -> Objects.equals(targetVersion, instance.getMetadata().get("version")))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(metadataMatchInstances)) {
                    log.warn("未找到元数据匹配的目标实例！请检查配置。targetVersion = {}, instance = {}", targetVersion, instances);
                    return null;
                }
            }
            // 当前对应版本的健康实例
            List<Instance> clusterMetadataMatchInstances = metadataMatchInstances;
            // 如果配置了集群名称，需筛选同集群下元数据匹配的实例
            if (StringUtils.isNotBlank(clusterName)) {
                clusterMetadataMatchInstances = metadataMatchInstances.stream()
                        .filter(instance -> Objects.equals(clusterName, instance.getClusterName()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(clusterMetadataMatchInstances)) {
                    // 没有找到相同区域集群, 则继续用所有对应v1版本的健康实例列表
                    clusterMetadataMatchInstances = metadataMatchInstances;
                    log.warn("发生跨集群调用。clusterName = {}, targetVersion = {}, clusterMetadataMatchInstances = {}", clusterName, targetVersion, clusterMetadataMatchInstances);
                }
            }
            // 就通过目标v1版本的用户中心, 且是同区域的集群节点
            Instance instance = ExtendBalancer.getHostByRandomWeightCustomize(clusterMetadataMatchInstances);
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
