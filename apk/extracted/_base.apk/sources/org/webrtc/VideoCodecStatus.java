package org.webrtc;

/* loaded from: /home/user/temp/discord/apk/extracted/_base.apk/classes6.dex */
public enum VideoCodecStatus {
    TARGET_BITRATE_OVERSHOOT(5),
    REQUEST_SLI(2),
    NO_OUTPUT(1),
    OK(0),
    ERROR(-1),
    LEVEL_EXCEEDED(-2),
    MEMORY(-3),
    ERR_PARAMETER(-4),
    ERR_SIZE(-5),
    TIMEOUT(-6),
    UNINITIALIZED(-7),
    ERR_REQUEST_SLI(-12),
    FALLBACK_SOFTWARE(-13);
    
    private final int number;

    VideoCodecStatus(int i) {
        this.number = i;
    }

    @CalledByNative
    public int getNumber() {
        return this.number;
    }
}
