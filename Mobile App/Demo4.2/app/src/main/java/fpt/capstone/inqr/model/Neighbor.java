package fpt.capstone.inqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Neighbor extends Location implements Serializable {

//    private int locationId;
//    private int neighboorId;

    public static final int ORIENT_LEFT = 1;
    public static final int ORIENT_RIGHT = 2;
    public static final int ORIENT_UP = 3;
    public static final int ORIENT_DOWN = 4;

    public static final int ORIENT_TURN_LEFT = 5;
    public static final int ORIENT_TURN_RIGHT = 6;

    public static final int ORIENT_LEFT_TURN_LEFT = 11;
    public static final int ORIENT_LEFT_TURN_RIGHT = 12;
    public static final int ORIENT_RIGHT_TURN_LEFT = 21;
    public static final int ORIENT_RIGHT_TURN_RIGHT = 22;
    public static final int ORIENT_NULL = 0;

    @SerializedName("Orientation")
    @Expose
    private int direction;

    @SerializedName("Distance")
    @Expose
    private float distance;

//    @SerializedName("Orientation")
//    @Expose
//    private String orientation;


    public Neighbor() {
    }

//
//
//
//    public int getLocationId() {
//        return locationId;
//    }
//
//    public void setLocationId(int locationId) {
//        this.locationId = locationId;
//    }
//
//    public int getNeighboorId() {
//        return neighboorId;
//    }
//
//    public void setNeighboorId(int neighboorId) {
//        this.neighboorId = neighboorId;
//    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
