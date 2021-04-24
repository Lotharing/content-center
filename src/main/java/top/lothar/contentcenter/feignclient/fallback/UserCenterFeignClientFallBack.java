package top.lothar.contentcenter.feignclient.fallback;

import org.springframework.stereotype.Component;
import top.lothar.contentcenter.domain.dto.user.UserAddBonusDTO;
import top.lothar.contentcenter.domain.dto.user.UserDTO;
import top.lothar.contentcenter.feignclient.UserCenterFeignClient;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/28 19:44
 */
@Component
public class UserCenterFeignClientFallBack implements UserCenterFeignClient {

    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("我是中国人");
        return userDTO;
    }

    @Override
    public UserDTO addBonus(UserAddBonusDTO userAddBonusDTO) {
        return null;
    }
}
