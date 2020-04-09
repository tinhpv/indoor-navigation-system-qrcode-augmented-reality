package fpt.capstone.inqr.activity;

import android.app.Application;

import com.microsoft.CloudServices;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class INQRApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CloudServices.initialize(this);
    }
}
