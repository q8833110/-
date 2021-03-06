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

import com.xiaolian.amigo.data.manager.WasherDataManager;
import com.xiaolian.amigo.data.manager.intf.IWasherDataManager;
import com.xiaolian.amigo.di.WasherActivityContext;
import com.xiaolian.amigo.ui.device.washer.ChooseWashModePresenter;
import com.xiaolian.amigo.ui.device.washer.ScanPresenter;
import com.xiaolian.amigo.ui.device.washer.WasherPresenter;
import com.xiaolian.amigo.ui.device.washer.WasherQrCodePresenter;
import com.xiaolian.amigo.ui.device.washer.intf.IChooseWashModePresenter;
import com.xiaolian.amigo.ui.device.washer.intf.IChooseWashModeView;
import com.xiaolian.amigo.ui.device.washer.intf.IScanPresenter;
import com.xiaolian.amigo.ui.device.washer.intf.IScanView;
import com.xiaolian.amigo.ui.device.washer.intf.IWasherPresenter;
import com.xiaolian.amigo.ui.device.washer.intf.IWasherQrCodePresenter;
import com.xiaolian.amigo.ui.device.washer.intf.IWasherQrCodeView;
import com.xiaolian.amigo.ui.device.washer.intf.IWasherView;

import dagger.Module;
import dagger.Provides;

@Module
public class WasherActivityModule {

    private AppCompatActivity mActivity;

    public WasherActivityModule(AppCompatActivity activity) {
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
    @WasherActivityContext
    IWasherPresenter<IWasherView> provideWasherPresenter(
            WasherPresenter<IWasherView> presenter) {
        return presenter;
    }

    @Provides
    @WasherActivityContext
    IChooseWashModePresenter<IChooseWashModeView> provideChooseWashModePresenter(
            ChooseWashModePresenter<IChooseWashModeView> presenter) {
        return presenter;
    }

    @Provides
    @WasherActivityContext
    IScanPresenter<IScanView> provideScanPresenter(
            ScanPresenter<IScanView> presenter) {
        return presenter;
    }

    @Provides
    @WasherActivityContext
    IWasherQrCodePresenter<IWasherQrCodeView> provideWasherQRCodePresenter(
            WasherQrCodePresenter<IWasherQrCodeView> presenter) {
        return presenter;
    }

    @Provides
    IWasherDataManager provideWasherDataManager(WasherDataManager manager) {
        return manager;
    }

}
