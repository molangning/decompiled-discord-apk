package org.webrtc;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import com.discord.DiscordMediaCodec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.webrtc.EglBase;
import org.webrtc.EglBase14;
import org.webrtc.VideoEncoderFactory;

/* loaded from: /home/user/temp/discord/apk/extracted/_base.apk/classes6.dex */
public class HardwareVideoEncoderFactory implements VideoEncoderFactory {
    private static final List<String> H264_HW_EXCEPTION_MODELS = Arrays.asList("SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4");
    private static final int PERIODIC_KEY_FRAME_INTERVAL_S = 3600;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "HardwareVideoEncoderFactory";
    private final Predicate<MediaCodecInfo> codecAllowedPredicate;
    private final boolean enableH264HighProfile;
    private final boolean enableIntelVp8Encoder;
    private final EglBase14.Context sharedContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.webrtc.HardwareVideoEncoderFactory$1 */
    /* loaded from: /home/user/temp/discord/apk/extracted/_base.apk/classes6.dex */
    public static /* synthetic */ class C112441 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$VideoCodecMimeType;

        static {
            int[] iArr = new int[VideoCodecMimeType.values().length];
            $SwitchMap$org$webrtc$VideoCodecMimeType = iArr;
            try {
                iArr[VideoCodecMimeType.VP8.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.VP9.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.H264.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.H265.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.AV1.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public HardwareVideoEncoderFactory(EglBase.Context context, boolean z, boolean z2) {
        this(context, z, z2, null);
    }

    private BitrateAdjuster createBitrateAdjuster(VideoCodecMimeType videoCodecMimeType, String str) {
        if (str.startsWith("OMX.Exynos.")) {
            if (videoCodecMimeType == VideoCodecMimeType.VP8) {
                return new DynamicBitrateAdjuster();
            }
            return new FramerateBitrateAdjuster();
        }
        return new BaseBitrateAdjuster();
    }

    private MediaCodecInfo findCodecForType(VideoCodecMimeType videoCodecMimeType) {
        int i = 0;
        while (true) {
            MediaCodecInfo mediaCodecInfo = null;
            if (i >= MediaCodecList.getCodecCount()) {
                return null;
            }
            try {
                mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                Logging.m8409e(TAG, "Cannot retrieve encoder codec info", e);
            }
            if (mediaCodecInfo != null && mediaCodecInfo.isEncoder() && isSupportedCodec(mediaCodecInfo, videoCodecMimeType)) {
                return mediaCodecInfo;
            }
            i++;
        }
    }

    private int getForcedKeyFrameIntervalMs(VideoCodecMimeType videoCodecMimeType, String str) {
        if (videoCodecMimeType == VideoCodecMimeType.VP8 && str.startsWith("OMX.qcom.")) {
            if (Build.VERSION.SDK_INT == 23) {
                return QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS;
            }
            return 15000;
        }
        return 0;
    }

    private boolean isH264HighProfileSupported(MediaCodecInfo mediaCodecInfo) {
        if (this.enableH264HighProfile && Build.VERSION.SDK_INT > 23 && mediaCodecInfo.getName().startsWith("OMX.Exynos.")) {
            return true;
        }
        return false;
    }

    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType) {
        boolean isHardwareAccelerated;
        if (Build.VERSION.SDK_INT >= 29) {
            isHardwareAccelerated = mediaCodecInfo.isHardwareAccelerated();
            return isHardwareAccelerated;
        }
        int i = C112441.$SwitchMap$org$webrtc$VideoCodecMimeType[videoCodecMimeType.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        return false;
                    }
                    return isHardwareSupportedInCurrentSdkH265(mediaCodecInfo);
                }
                return isHardwareSupportedInCurrentSdkH264(mediaCodecInfo);
            }
            return isHardwareSupportedInCurrentSdkVp9(mediaCodecInfo);
        }
        return isHardwareSupportedInCurrentSdkVp8(mediaCodecInfo);
    }

    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo mediaCodecInfo) {
        return DiscordMediaCodec.supportsHardwareEncoding(mediaCodecInfo);
    }

    private boolean isHardwareSupportedInCurrentSdkH265(MediaCodecInfo mediaCodecInfo) {
        if (Build.VERSION.SDK_INT >= 29 && DiscordMediaCodec.supportsHardwareEncoding(mediaCodecInfo)) {
            return true;
        }
        return false;
    }

    private boolean isHardwareSupportedInCurrentSdkVp8(MediaCodecInfo mediaCodecInfo) {
        String name = mediaCodecInfo.getName();
        if (!name.startsWith("OMX.qcom.") && !name.startsWith("OMX.Exynos.") && (!name.startsWith("OMX.Intel.") || !this.enableIntelVp8Encoder)) {
            return false;
        }
        return true;
    }

    private boolean isHardwareSupportedInCurrentSdkVp9(MediaCodecInfo mediaCodecInfo) {
        String name = mediaCodecInfo.getName();
        if ((name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.")) && Build.VERSION.SDK_INT >= 24) {
            return true;
        }
        return false;
    }

    private boolean isMediaCodecAllowed(MediaCodecInfo mediaCodecInfo) {
        Predicate<MediaCodecInfo> predicate = this.codecAllowedPredicate;
        if (predicate == null) {
            return true;
        }
        return predicate.test(mediaCodecInfo);
    }

    private boolean isSupportedCodec(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType) {
        if (!MediaCodecUtils.codecSupportsType(mediaCodecInfo, videoCodecMimeType) || MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, mediaCodecInfo.getCapabilitiesForType(videoCodecMimeType.mimeType())) == null || !isHardwareSupportedInCurrentSdk(mediaCodecInfo, videoCodecMimeType) || !isMediaCodecAllowed(mediaCodecInfo)) {
            return false;
        }
        return true;
    }

    @Override // org.webrtc.VideoEncoderFactory
    public VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo) {
        VideoCodecMimeType valueOf = VideoCodecMimeType.valueOf(videoCodecInfo.getName());
        MediaCodecInfo findCodecForType = findCodecForType(valueOf);
        if (findCodecForType == null) {
            return null;
        }
        String name = findCodecForType.getName();
        String mimeType = valueOf.mimeType();
        Integer selectColorFormat = MediaCodecUtils.selectColorFormat(MediaCodecUtils.TEXTURE_COLOR_FORMATS, findCodecForType.getCapabilitiesForType(mimeType));
        Integer selectColorFormat2 = MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, findCodecForType.getCapabilitiesForType(mimeType));
        if (valueOf == VideoCodecMimeType.H264) {
            boolean isSameH264Profile = H264Utils.isSameH264Profile(videoCodecInfo.params, MediaCodecUtils.getCodecProperties(valueOf, true));
            boolean isSameH264Profile2 = H264Utils.isSameH264Profile(videoCodecInfo.params, MediaCodecUtils.getCodecProperties(valueOf, false));
            if (!isSameH264Profile && !isSameH264Profile2) {
                return null;
            }
            if (isSameH264Profile && !isH264HighProfileSupported(findCodecForType)) {
                return null;
            }
        }
        int forcedKeyFrameIntervalMs = getForcedKeyFrameIntervalMs(valueOf, name);
        if (videoCodecInfo.params.containsKey("discord-fki")) {
            try {
                forcedKeyFrameIntervalMs = Integer.parseInt(videoCodecInfo.params.get("discord-fki"));
            } catch (NumberFormatException unused) {
            }
        }
        return new HardwareVideoEncoder(new MediaCodecWrapperFactoryImpl(), name, valueOf, selectColorFormat, selectColorFormat2, videoCodecInfo.params, PERIODIC_KEY_FRAME_INTERVAL_S, forcedKeyFrameIntervalMs, createBitrateAdjuster(valueOf, name), this.sharedContext);
    }

    @Override // org.webrtc.VideoEncoderFactory
    public /* synthetic */ VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return C11313j1.m8296a(this);
    }

    @Override // org.webrtc.VideoEncoderFactory
    public /* synthetic */ VideoCodecInfo[] getImplementations() {
        return C11313j1.m8295b(this);
    }

    @Override // org.webrtc.VideoEncoderFactory
    public VideoCodecInfo[] getSupportedCodecs() {
        ArrayList arrayList = new ArrayList();
        VideoCodecMimeType[] videoCodecMimeTypeArr = {VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264, VideoCodecMimeType.H265, VideoCodecMimeType.AV1};
        for (int i = 0; i < 5; i++) {
            VideoCodecMimeType videoCodecMimeType = videoCodecMimeTypeArr[i];
            MediaCodecInfo findCodecForType = findCodecForType(videoCodecMimeType);
            if (findCodecForType != null) {
                String name = videoCodecMimeType.name();
                if (videoCodecMimeType == VideoCodecMimeType.H264 && isH264HighProfileSupported(findCodecForType)) {
                    arrayList.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(videoCodecMimeType, true)));
                }
                arrayList.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(videoCodecMimeType, false)));
            }
        }
        return (VideoCodecInfo[]) arrayList.toArray(new VideoCodecInfo[arrayList.size()]);
    }

    public HardwareVideoEncoderFactory(EglBase.Context context, boolean z, boolean z2, Predicate<MediaCodecInfo> predicate) {
        if (context instanceof EglBase14.Context) {
            this.sharedContext = (EglBase14.Context) context;
        } else {
            Logging.m8407w(TAG, "No shared EglBase.Context.  Encoders will not use texture mode.");
            this.sharedContext = null;
        }
        this.enableIntelVp8Encoder = z;
        this.enableH264HighProfile = z2;
        this.codecAllowedPredicate = predicate;
    }

    @Deprecated
    public HardwareVideoEncoderFactory(boolean z, boolean z2) {
        this(null, z, z2);
    }
}
