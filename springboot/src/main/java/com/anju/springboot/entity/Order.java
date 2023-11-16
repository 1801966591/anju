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
 * 租赁订单表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-10-12
 */
@Getter
@Setter
@TableName("rent_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房源ID
     */
    @TableField("house_id")
    private Integer houseId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 租赁人姓名
     */
    @TableField("name")
    private String name;

    /**
     * 编号
     */
    @TableField("no")
    private String no;

    /**
     * 订单支付状态，0 未支付 1 已支付 2 超时未支付 3 已退款 4 已取消订单
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 总价
     */
    @TableField("total_amount")
    private Double totalAmount;

    /**
     * 租期
     */
    @TableField("term")
    private Integer term;

    /**
     * 到期时间
     */
    @TableField("due_time")
    private String dueTime;

    /**
     * 订单创建时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 订单支付时间
     */
    @TableField("payment_time")
    private String paymentTime;

    /**
     * 支付宝交易凭证号
     */
    @TableField("alipay_no")
    private String alipayNo;

    /**
     * 订单类型 1 首次租赁 2 续租
     */
    @TableField("order_type")
    private Integer orderType;

    /**
     * 租赁信息id
     */
    @TableField("rent_id")
    private Integer rentId;

    /**
     * 退款金额
     */
    @TableField("return_money")
    private Double returnMoney;

    /**
     * 订单退款时间
     */
    @TableField("return_time")
    private String returnTime;

}
