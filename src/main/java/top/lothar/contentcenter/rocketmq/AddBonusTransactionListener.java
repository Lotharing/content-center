package top.lothar.contentcenter.rocketmq;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import top.lothar.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import top.lothar.contentcenter.domain.dto.content.ShareAuditDTO;
import top.lothar.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import top.lothar.contentcenter.service.content.ShareService;

/**
 * <h1>积分 rocket本地事务消息处理</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/1 17:48
 *
 * {@link RocketMQTransactionListener txProducerGroup "对应发送事务消息的名称 txProducerGroup属性"}
 *
 * 描述：shareService 已经把半消息发送给MQ server , 「 RocketNo.2 在这里进行发送成功确认 」 「 RocketNo.3 内部执行生产者本地事务」
 */
@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private ShareService shareService;

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    /**
     * @param message 生产者的消息
     * @param args   {@link ShareAuditDTO}
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object args) {
        MessageHeaders headers = message.getHeaders();
        // 事务消息中存储的 transactionId
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer shareId = Integer.valueOf((String) headers.get("share_id"));
        try {
            // 审核操作 {属于在rocket发送半消息之后进行一个本地事务的处理, 然后才能根据这个事务的执行情况进行下一步是否二次确认 让消费者去消费}
            shareService.auditByIdWithRocketMqLog(shareId, (ShareAuditDTO) args, transactionId);
            // RocketNo.4 二次确认 代表本地事务已经提交了, 然后提交Commit信息 消费者可以进行消费
            //「如果网络问题等因素, 没有进行事务二次确认, rocket会定时的去请求内容中心让告诉下本地事务状态是什么 也就是下边的 checkLocalTransaction 方法」
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            // 本地事务失败进行回滚
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 本地事务检查 「MQ没有收到本地事务的确认，用这个方法去检查事务是否成功」
     * rocket会进行心跳检查, 比如上边COMMIT阶段进程被kill了, 那么我在启动内容中心时候,会执行「不定期」 checkLocalTransaction 进行确认 确保本地事务成功,rocket确认可以让消费者消费
     *
     * 牛逼： 分布式事务到此兜底彻底结束    9.12
     *
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();
        // 事务消息中存储的 transactionId
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        // select * from table where transactionId = ?
        RocketmqTransactionLog rocketmqTransactionLog = this.rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (rocketmqTransactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
