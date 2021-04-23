package top.lothar.contentcenter.dao.content;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import top.lothar.contentcenter.domain.entity.content.Share;

import java.util.List;

@Repository
public interface ShareMapper extends Mapper<Share> {
    /**
     * 查询分享信息
     * @param title
     * @return
     */
    List<Share> selectByParam(@Param("title") String title);
}