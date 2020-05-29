package fpt.capstone.inqr.helper;

import android.os.Handler;
import android.os.Looper;

/**
 * Demo4
 * Created by TinhPV on 2020-04-18
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class MainThreadContext {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Looper mainLooper = Looper.getMainLooper();

    public static void runOnUiThread(Runnable runnable){
        if (mainLooper.isCurrentThread()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        } // end if
    }

}
