/*
 *  Copyright 2012 Kevin Gaudin
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

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.xiaolian.amigo.util.crash.acra.ACRA;
import com.xiaolian.amigo.util.crash.acra.ACRAConstants;
import com.xiaolian.amigo.util.crash.acra.ReportField;
import com.xiaolian.amigo.util.crash.acra.builder.ReportBuilder;
import com.xiaolian.amigo.util.crash.acra.model.ComplexElement;
import com.xiaolian.amigo.util.crash.acra.model.Element;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Collects data about available codecs on the device through the MediaCodecList
 * API introduced in Android 4.1 JellyBean.
 *
 * @author Kevin Gaudin & F43nd1r
 */
final class MediaCodecListCollector extends Collector {

    private enum CodecType {
        AVC, H263, MPEG4, AAC

    }

    private static final String COLOR_FORMAT_PREFIX = "COLOR_";
    private static final String[] MPEG4_TYPES = {"mp4", "mpeg4", "MP4", "MPEG4"};
    private static final String[] AVC_TYPES = {"avc", "h264", "AVC", "H264"};
    private static final String[] H263_TYPES = {"h263", "H263"};
    private static final String[] AAC_TYPES = {"aac", "AAC"};

    private final SparseArray<String> mColorFormatValues = new SparseArray<String>();
    private final SparseArray<String> mAVCLevelValues = new SparseArray<String>();
    private final SparseArray<String> mAVCProfileValues = new SparseArray<String>();
    private final SparseArray<String> mH263LevelValues = new SparseArray<String>();
    private final SparseArray<String> mH263ProfileValues = new SparseArray<String>();
    private final SparseArray<String> mMPEG4LevelValues = new SparseArray<String>();
    private final SparseArray<String> mMPEG4ProfileValues = new SparseArray<String>();
    private final SparseArray<String> mAACProfileValues = new SparseArray<String>();

