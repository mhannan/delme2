package com.scanlibrary;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.ads.AdSize;

class AdaptiveAds {
    Context context;

    AdaptiveAds(Context context) {
        this.context = context;
    }

    public AdSize getSize() {
        WindowManager window = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);

        Display display = window.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = (float) outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context,
                adWidth
        );
    }


}
