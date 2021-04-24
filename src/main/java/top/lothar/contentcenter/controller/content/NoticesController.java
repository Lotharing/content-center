package top.lothar.contentcenter.controller.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lothar.contentcenter.domain.entity.notices.Notice;
import top.lothar.contentcenter.service.content.NoticesService;

/**
 * <h1>首页通知接口</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/24 22:23
 */
@RestController
@RequestMapping("/notices")
public class NoticesController {

    @Autowired
    private NoticesService noticesService;

    @GetMapping("/newest")
    public Notice getNotice(@RequestHeader("X-Token")String token){
        return this.noticesService.findNotice();
    }
}
