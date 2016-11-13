package com.jiajunhui.multiscreen_udp_terminal;

import android.app.Application;

import com.xapp.jjh.xui.config.XUIConfig;

/**
 * Created by Taurus on 16/11/13.
 */

public class MApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XUIConfig.setXUIRedStyle();
    }
}
