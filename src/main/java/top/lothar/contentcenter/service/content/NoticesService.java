package top.lothar.contentcenter.service.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lothar.contentcenter.dao.notices.NoticeMapper;
import top.lothar.contentcenter.domain.entity.notices.Notice;

/**
 * <h1>通知业务</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/24 22:26
 */
@Service
public class NoticesService {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 查询一个通知
     * @return
     */
    public Notice findNotice(){
        return this.noticeMapper.selectOne(
                Notice.builder()
                    .showFlag(true)
                .build()
        );
    }

}
