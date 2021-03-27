package top.lothar.contentcenter.controller.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lothar.contentcenter.domain.dto.content.ShareDTO;
import top.lothar.contentcenter.domain.entity.content.Share;
import top.lothar.contentcenter.service.content.ShareService;

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

    @RequestMapping("/{id}")
    public ShareDTO findShareById(@PathVariable Integer id){
        return this.shareService.findShareById(id);
    }


    @RequestMapping("/get")
    public List<ServiceInstance> getDiscovery(){
        return this.discoveryClient.getInstances("user-center");
    }
}
