package com.scanlibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsClass {

   private FirebaseAnalytics firebaseAnalytics;

    public AnalyticsClass(Context _context) {

        firebaseAnalytics = FirebaseAnalytics.getInstance(_context);

    }

    public void sendEventAnalytics(String eventName, String eventStatus) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, eventStatus);
        firebaseAnalytics.logEvent(eventName, bundle);

    }

    public void sendScreenAnalytics(Activity activity, String screenName) {

        firebaseAnalytics.setCurrentScreen(activity, screenName, null);

    }
}