package top.lothar.contentcenter.controller.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lothar.contentcenter.domain.dto.content.ShareAuditDTO;
import top.lothar.contentcenter.domain.entity.content.Share;
import top.lothar.contentcenter.service.content.ShareService;

/**
 * <h1>管理员审核</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/30 18:47
 */
@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {

    @Autowired
    private ShareService shareService;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO shareAuditDTO ){
        //TODO 认证授权
        return this.shareService.auditById(id, shareAuditDTO);
    }

}
