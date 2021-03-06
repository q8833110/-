package com.xiaolian.amigo.data.network.model.school;

import com.xiaolian.amigo.data.network.model.user.BriefSchoolBusiness;

import java.util.List;

import lombok.Data;

/**
 * 学校业务列表DTO
 *
 * @author zcd
 * @date 17/9/15
 */
@Data
public class QuerySchoolBizListRespDTO {
    private List<BriefSchoolBusiness> businesses;
}
