package com.xiaolian.amigo.data.network.model.bathroom;

import java.util.List;

import lombok.Data;

/**
 *
 * @author zcd
 * @date 18/7/4
 */
@Data
public class BathBuildingRespDTO {
    private List<BathFloorDTO> floors;
    private Integer missTimes;
    /**
     * 该学校公共浴室支持的交易方式
     * 预约使用 1
     * 购买编码 2
     * 扫一扫 3
     * 直接使用 4
     */
    private List<Integer> methods;
}