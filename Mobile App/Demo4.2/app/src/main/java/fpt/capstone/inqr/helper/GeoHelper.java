package fpt.capstone.inqr.helper;

import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class GeoHelper {

    private static Double calculateAngle(Location A, Location B) {
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

    public static String getDirection(Location A, Location B, Location C, Neighbor neighbor) {
        // angle by AB and Ox
        Double angleAB = calculateAngle(A, B);

        // angle by BC and Ox
        Double angleBC = calculateAngle(B, C);

        // angle by AB and BC || normalize to make this angle always in [0..2pi]
        Double result = angleBC - angleAB < 0 ? (angleBC - angleAB) + 2 * Math.PI : angleBC - angleAB;

        if (result == 0)
            return neighbor.getDirection();
        else if (result < Math.PI)
            return Neighbor.ORIENT_TURN_LEFT;
        else return Neighbor.ORIENT_TURN_RIGHT;
    }
}
