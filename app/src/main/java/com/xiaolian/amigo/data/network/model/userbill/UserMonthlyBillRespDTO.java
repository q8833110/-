package com.xiaolian.amigo.data.network.model.userbill;

import java.util.List;

import lombok.Data;

/**
 * @author zcd
 * @date 18/6/4
 */
@Data
public class UserMonthlyBillRespDTO {
    /**
     * 消费类型列表
     */
    private List<UserMonthlyBillConsumeRespDTO> consumeTypes;
    /**
     * 单笔消费最贵
     */
    private Double maxConsume;
    /**
     * 消费总额
     */
    private Double totalConsume;
    /**
     * 充值总额
     */
    private Double totalRecharge;
    /**
     * 提现总额
     */
    private Double totalWithdraw;

    /*消费退款*/
    private Double refunds;
}
