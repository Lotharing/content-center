package top.lothar.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.lothar.contentcenter.configuration.UserCenterFeignConfiguration;
import top.lothar.contentcenter.domain.dto.user.UserDTO;

// 这个是不对异常进行捕获处理对
import top.lothar.contentcenter.feignclient.fallback.UserCenterFeignClientFallBack;

// 可以使用fallbackFactory = UserCenterFeignClientFallBackFactory.class 来对异常处理
import top.lothar.contentcenter.feignclient.fallbackfactory.UserCenterFeignClientFallBackFactory;

/**
 * <h1>openfeign声明式http调用</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 13:29
 */
@FeignClient(name = "user-center",configuration = UserCenterFeignConfiguration.class,
        fallbackFactory = UserCenterFeignClientFallBackFactory.class)
public interface UserCenterFeignClient {
    /**
     * http://user-center/users/{id}
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);

    /***
     * 多参数配置
     * https://www.imooc.com/article/289000 手记：Feign多参数请求写法主要是@StringQueryMap @RequestParam @RequestBody
     *
     * 外部url配置「不再nacos中注册的」也就是Feign脱离Ribbon的使用方式
     * @FeignClient(name = "xxx" , url = "http://www.google.com")
     * interface
     *      @GetMapping("")
     *      String index();
     *      让controller去调用就行了
     *
     * https://www.imooc.com/article/289005  * feign常见问题总结
     */
}
