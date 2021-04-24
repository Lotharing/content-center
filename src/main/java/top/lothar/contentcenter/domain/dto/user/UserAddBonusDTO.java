package top.lothar.contentcenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/23 17:44
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserAddBonusDTO {

    private Integer userId;
    private Integer bonus;

}
