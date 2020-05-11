package fpt.capstone.inqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Neighbor extends Location implements Serializable {

//    private int locationId;
//    private int neighboorId;

    public static final String ORIENT_LEFT = "LEFT";
    public static final String ORIENT_RIGHT = "RIGHT";
    public static final String ORIENT_UP = "UP";
    public static final String ORIENT_DOWN = "DOWN";
    public static final String ORIENT_FORWARD = "FRONT";
    public static final String ORIENT_BACKWARD = "BACK";

    public static final String ORIENT_TURN_LEFT = "TURN_LEFT";
    public static final String ORIENT_TURN_RIGHT = "TURN_RIGHT";

//    public static final int ORIENT_LEFT_TURN_LEFT = 11;
//    public static final int ORIENT_LEFT_TURN_RIGHT = 12;
//    public static final int ORIENT_RIGHT_TURN_LEFT = 21;
//    public static final int ORIENT_RIGHT_TURN_RIGHT = 22;
    public static final String ORIENT_NULL = "DONE";
    public static final String ORIENT_NO_WAY = "NO_WAY";

    @SerializedName("Orientation")
    @Expose
    private String direction;

    @SerializedName("Distance")
    @Expose
    private float distance;

    @SerializedName("Active")
    @Expose
    private boolean active;

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


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