    MediaCodecListCollector() {
        super(ReportField.MEDIA_CODEC_LIST);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    @Override
    Element collect(ReportField reportField, ReportBuilder reportBuilder) {
        try {
            return collectMediaCodecList();
        } catch (JSONException e) {
            ACRA.log.w("Could not collect media codecs", e);
            return ACRAConstants.NOT_AVAILABLE;
        }
    }

    @Override
    boolean shouldCollect(Set<ReportField> crashReportFields, ReportField collect, ReportBuilder reportBuilder) {
        return super.shouldCollect(crashReportFields, collect, reportBuilder) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * use reflection to prepare field arrays.
     */
    private void prepare() {
        try {
            final Class<?> codecCapabilitiesClass = Class.forName("android.media.MediaCodecInfo$CodecCapabilities");

            // Retrieve list of possible Color Format
            for (Field f : codecCapabilitiesClass.getFields()) {
                if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())
                        && f.getName().startsWith(COLOR_FORMAT_PREFIX)) {
                    mColorFormatValues.put(f.getInt(null), f.getName());
                }
            }

            // Retrieve lists of possible codecs profiles and levels
            final Class<?> codecProfileLevelClass = Class.forName("android.media.MediaCodecInfo$CodecProfileLevel");
            for (Field f : codecProfileLevelClass.getFields()) {
                if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) {
                    if (f.getName().startsWith("AVCLevel")) {
                        mAVCLevelValues.put(f.getInt(null), f.getName());
                    } else if (f.getName().startsWith("AVCProfile")) {
                        mAVCProfileValues.put(f.getInt(null), f.getName());
                    } else if (f.getName().startsWith("H263Level")) {
                        mH263LevelValues.put(f.getInt(null), f.getName());
                    } else if (f.getName().startsWith("H263Profile")) {
                        mH263ProfileValues.put(f.getInt(null), f.getName());
                    } else if (f.getName().startsWith("MPEG4Level")) {
                        mMPEG4LevelValues.put(f.getInt(null), f.getName());
                    } else if (f.getName().startsWith("MPEG4Profile")) {
                        mMPEG4ProfileValues.put(f.getInt(null), f.getName());
                    } else if (f.getName().startsWith("AAC")) {
                        mAACProfileValues.put(f.getInt(null), f.getName());
                    }
                }
            }
        } catch (@NonNull ClassNotFoundException ignored) {
            // NOOP
        } catch (@NonNull SecurityException ignored) {
            // NOOP
        } catch (@NonNull IllegalAccessException ignored) {
            // NOOP
        } catch (@NonNull IllegalArgumentException ignored) {
            // NOOP
        }

    }

    /**
     * Builds an Element describing the list of available codecs on the device
     * with their capabilities (supported Color Formats, Codec Profiles et
     * Levels).
     *
     * @return The media codecs information
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    private Element collectMediaCodecList() throws JSONException {
        prepare();
        final MediaCodecInfo[] infos;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            final int codecCount = MediaCodecList.getCodecCount();
            infos = new MediaCodecInfo[codecCount];
            for (int codecIdx = 0; codecIdx < codecCount; codecIdx++) {
                //noinspection deprecation
                infos[codecIdx] = MediaCodecList.getCodecInfoAt(codecIdx);
            }
        } else {
            infos = new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos();
        }

        final ComplexElement result = new ComplexElement();
        for (int i = 0; i < infos.length; i++) {
            final MediaCodecInfo codecInfo = infos[i];
            JSONObject codec = new JSONObject();
            final String[] supportedTypes = codecInfo.getSupportedTypes();
            codec.put("name", codecInfo.getName())
                    .put("isEncoder", codecInfo.isEncoder());
            JSONObject supportedTypesJson = new JSONObject();
            for (String type : supportedTypes) {
                supportedTypesJson.put(type, collectCapabilitiesForType(codecInfo, type));
            }
            codec.put("supportedTypes", supportedTypesJson);
            result.put(String.valueOf(i), codec);
        }
        return result;
    }

    /**
     * Retrieve capabilities (ColorFormats and CodecProfileLevels) for a
     * specific codec type.
     *
     * @param codecInfo the currently inspected codec
     * @param type      supported type to collect
     * @return the color formats and codec profile levels
     * available for a specific codec type.
     */
    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private JSONObject collectCapabilitiesForType(@NonNull final MediaCodecInfo codecInfo, @NonNull String type) throws JSONException {
        final JSONObject result = new JSONObject();
        final MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);

        // Color Formats
        final int[] colorFormats = codecCapabilities.colorFormats;
        if (colorFormats.length > 0) {
            JSONArray colorFormatsJson = new JSONArray();
            for (int colorFormat : colorFormats) {
                colorFormatsJson.put(mColorFormatValues.get(colorFormat));
            }
            result.put("colorFormats", colorFormatsJson);
        }

        final CodecType codecType = identifyCodecType(codecInfo);

        // Profile Levels
        final MediaCodecInfo.CodecProfileLevel[] codecProfileLevels = codecCapabilities.profileLevels;
        if (codecProfileLevels.length > 0) {
            JSONArray profileLevels = new JSONArray();
            for (MediaCodecInfo.CodecProfileLevel codecProfileLevel : codecProfileLevels) {
                final int profileValue = codecProfileLevel.profile;
                final int levelValue = codecProfileLevel.level;

                if (codecType == null) {
                    // Unknown codec
                    profileLevels.put(profileValue + '-' + levelValue);
                    break;
                }

                switch (codecType) {
                    case AVC:
                        profileLevels.put(profileValue + mAVCProfileValues.get(profileValue)
                                + '-' + mAVCLevelValues.get(levelValue));
                        break;
                    case H263:
                        profileLevels.put(mH263ProfileValues.get(profileValue)
                                + '-' + mH263LevelValues.get(levelValue));
                        break;
                    case MPEG4:
                        profileLevels.put(mMPEG4ProfileValues.get(profileValue)
                                + '-' + mMPEG4LevelValues.get(levelValue));
                        break;
                    case AAC:
                        profileLevels.put(mAACProfileValues.get(profileValue));
                        break;
                    default:
                        break;
                }
            }
            result.put("profileLevels", profileLevels);
        }
        return result;
    }

    /**
     * Looks for keywords in the codec name to identify its nature ({@link CodecType}).
     *
     * @param codecInfo the currently inspected codec
     * @return type of the codec or null if it could bot be guessed
     */
    @Nullable
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private CodecType identifyCodecType(@NonNull MediaCodecInfo codecInfo) {

        final String name = codecInfo.getName();
        for (String token : AVC_TYPES) {
            if (name.contains(token)) {
                return CodecType.AVC;
            }
        }
        for (String token : H263_TYPES) {
            if (name.contains(token)) {
                return CodecType.H263;
            }
        }
        for (String token : MPEG4_TYPES) {
            if (name.contains(token)) {
                return CodecType.MPEG4;
            }
        }
        for (String token : AAC_TYPES) {
            if (name.contains(token)) {
                return CodecType.AAC;
            }
        }

        return null;
    }
}
