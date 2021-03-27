package top.lothar.contentcenter.domain.entity.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lothar.contentcenter.dao.content.ShareMapper;

import java.util.Date;
import java.util.List;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/22 22:59
 */
@RestController
public class TestController {

    @Autowired
    private ShareMapper shareMapper;

    @GetMapping("/test")
    public List<Share> testInsert(){
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("xxx");
        share.setCover("xxx");
        share.setAuthor("路通");
        share.setBuyCount(1);
        this.shareMapper.insertSelective(share);
        // select * from share;
        // 快捷键option + enter 生成返回类型本地变量
        List<Share> shares = this.shareMapper.selectAll();
        return shares;
    }

}
