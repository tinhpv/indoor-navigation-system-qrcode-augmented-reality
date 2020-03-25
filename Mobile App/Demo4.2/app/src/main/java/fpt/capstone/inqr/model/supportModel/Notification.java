package fpt.capstone.inqr.model.supportModel;

public class Notification {

    public static final int TYPE_ADD = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_REMOVE = 3;

    private int type;
    private String buildingName;


    public Notification(int type, String buildingName) {
        this.type = type;
        this.buildingName = buildingName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
