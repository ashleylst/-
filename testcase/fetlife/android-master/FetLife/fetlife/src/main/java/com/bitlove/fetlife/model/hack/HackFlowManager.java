package com.bitlove.fetlife.model.hack;


import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.lang.reflect.Field;
import java.util.HashSet;

public class HackFlowManager {

    public static void init(Context context) {
        FlowManager.init(context);
        try {
            Field loadedModulesField = FlowManager.class.getDeclaredField("loadedModules");
            loadedModulesField.setAccessible(true);
            loadedModulesField.set(null,new HashSet());
            FlowManager.initModule(HackDatabaseHolder.class);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public static void close() {
        try {
            Field loadedModulesField = FlowManager.class.getDeclaredField("loadedModules");
            loadedModulesField.setAccessible(true);
            loadedModulesField.set(null,new HashSet());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
