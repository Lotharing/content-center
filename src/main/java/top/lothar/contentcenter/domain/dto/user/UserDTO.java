package top.lothar.contentcenter.domain.dto.user;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * <h1></h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/3/26 17:07
 */
@Data
public class UserDTO {
    /**
     * Id
     */
    private Integer id;

    /**
     * 微信id
     */
    private String wxId;

    /**
     * 微信昵称
     */
    private String wxNickname;

    /**
     * 角色
     */
    private String roles;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 积分
     */
    private Integer bonus;
}
