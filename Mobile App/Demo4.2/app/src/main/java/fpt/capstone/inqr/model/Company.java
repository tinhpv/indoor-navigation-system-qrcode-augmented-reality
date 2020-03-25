package fpt.capstone.inqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Company implements Serializable {

    @SerializedName("Id")
    @Expose
    private String id;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("ListBuilding")
    @Expose
    private List<Building> listBuilding;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Building> getListBuilding() {
        return listBuilding;
    }
}
