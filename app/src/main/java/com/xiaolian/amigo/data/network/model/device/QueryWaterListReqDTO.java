package com.xiaolian.amigo.data.network.model.device;

import java.util.List;

import lombok.Data;

/**
 * @author zcd
 * @date 17/12/15
 */
@Data
public class QueryWaterListReqDTO {
    private List<String> macAddresses;
}
