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
 * 审核订单关系表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Getter
@Setter
@TableName("rent_audit_order")
public class RentAuditOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 审核id
     */
    @TableField("audit_id")
    private Integer auditId;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer orderId;
}
