package top.lothar.contentcenter.dao.messaging;

import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import top.lothar.contentcenter.domain.entity.messaging.RocketmqTransactionLog;

@Repository
public interface RocketmqTransactionLogMapper extends Mapper<RocketmqTransactionLog> {
}