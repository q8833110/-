package com.xiaolian.amigo.data.network.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册接口请求DTO
 * @author zcd
 */
@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class RegisterReqDTO {
    private String code;
    private int mobile;
    private String password;
    private int schoolId;

    public RegisterReqDTO(String code, int mobile, String password, int schoolId) {
        this.code = code;
        this.mobile = mobile;
        this.password = password;
        this.schoolId = schoolId;
    }
}
