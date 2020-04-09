package fpt.capstone.inqr.fragment;

import android.graphics.Bitmap;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class CustomArFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        session.configure(config);
        this.getArSceneView().setupSession(session);
        return config;
    }
}
