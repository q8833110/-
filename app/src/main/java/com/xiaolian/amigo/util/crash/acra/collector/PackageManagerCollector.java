/*
 *  Copyright 2016
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaolian.amigo.util.crash.acra.collector;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;

import com.xiaolian.amigo.util.crash.acra.ACRAConstants;
import com.xiaolian.amigo.util.crash.acra.ReportField;
import com.xiaolian.amigo.util.crash.acra.builder.ReportBuilder;
import com.xiaolian.amigo.util.crash.acra.model.Element;
import com.xiaolian.amigo.util.crash.acra.model.NumberElement;
import com.xiaolian.amigo.util.crash.acra.model.StringElement;
import com.xiaolian.amigo.util.crash.acra.util.PackageManagerWrapper;

/**
 * Collects PackageInfo values
 *
 * @author F43nd1r
 * @since 4.9.1
 */
final class PackageManagerCollector extends Collector {
    private final PackageManagerWrapper pm;

    PackageManagerCollector(PackageManagerWrapper pm) {
        super(ReportField.APP_VERSION_NAME, ReportField.APP_VERSION_CODE);
        this.pm = pm;
    }

    @NonNull
    @Override
    Element collect(ReportField reportField, ReportBuilder reportBuilder) {
        PackageInfo info = pm.getPackageInfo();
        if (info != null) {
            switch (reportField) {
                case APP_VERSION_NAME:
                    return new StringElement(info.versionName);
                case APP_VERSION_CODE:
                    return new NumberElement(info.versionCode);
            }
        }
        return ACRAConstants.NOT_AVAILABLE;
    }
}