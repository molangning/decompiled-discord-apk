package lc;

import p136hc.C7064j;

/* renamed from: lc.f */
/* loaded from: /home/user/temp/discord/apk/extracted/_base.apk/classes3.dex */
public final class C10124f extends AbstractC10127i {
    @Override // lc.AbstractC10127i
    /* renamed from: c */
    public C10120b mo12215c(C10120b c10120b, int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16) {
        return mo12214d(c10120b, i, i2, C10129k.m12206b(f, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16));
    }

    @Override // lc.AbstractC10127i
    /* renamed from: d */
    public C10120b mo12214d(C10120b c10120b, int i, int i2, C10129k c10129k) {
        if (i > 0 && i2 > 0) {
            C10120b c10120b2 = new C10120b(i, i2);
            int i3 = i * 2;
            float[] fArr = new float[i3];
            for (int i4 = 0; i4 < i2; i4++) {
                float f = i4 + 0.5f;
                for (int i5 = 0; i5 < i3; i5 += 2) {
                    fArr[i5] = (i5 / 2) + 0.5f;
                    fArr[i5 + 1] = f;
                }
                c10129k.m12202f(fArr);
                AbstractC10127i.m12217a(c10120b, fArr);
                for (int i6 = 0; i6 < i3; i6 += 2) {
                    try {
                        if (c10120b.m12252e((int) fArr[i6], (int) fArr[i6 + 1])) {
                            c10120b2.m12244p(i6 / 2, i4);
                        }
                    } catch (ArrayIndexOutOfBoundsException unused) {
                        throw C7064j.m21461a();
                    }
                }
            }
            return c10120b2;
        }
        throw C7064j.m21461a();
    }
}
