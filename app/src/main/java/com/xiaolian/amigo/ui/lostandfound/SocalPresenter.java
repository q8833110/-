package com.xiaolian.amigo.ui.lostandfound;

import android.text.TextUtils;

import com.xiaolian.amigo.data.enumeration.LostAndFound;
import com.xiaolian.amigo.data.manager.intf.ILostAndFoundDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.lostandfound.BbsTopicListTradeRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.CommonRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LikeItemReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostAndFoundDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.NoticeCountDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostFoundDetailReqDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.lostandfound.intf.ISocalPresenter;
import com.xiaolian.amigo.ui.lostandfound.intf.ISocalView;
import com.xiaolian.amigo.util.Log;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SocalPresenter <V extends ISocalView> extends BasePresenter<V>
        implements ISocalPresenter<V> {


    private ILostAndFoundDataManager lostAndFoundDataManager ;


    private boolean commentEnable = false;

    private int noticeCount = 0;

    @Inject
    SocalPresenter(ILostAndFoundDataManager lostAndFoundDataManager) {
        super();
        this.lostAndFoundDataManager = lostAndFoundDataManager;
    }


    @Override
    public void getTopicList() {
        addObserver(lostAndFoundDataManager.getTopicList(), new NetworkObserver<ApiResult<BbsTopicListTradeRespDTO>>(){
            @Override
            public void onStart() {
                getMvpView().showBlogLoading();
                getMvpView().hideErrorLayout();
                getMvpView().hideTagLayout();
            }

            @Override
            public void onReady(ApiResult<BbsTopicListTradeRespDTO> result) {
                getMvpView().hideBlogLoading();
                getMvpView().showTagLayout();
                if (result.getError() == null){
                    if (result.getData().getTopicList() != null && result.getData().getTopicList().size() > 0) {
                        lostAndFoundDataManager.setTopic(result.getData().getTopicList());
                        getMvpView().referTopic(result.getData());
                    }

                }else{
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getMvpView().hideBlogLoading();
                getMvpView().hideTagLayout();
                getMvpView().showErrorLayout();
            }
        });
    }

    @Override
    public void getLostList(String hotPosIds , int page , String selectKey , int topicId) {
        QueryLostAndFoundListReqDTO reqDTO = new QueryLostAndFoundListReqDTO();
        if (!TextUtils.isEmpty(hotPosIds)){
            reqDTO.setHotPostIds(hotPosIds);
        }
        if (page != 0){
            reqDTO.setPage(page);
        }

        if (!TextUtils.isEmpty(selectKey)){
            reqDTO.setSelectKey(selectKey);
        }

        if (topicId != 0){
            reqDTO.setTopicId((long) topicId);
        }

        addObserver(lostAndFoundDataManager.queryLostAndFounds(reqDTO) , new NetworkObserver<ApiResult<QueryLostAndFoundListRespDTO>>(){

            @Override
            public void onStart() {

            }

            @Override
            public void onReady(ApiResult<QueryLostAndFoundListRespDTO> result) {
                if (result.getError() == null) {
                    commentEnable = result.getData().getCommentEnable() ;
                    fetchNoticeCount();
                    if (!TextUtils.isEmpty(selectKey)) {
                        if (result.getData().getPosts() == null || result.getData().getPosts().size() == 0) {
                            getMvpView().showNoSearchResult(selectKey);
                        } else {
                            getMvpView().showSearchResult(result.getData().getPosts());
                        }
                    }
                }else{
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

    }

    private void likeOrUnLikeCommentOrContent(int position, long id, boolean comment, boolean like) {
        LikeItemReqDTO reqDTO = new LikeItemReqDTO();
        reqDTO.setItemId(id);
        // 是否是点赞，1 点赞 2 取消点赞
        reqDTO.setLike(like ? 1 : 2);
        // 被点赞/取消点赞的类型，1 联子 2 评论 3 回复
        reqDTO.setType(1);
        addObserver(lostAndFoundDataManager.like(reqDTO),
                new NetworkObserver<ApiResult<CommonRespDTO>>(false) {

                    @Override
                    public void onReady(ApiResult<CommonRespDTO> result) {
                        if (null == result.getError()) {
                               if (like){
                                   getMvpView().notifyAdapter(position , true , true);
                               }else{
                                   getMvpView().notifyAdapter(position , false , true );
                               }
                        } else {
                            getMvpView().onError(result.getError().getDisplayMessage());
                        }
                    }
                });

    }

    @Override
    public void unLikeComment(int position ,long id) {
        likeOrUnLikeCommentOrContent(position, id, true, false);
    }

    @Override
    public void likeComment(int position, long id) {
        likeOrUnLikeCommentOrContent(position, id, true, true);
    }

    @Override
    public void unLikeComment(int position, long id, boolean isDialog) {
        likeOrUnLikeCommentOrContent(position, id, false, false);
    }

    @Override
    public void likeComment(int position, long id, boolean isDialog) {
        likeOrUnLikeCommentOrContent(position, id, false, false);
    }


    @Override
    public boolean isCommentEnable() {
        return commentEnable;
    }

    @Override
    public int getNoticeCount() {
        return noticeCount;
    }

    @Override
    public void fetchNoticeCount() {
        addObserver(lostAndFoundDataManager.noticeCount(),
                new NetworkObserver<ApiResult<NoticeCountDTO>>(false) {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onReady(ApiResult<NoticeCountDTO> result) {
                        if (null == result.getError()) {
                            noticeCount = result.getData().getNoticeCount();
                            if (noticeCount != 0) {
                                getMvpView().showNoticeRemind(noticeCount);
                            } else {
                                getMvpView().hideNoticeRemind();
                            }
                        }
                    }
                });
    }



    @Override
    public boolean getIsFirstAfterLogin() {
        return lostAndFoundDataManager.getIsFirstAfterLogin();
    }

    @Override
    public void setIsFirstAfterLogin(boolean b) {
         lostAndFoundDataManager.setIsFirstAfterLogin(b);
    }

    @Override
    public void setNoticeCount(int i) {
        this.noticeCount = i ;
    }
}
