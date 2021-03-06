package com.xiaolian.amigo.data.network.model.trade;

import com.xiaolian.amigo.data.vo.Bonus;

import lombok.Data;

/**
 * 扫描二维码结账
 *
 * @author zcd
 * @date 18/1/17
 */
@Data
public class QrCodeScanRespDTO {
    private String deviceToken;
    private String macAddress;

    private  int deviceType;
    /**
     * 红包
     */
    private Bonus bonus;
    /**
     * 余额
     */
    private Double balance;
}
