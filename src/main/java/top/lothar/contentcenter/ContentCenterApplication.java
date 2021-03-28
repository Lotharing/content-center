package top.lothar.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Project content-center 内容中心
 * @author zhaolutong
 * @Date 2020-03-22 22:10:00
 *
 * {@link MapperScan}扫描MySQL这些包里的接口
 */
@MapperScan("top.lothar")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }

    /**
     * spring ioc 中创建对象
     * {@link LoadBalanced} 为RestTemplate整合Ribbon
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
