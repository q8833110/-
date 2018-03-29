package com.xiaolian.amigo.data.network;

import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.school.QueryBriefSchoolListRespDTO;
import com.xiaolian.amigo.data.network.model.school.QuerySchoolBizListRespDTO;
import com.xiaolian.amigo.data.network.model.school.QuerySchoolListReqDTO;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 学校相关api
 *
 * @author zcd
 * @date 17/12/14
 */

public interface ISchoolApi {
    /**
     * 获取学校列表
     */
    @POST("school/brief/list")
    Observable<ApiResult<QueryBriefSchoolListRespDTO>> getSchoolList(@Body QuerySchoolListReqDTO body);

    /**
     * 获取学校业务列表
     */
    @POST("school/business/list")
    Observable<ApiResult<QuerySchoolBizListRespDTO>> getSchoolBizList();
}
