package top.lothar.contentcenter.service.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.lothar.contentcenter.dao.content.ShareMapper;
import top.lothar.contentcenter.domain.dto.content.ShareDTO;
import top.lothar.contentcenter.domain.dto.user.UserDTO;
import top.lothar.contentcenter.domain.entity.content.Share;
import top.lothar.contentcenter.feignclient.UserCenterFeignClient;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/26 16:50
 */
@Slf4j
@Service
public class ShareService {


    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private ShareMapper shareMapper;

    public ShareDTO findShareById(Integer id){
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 发布人ID
        Integer userId = share.getUserId();
        // 通过openfeign实现http调用user-center服务接口 , 并且feign整合Ribbon, 所以Ribbon配置对负载均衡策略依旧有效
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

}
