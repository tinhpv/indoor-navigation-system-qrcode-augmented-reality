package fpt.capstone.inqr.model.supportModel;

public class Step {
    public static final int TYPE_GO_STRAIGHT = 1;
    public static final int TYPE_TURN_LEFT = 2;
    public static final int TYPE_TURN_RIGHT = 4;
    public static final int TYPE_UP_STAIR = 5;
    public static final int TYPE_DOWN_STAIR = 7;
    public static final int TYPE_START_POINT = 10;
    public static final int TYPE_END_POINT = 11;

    private int type;
    private String info;
    private String distance;


    public Step(int type, String info, String distance) {
        this.type = type;
        this.info = info;
        this.distance = distance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
