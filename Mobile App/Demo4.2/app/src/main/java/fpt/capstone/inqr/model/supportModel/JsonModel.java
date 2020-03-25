package fpt.capstone.inqr.model.supportModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JsonModel<T> implements Serializable {

    @SerializedName("Status")
    @Expose
    private int status;

    @SerializedName("Data")
    @Expose
    private T data;





    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

}
