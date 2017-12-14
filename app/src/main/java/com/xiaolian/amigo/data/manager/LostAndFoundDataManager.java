package com.xiaolian.amigo.data.manager;

import com.xiaolian.amigo.data.manager.intf.ILostAndFoundDataManager;
import com.xiaolian.amigo.data.network.ILostAndFoundApi;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.dto.request.SimpleReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.SimpleRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostAndFoundDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListReqDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.QueryLostAndFoundListRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.SaveLostAndFoundDTO;
import com.xiaolian.amigo.data.network.model.user.User;
import com.xiaolian.amigo.data.prefs.ISharedPreferencesHelp;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;

/**
 * 失物招领
 * <p>
 * Created by zcd on 9/18/17.
 */

public class LostAndFoundDataManager implements ILostAndFoundDataManager {

    private ILostAndFoundApi lostAndFoundApi;
    private ISharedPreferencesHelp sharedPreferencesHelp;

    @Inject
    public LostAndFoundDataManager(Retrofit retrofit, ISharedPreferencesHelp sharedPreferencesHelp) {
        lostAndFoundApi = retrofit.create(ILostAndFoundApi.class);
        this.sharedPreferencesHelp = sharedPreferencesHelp;
    }

    @Override
    public Observable<ApiResult<QueryLostAndFoundListRespDTO>> queryLostAndFounds(@Body QueryLostAndFoundListReqDTO reqDTO) {
        return lostAndFoundApi.queryLostAndFounds(reqDTO);
    }

    @Override
    public Observable<ApiResult<SimpleRespDTO>> saveLostAndFounds(@Body SaveLostAndFoundDTO reqDTO) {
        return lostAndFoundApi.saveLostAndFounds(reqDTO);
    }

    @Override
    public Observable<ApiResult<LostAndFoundDTO>> getLostAndFound(@Body SimpleReqDTO reqDTO) {
        return lostAndFoundApi.getLostAndFound(reqDTO);
    }

    @Override
    public Observable<ApiResult<QueryLostAndFoundListRespDTO>> getMyLostAndFounds() {
        return lostAndFoundApi.getMyLostAndFounds();
    }

    @Override
    public Observable<ApiResult<SimpleRespDTO>> updateLostAndFounds(@Body SaveLostAndFoundDTO reqDTO) {
        return lostAndFoundApi.updateLostAndFounds(reqDTO);
    }

    @Override
    public Observable<ApiResult<BooleanRespDTO>> deleteLostAndFounds(SimpleReqDTO reqDTO) {
        return lostAndFoundApi.deleteLostAndFounds(reqDTO);
    }

    @Override
    public User getUserInfo() {
        return sharedPreferencesHelp.getUserInfo();
    }
}
