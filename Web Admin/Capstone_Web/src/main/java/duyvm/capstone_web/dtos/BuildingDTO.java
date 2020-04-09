package duyvm.capstone_web.dtos;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingDTO implements Serializable {

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
	@SerializedName("Description")
	private String description;

	@Expose
	@SerializedName("DayExpired")
	private String dayExpired;

	@Expose
	@SerializedName("Active")
	private boolean active;

	@Expose
	@SerializedName("ListFloor")
	private List<FloorDTO> listFloor;

	public BuildingDTO() {
	}

	public BuildingDTO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public BuildingDTO(String id, String name, String description, String dayExpired, boolean active,
			List<FloorDTO> listFloor) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.dayExpired = dayExpired;
		this.active = active;
		this.listFloor = listFloor;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDayExpired() {
		return dayExpired;
	}

	public void setDayExpired(String dayExpired) {
		this.dayExpired = dayExpired;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<FloorDTO> getListFloor() {
		return listFloor;
	}

	public void setListFloor(List<FloorDTO> listFloor) {
		this.listFloor = listFloor;
	}
}
