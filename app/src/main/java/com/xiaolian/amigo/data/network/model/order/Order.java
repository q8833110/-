package com.xiaolian.amigo.data.network.model.order;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单
 * <p>
 * Created by caidong on 2017/9/15.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private int id;
    // 消费金额
    private Double consume;
    // 创建时间
    private Long createTime;
    // 设备id
    private Long deviceId;
    // 设备编号
    private String deviceNo;
    // 设备类型，1 - 热水器， 2 - 饮水机，3 - 洗衣机，4 - 吹风机，
    private Integer deviceType;
    // 设备位置
    private String location;
    // 订单编号
    private String orderNo;
    // 支付类型，1 - 余额支付， 2 - 红包支付
    private Integer paymentType;
    // 预付金额
    private Double prepay;
    // 学校id
    private Integer schoolId;
    // 学校名称
    private String schoolName;
    // 订单状态， 1 - 使用中， 2 - 已结束
    private Integer status;
    // 用户id
    private Long userId;
    // 用户名
    private String username;
    // 用水量
    private Integer waterUsage;
}
