package fpt.capstone.inqr.model.supportModel;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class LocationDemo {
    private String qrAnchorId;
    private String spaceAnchorId;

    public LocationDemo(String qrAnchorId, String spaceAnchorId) {
        this.qrAnchorId = qrAnchorId;
        this.spaceAnchorId = spaceAnchorId;
    }

    public String getQrAnchorId() {
        return qrAnchorId;
    }

    public String getSpaceAnchorId() {
        return spaceAnchorId;
    }
}
