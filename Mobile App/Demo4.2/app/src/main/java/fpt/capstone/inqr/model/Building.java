package fpt.capstone.inqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Building implements Serializable {

    public static final int DOWNLOADED = 1;
    public static final int NOT_DOWNLOAD = 0;
    public static final int UPDATE = 2;

    public static final int EXISTED = 11;
    public static final int NOT_EXIST = 10;
    public static final int UPDATE_DATA = 12;

    @SerializedName("Id")
    @Expose
    private String id;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("CompanyName")
    @Expose
    private String companyName;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("Version")
    @Expose
    private int version;

    @SerializedName("DayExpired")
    @Expose
    private String dayExpired;

    @SerializedName("HadExpired")
    @Expose
    private boolean hadExpired;

    @SerializedName("ListFloor")
    @Expose
    private List<Floor> listFloor;

    private int status;

    public List<Floor> getListFloor() {
        return listFloor;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public int getVersion() {
        return version;
    }

    public String getDayExpired() {
        return dayExpired;
    }

    public boolean isHadExpired() {
        return hadExpired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setDayExpired(String dayExpired) {
        this.dayExpired = dayExpired;
    }

    public void setHadExpired(boolean hadExpired) {
        this.hadExpired = hadExpired;
    }

    public void setListFloor(List<Floor> listFloor) {
        this.listFloor = listFloor;
    }


}
