package duyvm.capstone_web.dtos;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NeighbourDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("Id")
	private String id;

	@SerializedName("Name")
	private String name;

	@Expose
	@SerializedName("Orientation")
	private String orientation;

	@Expose
	@SerializedName("Distance")
	private float distance;

	public NeighbourDTO() {
		super();
	}
	
	public NeighbourDTO(String id, String name, String orientation, float distance) {
		super();
		this.id = id;
		this.name = name;
		this.orientation = orientation;
		this.distance = distance;
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

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
