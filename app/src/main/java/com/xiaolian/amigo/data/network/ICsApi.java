package com.xiaolian.amigo.data.network;

import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.cs.RemindReqDTO;
import com.xiaolian.amigo.data.network.model.cs.CsMobileRespDTO;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 客服相关api
 *
 * @author zcd
 * @date 17/12/14
 */

public interface ICsApi {
    /**
     * 提醒客服
     */
    @POST("cs/remind")
    Observable<ApiResult<BooleanRespDTO>> remind(@Body RemindReqDTO reqDTO);

    /**
     * 获取客服人员电话
     */
    @POST("cs/mobile/one")
    Observable<ApiResult<CsMobileRespDTO>> queryCsInfo();
}
