package top.lothar.contentcenter.dao.content;

import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import top.lothar.contentcenter.domain.entity.content.Share;

@Repository
public interface ShareMapper extends Mapper<Share> {
}