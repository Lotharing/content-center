package top.lothar.contentcenter.dao.notices;

import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import top.lothar.contentcenter.domain.entity.notices.Notice;

@Repository
public interface NoticeMapper extends Mapper<Notice> {
}