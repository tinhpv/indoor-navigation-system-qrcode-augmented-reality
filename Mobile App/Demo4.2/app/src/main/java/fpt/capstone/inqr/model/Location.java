package fpt.capstone.inqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Location implements Serializable {

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

    @SerializedName("LinkQR")
    @Expose
    private String linkQr;

    @SerializedName("QrAnchorId")
    @Expose
    private String qrAnchorId;

    @SerializedName("SpaceAnchorId")
    @Expose
    private String spaceAnchorId;

    @SerializedName("ListLocationBeside")
    @Expose
    private List<Neighbor> neighborList;

    @SerializedName("ListRoom")
    @Expose
    private List<Room> listRoom;

    private String floorId;

    public Location() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
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

    public String getLinkQr() {
        return linkQr;
    }

    public void setLinkQr(String linkQr) {
        this.linkQr = linkQr;
    }

    public List<Neighbor> getNeighborList() {
        return neighborList;
    }

    public void setNeighborList(List<Neighbor> neighborList) {
        this.neighborList = neighborList;
    }

    public List<Room> getListRoom() {
        return listRoom;
    }

    public void setListRoom(List<Room> listRoom) {
        this.listRoom = listRoom;
    }

    public String getQrAnchorId() {
        return qrAnchorId;
    }

    public void setQrAnchorId(String qrAnchorId) {
        this.qrAnchorId = qrAnchorId;
    }

    public String getSpaceAnchorId() {
        return spaceAnchorId;
    }

    public void setSpaceAnchorId(String spaceAnchorId) {
        this.spaceAnchorId = spaceAnchorId;
    }
}
