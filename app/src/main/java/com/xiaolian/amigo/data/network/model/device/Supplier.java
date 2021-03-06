package com.xiaolian.amigo.data.network.model.device;

import lombok.Data;

/**
 * 供应商
 *
 * @author zcd
 * @date 17/12/18
 */
@Data
public class Supplier {
    private Integer agreement;
    private Long id;
    private String notifyUuid;
    private String serviceUuid;
    private String writeUuid;
}
