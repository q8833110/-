package com.xiaolian.amigo.data.network.model.user;

import lombok.Data;

/**
 * 建筑物模型
 * <p>
 * Created by zcd on 9/19/17.
 */
@Data
public class Residence {
    private Long id;
    private String name;
    private String fullName;
    private String macAddress;
}
