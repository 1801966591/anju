package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 租赁审核表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Getter
@Setter
@TableName("rent_audit")
public class RentAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房源id
     */
    @TableField("house_id")
    private Integer houseId;

    /**
     * 租赁人id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 租期
     */
    @TableField("term")
    private Integer term;

    /**
     * 租赁人姓名
     */
    @TableField("name")
    private String name;

    /**
     * 租赁人手机号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 审核状态 0 待确认 1 同意 2 拒绝 3 超时未确认 4 用户已取消申请
     */
    @TableField("audit_status")
    private Integer auditStatus;

    /**
     * 租赁信息id
     */
    @TableField("rent_id")
    private Integer rentId;

    /**
     * 申请类型 1 首次租赁 2 续租
     */
    @TableField("apply_type")
    private Integer applyType;

    /**
     * 拒绝原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 申请时间
     */
    @TableField("apply_time")
    private String applyTime;

    /**
     * 申请时间
     */
    @TableField("update_time")
    private String updateTime;
}
