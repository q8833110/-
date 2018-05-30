package com.xiaolian.amigo.data.network;

import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.lostandfound.DeleteLostFoundItemReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostAndFoundDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostFoundCommentsListDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostFoundRepliesListDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostFoundCommentsReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostFoundDetailReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostFoundRepliesReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.SaveLostAndFoundDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleReqDTO;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListRespDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.SaveLostAndFoundReportDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.SaveLostFoundCommentsRepliesDTO;

import rx.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 失物招领
 *
 * @author zcd
 * @date 17/9/18
 */
public interface ILostAndFoundApi {
    /**
     * 获取失物招领列表
     */
    @POST("lost/list")
    Observable<ApiResult<QueryLostAndFoundListRespDTO>> queryLostAndFounds(@Body QueryLostAndFoundListReqDTO reqDTO);

    /**
     * 保存失物招领
     */
    @POST("lost/add_new")
    Observable<ApiResult<SimpleRespDTO>> saveLostAndFounds(@Body SaveLostAndFoundDTO reqDTO);

    /**
     * 获取失物招领详情
     */
    @POST("lost/one")
    Observable<ApiResult<LostAndFoundDTO>> getLostAndFound(@Body QueryLostFoundDetailReqDTO reqDTO);

    /**
     * 我的失物招领
     */
    @POST("lost/personal/list_new")
    Observable<ApiResult<QueryLostAndFoundListRespDTO>> getMyLostAndFounds();

    /**
     * 更新失物招领
     */
    @POST("lost/update_update")
    Observable<ApiResult<SimpleRespDTO>> updateLostAndFounds(@Body SaveLostAndFoundDTO reqDTO);

    /**
     * 删除失物招领
     */
    @POST("lost/delete_delete")
    Observable<ApiResult<BooleanRespDTO>> deleteLostAndFounds(@Body SimpleReqDTO reqDTO);

    /**
     * 获取评论
     */
    @POST("lost/commentsList")
    Observable<ApiResult<LostFoundCommentsListDTO>> getCommentList(@Body QueryLostFoundCommentsReqDTO reqDTO);

    /**
     * 发布评论/回复
     */
    @POST("lost/addCommentsReplies")
    Observable<ApiResult<SimpleRespDTO>> publishCommentOrReply(@Body SaveLostFoundCommentsRepliesDTO reqDTO);

    /**
     * 失物招领回复列表
     */
    @POST("lost/repliesList")
    Observable<ApiResult<LostFoundRepliesListDTO>> getReplies(@Body QueryLostFoundRepliesReqDTO reqDTO);

    /**
     * 举报
     */
    @POST("lost/report")
    Observable<ApiResult<SimpleRespDTO>> report(@Body SaveLostAndFoundReportDTO reqDTO);

    /**
     * 删除失物招领／评论／回复
     */
    @POST("lost/delete_new")
    Observable<ApiResult<BooleanRespDTO>> delete(@Body DeleteLostFoundItemReqDTO reqDTO);

}
