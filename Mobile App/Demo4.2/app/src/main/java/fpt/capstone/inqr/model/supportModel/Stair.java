package fpt.capstone.inqr.model.supportModel;

public class Stair {
    private String orientation;
    private float ratioX, ratioY;

    public Stair(String orientation, float ratioX, float ratioY) {
        this.orientation = orientation;
        this.ratioX = ratioX;
        this.ratioY = ratioY;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
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
}
