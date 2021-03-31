package top.lothar.contentcenter.service.content;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lothar.contentcenter.dao.content.ShareMapper;
import top.lothar.contentcenter.domain.dto.content.ShareAuditDTO;
import top.lothar.contentcenter.domain.dto.content.ShareDTO;
import top.lothar.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import top.lothar.contentcenter.domain.dto.user.UserDTO;
import top.lothar.contentcenter.domain.entity.content.Share;
import top.lothar.contentcenter.domain.enums.AuditStatusEnum;
import top.lothar.contentcenter.feignclient.UserCenterFeignClient;
import java.util.Objects;


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
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private ShareMapper shareMapper;

    /**
     * 查询分享信息
     * @param id
     * @return
     */
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

    /**
     * 分享审核
     * @param id
     * @param auditDTO
     * @return
     */
    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        // 查询并参数校验
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share==null) {
            throw new IllegalArgumentException("参数非法! 该分享不存在");
        }
        if (!Objects.equals(AuditStatusEnum.NOT_YET.toString(),share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法! 该分享已经审核通过或不通过");
        }
        // 审核并更新
        share.setAuditStatus(auditDTO.getAuditStatusEnum().toString());
        this.shareMapper.updateByPrimaryKey(share);
        // 如果PASS 需要加积分「加积分是个异步操作 有效缩短接口响应耗时」
        // 异步方法「1.AsyncRestTemplate 2.@Async 3.WebClient spring5.0引入 4.消息队列MQ」
        rocketMQTemplate.convertAndSend("add-bonus", UserAddBonusMsgDTO
        .builder()
        .userId(share.getUserId())
        .bonus(50)
        .build());
        return share;
    }
}
