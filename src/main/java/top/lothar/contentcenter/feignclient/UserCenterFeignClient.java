package top.lothar.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.lothar.contentcenter.domain.dto.user.UserDTO;

/**
 * <h1>openfeign声明式http调用</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 13:29
 */
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {
    /**
     * http://user-center/users/{id}
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
