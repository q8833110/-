package com.xiaolian.amigo.ui.lostandfound;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.manager.intf.ILostAndFoundDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostAndFoundDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListRespDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.lostandfound.adapter.LostAndFoundAdaptor;
import com.xiaolian.amigo.ui.lostandfound.intf.ILostAndFoundPresenter;
import com.xiaolian.amigo.ui.lostandfound.intf.ILostAndFoundView;
import com.xiaolian.amigo.util.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * 失物招领
 *
 * @author zcd
 * @date 17/9/18
 */

public class LostAndFoundPresenter<V extends ILostAndFoundView> extends BasePresenter<V>
        implements ILostAndFoundPresenter<V> {

    private ILostAndFoundDataManager lostAndFoundDataManager;

    @Inject
    LostAndFoundPresenter(ILostAndFoundDataManager lostAndFoundDataManager) {
        super();
        this.lostAndFoundDataManager = lostAndFoundDataManager;
    }

    private void queryLostAndFoundList(Integer page, Integer size, Integer type, String selectKey, boolean isSearch) {
        QueryLostAndFoundListReqDTO dto = new QueryLostAndFoundListReqDTO();
        dto.setPage(page);
        dto.setSelectKey(selectKey);
        dto.setSize(size);
        addObserver(lostAndFoundDataManager.queryLostAndFounds(dto), new NetworkObserver<ApiResult<QueryLostAndFoundListRespDTO>>(false, true) {

            @Override
            public void onReady(ApiResult<QueryLostAndFoundListRespDTO> result) {
                getMvpView().setRefreshComplete();
                getMvpView().setLoadMoreComplete();
                getMvpView().hideEmptyView();
                getMvpView().hideErrorView();
                if (null == result.getError()) {

//                    if (null != result.getData().getLostAndFounds()) {
//                        List<LostAndFoundAdaptor.LostAndFoundWapper> wrappers = new ArrayList<>();
//                        for (LostAndFoundDTO lost : result.getData().getLostAndFounds()) {
//                            wrappers.add(new LostAndFoundAdaptor.LostAndFoundWapper(lost.transform()));
//                        }
//                        if (isSearch) {
//                            if (wrappers.isEmpty()) {
//                                getMvpView().showNoSearchResult(selectKey);
//                            } else {
//                                getMvpView().showSearchResult(wrappers);
//                            }
//                        } else {
//                            if (wrappers.isEmpty() && page == Constant.PAGE_START_NUM) {
//                                getMvpView().showEmptyView(R.string.empty_tip_1);
//                                return;
//                            }
//                            getMvpView().hideEmptyView();
//                            if (type == null) {
//                                getMvpView().addMore(wrappers);
//                            } else {
//                                if (type == 1) {
//                                    getMvpView().addMoreLost(wrappers);
//                                } else {
//                                    getMvpView().addMoreFound(wrappers);
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    getMvpView().hideErrorView();
//                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getMvpView().setRefreshComplete();
                getMvpView().setLoadMoreComplete();
                getMvpView().showErrorView();
            }
        });
    }

    @Override
    public void queryLostAndFoundList(Integer page, Integer size, Integer type, String selectKey) {
        queryLostAndFoundList(page, size, type, selectKey, false);
    }

    @Override
    public void queryLostList(Integer page, Integer size) {
        // type 1 表示失物
        queryLostAndFoundList(page, size, 1, null);
    }

    @Override
    public void queryFoundList(Integer page, Integer size) {
        // type 2 表示招领
        queryLostAndFoundList(page, size, 2, null);
    }

    @Override
    public void searchLostList(Integer page, Integer size, String selectKey) {
        // type 1 表示失物
        queryLostAndFoundList(page, size, 1, selectKey, true);
    }

    @Override
    public void searchFoundList(Integer page, Integer size, String selectKey) {
        // type 2 表示招领
        queryLostAndFoundList(page, size, 2, selectKey, true);
    }

    @Override
    public void getMyLostAndFounds() {
        addObserver(lostAndFoundDataManager.getMyLostAndFounds(), new NetworkObserver<ApiResult<QueryLostAndFoundListRespDTO>>(false, true) {

            @Override
            public void onReady(ApiResult<QueryLostAndFoundListRespDTO> result) {
                getMvpView().setRefreshComplete();
                getMvpView().setLoadMoreComplete();
                getMvpView().hideEmptyView();
                getMvpView().hideErrorView();
                if (null == result.getError()) {
//                    if (result.getData().getLostAndFounds() != null && result.getData().getLostAndFounds().size() > 0) {
//                        List<LostAndFoundAdaptor.LostAndFoundWapper> wrappers = new ArrayList<>();
//                        for (LostAndFoundDTO lostAndFound : result.getData().getLostAndFounds()) {
//                            wrappers.add(new LostAndFoundAdaptor.LostAndFoundWapper(lostAndFound.transform()));
//                        }
//                        getMvpView().addMore(wrappers);
//                    } else {
//                        getMvpView().showEmptyView(R.string.empty_tip_1);
//                        getMvpView().addMore(new ArrayList<>());
//                    }
//                } else {
//                    getMvpView().addMore(new ArrayList<>());
//                    getMvpView().onError(result.getError().getDisplayMessage());
//                    getMvpView().showErrorView();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getMvpView().addMore(new ArrayList<>());
                getMvpView().setRefreshComplete();
                getMvpView().setLoadMoreComplete();
                getMvpView().showErrorView();
            }
        });
    }

    @Override
    public void deleteLostAndFounds(Long id) {
        SimpleReqDTO reqDTO = new SimpleReqDTO();
        reqDTO.setId(id);
        addObserver(lostAndFoundDataManager.deleteLostAndFounds(reqDTO), new NetworkObserver<ApiResult<BooleanRespDTO>>() {

            @Override
            public void onReady(ApiResult<BooleanRespDTO> result) {
                if (null == result.getError()) {
                    getMvpView().onSuccess("删除成功");
                    getMvpView().refresh();
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

}
