package com.xiaolian.amigo.data.network.model.order;

import java.util.List;

import lombok.Data;

/**
 * 网络返回 - 订单
 *
 * @author caidong
 * @date 17/9/15
 */
@Data
public class OrderRespDTO {

    /**
     * 订单总数
     */
    private Integer total;
    /**
     * 订单列表
     */
    private List<OrderInListDTO> orders;
}
