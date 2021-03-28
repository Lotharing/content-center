package ribbonconfiguration;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lothar.contentcenter.configuration.NacosWeightedRule;

/**
 * <h1>不能在application启动类包下，为了避免父子上下文重合，这里是做细粒度的配置，避免了重合就可以让规则指定某个服务使用，而不是让所有服务使用</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 00:21
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule ribbonRule(){
        // return new RandomRule();

        // 自定义Nacos提供的权重负载均衡规则
        return new NacosWeightedRule();
    }
}
