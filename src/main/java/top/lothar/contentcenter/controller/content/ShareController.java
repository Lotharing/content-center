package top.lothar.contentcenter.controller.content;

import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import top.lothar.contentcenter.auth.CheckLogin;
import top.lothar.contentcenter.domain.dto.content.ShareDTO;
import top.lothar.contentcenter.domain.entity.content.Share;
import top.lothar.contentcenter.service.content.ShareService;
import top.lothar.contentcenter.util.JwtOperator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/26 16:51
 */
@RestController
@RequestMapping("/shares")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private JwtOperator jwtOperator;

    @CheckLogin
    @RequestMapping("/{id}")
    public ShareDTO findShareById(@PathVariable Integer id){
        return this.shareService.findShareById(id);
    }

    /**
     * 首页查询分享接口
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                             @RequestHeader("X-Token")String token){
        // pageSize安全校验
        if (pageSize > 100) { pageSize = 100; }
        Integer userId = null;
        if (StringUtils.isNotBlank(token)) {
            Claims claims = jwtOperator.getClaimsFromToken(token);
            userId = (Integer) claims.get("id");
        }
        return shareService.q(title, pageNo, pageSize, userId);
    }

    /**
     * 积分兑换分享接口
     * @param id
     * @return
     */
    @CheckLogin
    @GetMapping("/exchange/{id}")
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest request){
        return this.shareService.exchangeById(id, request);
    }

    /**
     * 我的投稿
     * @param userId
     * @return
     */
    @GetMapping("/contributions")
    public List<Share> myContributions(@RequestParam("userId")Integer userId){
        return shareService.getMyContributions(userId);
    }

    /** 测试接口 **/


    @RequestMapping("/get")
    public List<ServiceInstance> getDiscovery(){
        return this.discoveryClient.getInstances("user-center");
    }

    @Autowired
    private Source source;

    @GetMapping("/stream")
    public String Stream(){
        this.source.output()
                .send(MessageBuilder
                .withPayload("消息体")
                .build());
        return "success";
    }
}
