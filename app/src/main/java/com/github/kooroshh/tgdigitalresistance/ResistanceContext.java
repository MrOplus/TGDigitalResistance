package com.github.kooroshh.tgdigitalresistance;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import co.ronash.pushe.Pushe;
import io.fabric.sdk.android.Fabric;
import ir.tapsell.sdk.Tapsell;

/**
 * Created by Oplus on 2018/05/03.
 */

public class ResistanceContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Tapsell.initialize(this, "oepnonnbedjasaorgnhtodcehaecehpfjeskjcfcsbbeeqdirekpeekehrbbifgebhntak");
        Pushe.initialize(this,true);
        Console.CopyAssets(this);
    }
}
