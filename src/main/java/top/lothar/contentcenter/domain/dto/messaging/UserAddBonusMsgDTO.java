package top.lothar.contentcenter.domain.dto.messaging;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * <h1>积分消息体</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/31 17:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddBonusMsgDTO {
    /** 用户ID **/
    private Integer userId;
    /** 加的积分 **/
    private Integer bonus;
}
