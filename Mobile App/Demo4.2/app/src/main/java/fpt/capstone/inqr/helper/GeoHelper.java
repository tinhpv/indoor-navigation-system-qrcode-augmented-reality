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

        double angleFromOx = Math.atan ((yB - yA) / (xB - xA));

        // value of arc-tan is only in [0..PI]
        // so if xB > xA (in Android Screen coordinate, (0, 0) in up-left),
        // vector AB is coming down over PI ==> add PI to the calculated angle.
        return  (xB - xA > 0) ? angleFromOx + Math.PI : angleFromOx;
    }

    public static String getDirection(Location A, Location B, Location C, Neighbor neighbor) {
        Double angleAB = Math.atan2(B.getRatioY() - A.getRatioY(), B.getRatioX() - A.getRatioX());
        Double angleBC = Math.atan2(B.getRatioY() - C.getRatioY(), B.getRatioX() - C.getRatioX());

        double resultInDegrees = (angleBC - angleAB) * (180/Math.PI);
        resultInDegrees = resultInDegrees < 0 ? resultInDegrees + 360 : resultInDegrees;

        if (resultInDegrees > 30 && resultInDegrees < 150) return Neighbor.ORIENT_TURN_LEFT;
        else if (resultInDegrees > 210 && resultInDegrees < 330) return Neighbor.ORIENT_TURN_RIGHT;
        else return neighbor.getDirection();

        // Value calculated is in [180 - 30 ... 180 + 30]
        // Changing direction in 30 degrees is not considered to be a turn
//        if (Math.abs(resultInDegrees) >= 150 && Math.abs(resultInDegrees) <= 210)
//            return neighbor.getDirection();
//        else if (resultInDegrees < 0)
//            return Neighbor.ORIENT_TURN_LEFT;
//        else return Neighbor.ORIENT_TURN_RIGHT;
    }

//    public static String getDirection(Location A, Location B, Location C, Neighbor neighbor) {
//        // angle by AB and Ox
//        Double angleAB = calculateAngle(A, B) * (180 / Math.PI);
//
//        // angle by BC and Ox
//        Double angleBC = calculateAngle(B, C) * (180 / Math.PI);
//
//        // angle by AB and BC || normalize to make this angle always in [0..2pi]
//        Double result = (angleBC - angleAB < 0) ? (angleBC - angleAB) + 360 : (angleBC - angleAB);
//
//        if (result == 0) return neighbor.getDirection();
//        else if (result < 180)
//            return Neighbor.ORIENT_TURN_LEFT;
//        else return Neighbor.ORIENT_TURN_RIGHT;
//    }
}
