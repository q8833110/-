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

package com.xiaolian.amigo.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.xiaolian.amigo.data.manager.BleDataManager;
import com.xiaolian.amigo.data.manager.intf.IBleDataManager;
import com.xiaolian.amigo.di.BleActivityContext;
import com.xiaolian.amigo.ui.ble.BleInteractivePresenter;
import com.xiaolian.amigo.ui.ble.BlePresenter;
import com.xiaolian.amigo.ui.ble.intf.IBleInteractivePresenter;
import com.xiaolian.amigo.ui.ble.intf.IBleInteractiveView;
import com.xiaolian.amigo.ui.ble.intf.IBlePresenter;
import com.xiaolian.amigo.ui.ble.intf.IBleView;

import dagger.Module;
import dagger.Provides;

@Module
public class BleActivityModule {

    private AppCompatActivity mActivity;

    public BleActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    @BleActivityContext
    IBleDataManager provideBleDataManager(BleDataManager manager) {
        return manager;
    }

    @Provides
    @BleActivityContext
    IBlePresenter<IBleView> provideBlePresenter(BlePresenter<IBleView> presenter) {
        return presenter;
    }

    @Provides
    @BleActivityContext
    IBleInteractivePresenter<IBleInteractiveView> provideBleInterractivePresenter(BleInteractivePresenter<IBleInteractiveView> presenter) {
        return presenter;
    }
}
