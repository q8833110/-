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

package com.xiaolian.amigo.ui.base.intf;


import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.data.network.model.Error;

import rx.Observable;
import rx.Scheduler;

/**
 * @author caidong
 * @date 17/9/15
 */
public interface IBasePresenter<V extends IBaseView> {

    /**
     * 绑定视图
     */
    void onAttach(V mvpView);

    /**
     * 解绑视图
     */
    void onDetach();

    /**
     * 处理http远程调用异常
     */
    void onRemoteInvocationError(Throwable e);

    /**
     * 处理业务层面异常
     */
    void onBizCodeError(Error error);

    /**
     * 添加网络请求观察者
     */
    <P> void addObserver(Observable<P> observable, BasePresenter.NetworkObserver observer);

    /**
     * 添加网络请求观察者，指定订阅线程
     */
    <P> void addObserver(Observable<P> observable, BasePresenter.NetworkObserver observer, Scheduler scheduler);

    /**
     * 清空观察者列表
     */
    void clearObservers();



}
