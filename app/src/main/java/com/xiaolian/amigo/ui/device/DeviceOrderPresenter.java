package com.xiaolian.amigo.ui.device;

import com.xiaolian.amigo.data.manager.intf.IOrderDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.OrderDetailReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.OrderDetailRespDTO;
import com.xiaolian.amigo.data.prefs.ISharedPreferencesHelp;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.device.intf.IDeviceOrderPresenter;
import com.xiaolian.amigo.ui.device.intf.IDeviceOrderView;

import javax.inject.Inject;

public class DeviceOrderPresenter<V extends IDeviceOrderView> extends BasePresenter<V>
        implements IDeviceOrderPresenter<V> {

    private static final String TAG = DeviceOrderPresenter.class.getSimpleName();
    private IOrderDataManager manager;
    private ISharedPreferencesHelp sharedPreferencesHelp;

    @Inject
    public DeviceOrderPresenter(IOrderDataManager manager,
                                ISharedPreferencesHelp sharedPreferencesHelp) {
        super();
        this.manager = manager;
        this.sharedPreferencesHelp = sharedPreferencesHelp;
    }

    @Override
    public void onLoad(long orderId) {
        OrderDetailReqDTO reqDTO = new OrderDetailReqDTO();
        reqDTO.setId(orderId);

        addObserver(manager.queryOrderDetail(reqDTO), new NetworkObserver<ApiResult<OrderDetailRespDTO>>() {
            @Override
            public void onReady(ApiResult<OrderDetailRespDTO> result) {
                if (null == result.getError()) {
                    getMvpView().setRefreshComplete(result.getData());
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    @Override
    public String getToken() {
        return sharedPreferencesHelp.getToken();
    }
}
