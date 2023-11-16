package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 房源审核表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-29
 */
@Getter
@Setter
@TableName("house_audit")
public class HouseAudit implements Serializable {

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
     * 审核状态 0 待审核 1 已通过 2 未通过
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核意见
     */
    @TableField("content")
    private String content;
}
