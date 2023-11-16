package com.anju.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author ZhongZhen
 * @since 2023-09-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("log")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作内容
     */
    @TableField("name")
    private String name;

    /**
     * 操作时间
     */
    @TableField("time")
    private String time;

    /**
     * 操作人
     */
    @TableField("username")
    private String username;

    /**
     * 操作人ip
     */
    @TableField("ip")
    private String ip;
}
