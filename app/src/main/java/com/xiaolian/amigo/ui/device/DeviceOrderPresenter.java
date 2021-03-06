package com.xiaolian.amigo.ui.device;

import android.text.TextUtils;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.manager.intf.IOrderDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.common.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.complaint.CheckComplaintReqDTO;
import com.xiaolian.amigo.data.network.model.order.OrderDetailReqDTO;
import com.xiaolian.amigo.data.network.model.order.OrderDetailRespDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.device.intf.IDeviceOrderPresenter;
import com.xiaolian.amigo.ui.device.intf.IDeviceOrderView;

import javax.inject.Inject;

/**
 * 设备账单
 *
 * @author caidong
 * @date 17/10/11
 */
public class DeviceOrderPresenter<V extends IDeviceOrderView> extends BasePresenter<V>
        implements IDeviceOrderPresenter<V> {

    @SuppressWarnings("unused")
    private static final String TAG = DeviceOrderPresenter.class.getSimpleName();
    private IOrderDataManager orderDataManager;

    @Inject
    DeviceOrderPresenter(IOrderDataManager orderDataManager) {
        super();
        this.orderDataManager = orderDataManager;
    }

    @Override
    public void onLoad(long orderId) {
        OrderDetailReqDTO reqDTO = new OrderDetailReqDTO();
        reqDTO.setId(orderId);

        addObserver(orderDataManager.queryOrderDetail(reqDTO), new NetworkObserver<ApiResult<OrderDetailRespDTO>>() {
            @Override
            public void onReady(ApiResult<OrderDetailRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().getLowest() != null
                            && result.getData().getLowest()
                            && TextUtils.isEmpty(result.getData().getBonus())) {
                        getMvpView().showNoUseTip();
                    }
                    getMvpView().setRefreshComplete(result.getData());
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    @Override
    public void checkComplaint(Long orderId, Integer orderType) {
        CheckComplaintReqDTO reqDTO = new CheckComplaintReqDTO();
        reqDTO.setOrderId(orderId);
        reqDTO.setOrderType(orderType);
        addObserver(orderDataManager.checkComplaint(reqDTO), new NetworkObserver<ApiResult<BooleanRespDTO>>() {

            @Override
            public void onReady(ApiResult<BooleanRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().isResult()) {
                        getMvpView().onError(R.string.complaint_error);
                    } else {
                        getMvpView().toComplaint();
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    @Override
    public String getAccessToken() {
        return orderDataManager.getAccessToken();
    }

    @Override
    public String getRefreshToken() {
        return orderDataManager.getRefreshToken();
    }
}
