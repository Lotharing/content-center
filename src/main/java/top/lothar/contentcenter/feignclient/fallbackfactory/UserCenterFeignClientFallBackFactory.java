package top.lothar.contentcenter.feignclient.fallbackfactory;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.lothar.contentcenter.domain.dto.user.UserAddBonusDTO;
import top.lothar.contentcenter.domain.dto.user.UserDTO;
import top.lothar.contentcenter.feignclient.UserCenterFeignClient;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 19:56
 */
@Slf4j
@Component
public class UserCenterFeignClientFallBackFactory implements FallbackFactory<UserCenterFeignClient> {

    @Override
    public UserCenterFeignClient create(Throwable cause) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findById(Integer id) {
                log.warn("远程调用被限流/降级" , cause);
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("我是中国人");
                return userDTO;
            }

            @Override
            public UserDTO addBonus(UserAddBonusDTO userAddBonusDTO) {
                log.warn("远程调用被限流/降级" , cause);
                return null;
            }
        };
    }
}
