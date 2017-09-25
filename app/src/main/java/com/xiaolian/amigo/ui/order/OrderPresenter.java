/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.xiaolian.amigo.ui.order;


import com.xiaolian.amigo.data.manager.intf.IOrderDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.OrderReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.OrderRespDTO;
import com.xiaolian.amigo.data.network.model.order.Order;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.order.adaptor.OrderAdaptor;
import com.xiaolian.amigo.ui.order.intf.IOrderPresenter;
import com.xiaolian.amigo.ui.order.intf.IOrderView;
import com.xiaolian.amigo.util.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OrderPresenter<V extends IOrderView> extends BasePresenter<V>
        implements IOrderPresenter<V> {

    private static final String TAG = OrderPresenter.class.getSimpleName();
    private IOrderDataManager manager;

    @Inject
    public OrderPresenter(IOrderDataManager manager) {
        super();
        this.manager = manager;
    }


    @Override
    public void requestOrders(int page) {
        OrderReqDTO reqDTO = new OrderReqDTO();
        reqDTO.setPage(page);
        reqDTO.setSize(Constant.PAGE_SIZE);
        // 查看已结束账单
        reqDTO.setOrderStatus(2);
        addObserver(manager.queryOrders(reqDTO), new NetworkObserver<ApiResult<OrderRespDTO>>() {
            @Override
            public void onReady(ApiResult<OrderRespDTO> result) {
                getMvpView().setRefreshing(false);
                getMvpView().setLoadMoreComplete();
                if (null == result.getError()) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        getMvpView().addMore(result.getData().getOrders().stream().
//                                map(OrderAdaptor.OrderWrapper::new).collect(Collectors.toList()));
//                    }
                    List<OrderAdaptor.OrderWrapper> wrappers = new ArrayList<OrderAdaptor.OrderWrapper>();
                    if (null != result.getData().getOrders() && result.getData().getOrders().size() > 0) {
                        for (Order order : result.getData().getOrders()) {
                            wrappers.add(new OrderAdaptor.OrderWrapper(order));
                        }
                        getMvpView().addMore(wrappers);
                        getMvpView().addPage();
                    }
                }
            }
        });
    }
}
