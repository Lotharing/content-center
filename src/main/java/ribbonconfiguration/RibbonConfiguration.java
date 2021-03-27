package ribbonconfiguration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return new RandomRule();
    }
}
