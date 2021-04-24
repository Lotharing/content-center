package top.lothar.contentcenter.service.content;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lothar.contentcenter.dao.content.MidUserShareMapper;
import top.lothar.contentcenter.dao.content.ShareMapper;
import top.lothar.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import top.lothar.contentcenter.domain.dto.content.ShareAuditDTO;
import top.lothar.contentcenter.domain.dto.content.ShareDTO;
import top.lothar.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import top.lothar.contentcenter.domain.dto.user.UserAddBonusDTO;
import top.lothar.contentcenter.domain.dto.user.UserDTO;
import top.lothar.contentcenter.domain.entity.content.MidUserShare;
import top.lothar.contentcenter.domain.entity.content.Share;
import top.lothar.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import top.lothar.contentcenter.domain.enums.AuditStatusEnum;
import top.lothar.contentcenter.feignclient.UserCenterFeignClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


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

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Autowired
    private MidUserShareMapper midUserShareMapper;

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
        /**
         * 本身我这里的逻辑是：
         *  1.执行本地审核 异步发送增加积分的消息 让 用户中心去处理「增加积分 日志等操作」  2.share加入缓存
         *  如果审核失败：
         *      1.share加入了缓存这就出现数据不一致的情况「没被审核通过，你却加入了缓存让用户可访问」
         *      2.用户中心微服务积分增加了,但是内容中心的内容其实被回滚了没被审核通过
         *  处理方法：
         *      1.发送rocket半消息 2.利用 RocketMQTransactionListener 进行本地事务的确认「确认的方式就看成功会有事务日志」 3.确认成功在让消费主要处理加积分，积分日志
         */

        // 如果是PASS 需要加积分「加积分是个异步操作 有效缩短接口响应耗时」
        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 「 RocketNo.1 发送半消息 」 String txProducerGroup{名称}, String destination{topic}, Message<?> message{msg}, Object arg{}
            String transactionId = UUID.randomUUID().toString();
            this.rocketMQTemplate.sendMessageInTransaction(
                    "tx-add-bonus-group",
                    "add-bonus",
                    // message消息体
                    MessageBuilder.withPayload(UserAddBonusMsgDTO
                            .builder()
                            .userId(share.getUserId())
                            .bonus(50)
                            .build()
                    ).setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build(),
                    // arg 有大用处
                    auditDTO
            );
        } else {
            // 如果不是审核通过不需要发消息
            this.auditByIdInDB(id, auditDTO);
        }
        return share;
    }

    /**
     * 审核操作
     * @param id
     * @param auditDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {
        // 更新审核状态和理由
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();
        this.shareMapper.updateByPrimaryKeySelective(share);

        // share 写入缓存「解决就是这个分布式事务不一致问题」
    }

    /**
     * 审核事务日志记录
     * @param id
     * @param auditDTO
     * @param transactionId
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId){
        this.auditByIdInDB(id, auditDTO);
        // 审核通过记录事务日志用于回查
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .log("审核分享内容日志")
                        .build()
        );
    }

    /**
     * 首页分享内容查询
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     */
    public PageInfo<Share> q(String title, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Share> shares = this.shareMapper.selectByParam(title);
        return new PageInfo<>(shares);
    }

    /**
     * 积分兑换分享内容
     * @param id
     * @return
     */
    public Share exchangeById(Integer id, HttpServletRequest request) {
        Object userId = request.getAttribute("id");
        Integer integerUserId = (Integer) userId;
        // 1.id查询share
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("该分享不存在");
        }
        // 当前用户如果是兑换过了此分享信息则直接返回
        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                MidUserShare.builder()
                        .shareId(id)
                        .userId(integerUserId)
                        .build()
        );
        if (midUserShare != null) { return share; }
        // 分享内容所需积分
        Integer price = share.getPrice();
        // 2.根据当前用户ID查询积分是否充足
        UserDTO userDTO = this.userCenterFeignClient.findById(integerUserId);
        if (share.getPrice() > userDTO.getBonus()) {
            throw new IllegalArgumentException("用户积分不足");
        }
        // 3.扣减积分, 直接用feign玩
        this.userCenterFeignClient.addBonus(
                UserAddBonusDTO.builder()
                    .userId(integerUserId)
                    .bonus(0 - price)
                    .build()
        );
        // 4.往mid_user_share兑换表插入数据,表示已兑换
        this.midUserShareMapper.insertSelective(
                MidUserShare.builder()
                    .userId(integerUserId)
                    .shareId(id)
                .build()
        );
        return share;
    }
}
