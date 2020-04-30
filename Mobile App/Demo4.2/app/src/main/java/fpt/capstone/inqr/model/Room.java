package fpt.capstone.inqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Room implements Serializable {

    @SerializedName("Id")
    @Expose
    private String id;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("RatioX")
    @Expose
    private float ratioX;

    @SerializedName("RatioY")
    @Expose
    private float ratioY;

    @SerializedName("SpecialRoom")
    @Expose
    private boolean specialRoom;

    @SerializedName("SpaceAnchorId")
    @Expose
    private String spaceAnchorId;

    private String locationId;

    private String floorId;

    private String floorName;

    private int counter;

    private boolean favorite;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getFloorId() {
        return floorId;
    }

    public String getLocationId() {
        return locationId;
    }

    public boolean isSpecialRoom() {
        return specialRoom;
    }

    public void setSpecialRoom(boolean specialRoom) {
        this.specialRoom = specialRoom;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRatioX() {
        return ratioX;
    }

    public void setRatioX(float ratioX) {
        this.ratioX = ratioX;
    }

    public float getRatioY() {
        return ratioY;
    }

    public void setRatioY(float ratioY) {
        this.ratioY = ratioY;
    }

    public String getSpaceAnchorId() {
        return spaceAnchorId;
    }

    public void setSpaceAnchorId(String spaceAnchorId) {
        this.spaceAnchorId = spaceAnchorId;
    }
}
