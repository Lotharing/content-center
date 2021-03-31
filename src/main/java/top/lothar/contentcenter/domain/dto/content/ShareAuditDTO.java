package top.lothar.contentcenter.domain.dto.content;

import lombok.Data;
import top.lothar.contentcenter.domain.enums.AuditStatusEnum;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/30 18:49
 */
@Data
public class ShareAuditDTO {
    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatusEnum;
    /**
     * 原因
     */
    private String reason;
}
