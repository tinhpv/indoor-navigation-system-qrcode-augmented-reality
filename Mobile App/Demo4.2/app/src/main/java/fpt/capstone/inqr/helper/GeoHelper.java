package fpt.capstone.inqr.helper;

import fpt.capstone.inqr.model.Location;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class GeoHelper {

    public static Double calculateAngle(Location A, Location B) {
        float xB = B.getRatioX();
        float yB = B.getRatioY();
        float xA = A.getRatioX();
        float yA = A.getRatioY();

        Double angleFromOx = Math.atan ((yB - yA) / (xB - xA));

        // value of arc-tan is only in [0..PI]
        // so if xB > xA (in Android Screen coordinate, (0, 0) in up-left),
        // vector AB is coming down over PI ==> add PI to the calculated angle.
        return  (xB - xA > 0) ? angleFromOx + Math.PI : angleFromOx;
    }
}
