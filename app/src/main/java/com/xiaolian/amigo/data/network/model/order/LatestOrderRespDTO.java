package com.xiaolian.amigo.data.network.model.order;

import lombok.Data;

/**
 * Created by caidong on 2017/10/15.
 */
@Data
public class LatestOrderRespDTO {

    // 订单id
    private Long orderId;

    // 订单产生时间
    private Long finishTime;

}