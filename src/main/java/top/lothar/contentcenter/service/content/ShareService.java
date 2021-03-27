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
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ShareMapper shareMapper;

    public ShareDTO findShareById(Integer id){
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 发布人ID
        Integer userId = share.getUserId();
        // 从n a c o s注册中心获取可用的user实例子
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");

        String targetUri = instances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("当前没有实例..."));

        // 所有用户中心实例的请求地址
        List<String> targetUris = instances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .collect(Collectors.toList());

        // 客户端负载均衡： 随机算法
        int index = ThreadLocalRandom.current().nextInt(targetUris.size());
        targetUri = targetUris.get(index);

        log.info("请求的目标地址: {}" , targetUri);
        // 怎么调用用户微服务的 /users/{id} 的
        UserDTO userDTO = restTemplate.getForObject(targetUri, UserDTO.class,1);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

}
