package duyvm.capstone_web.dtos;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("Id")
	private String id;

	@Expose
	@SerializedName("Name")
	private String name;

	@Expose
	@SerializedName("RatioX")
	private double ratioX;

	@Expose
	@SerializedName("RatioY")
	private double ratioY;

	@Expose
	@SerializedName("SpecialRoom")
	private boolean specialRoom;

	public RoomDTO() {
		super();
	}

	public RoomDTO(String id, String name, double ratioX, double ratioY, boolean specialRoom) {
		super();
		this.id = id;
		this.name = name;
		this.ratioX = ratioX;
		this.ratioY = ratioY;
		this.specialRoom = specialRoom;
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

	public double getRatioX() {
		return ratioX;
	}

	public void setRatioX(double ratioX) {
		this.ratioX = ratioX;
	}

	public double getRatioY() {
		return ratioY;
	}

	public void setRatioY(double ratioY) {
		this.ratioY = ratioY;
	}

	public boolean isSpecialRoom() {
		return specialRoom;
	}

	public void setSpecialRoom(boolean specialRoom) {
		this.specialRoom = specialRoom;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
