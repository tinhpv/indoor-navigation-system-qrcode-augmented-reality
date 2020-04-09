package duyvm.capstone_web.dtos;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorDTO implements Serializable {
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
	@SerializedName("LinkMap")
	private String linkMap;

//	@JsonIgnore
	private String mapFilePath;

	@Expose
	@SerializedName("ListLocation")
	private List<LocationDTO> listLocation;

	public FloorDTO() {
		super();
	}

	public FloorDTO(String id, String name, String linkMap, String mapFilePath, List<LocationDTO> listLocation) {
		super();
		this.id = id;
		this.name = name;
		this.linkMap = linkMap;
		this.mapFilePath = mapFilePath;
		this.listLocation = listLocation;
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

	public String getLinkMap() {
		return linkMap;
	}

	public void setLinkMap(String linkMap) {
		this.linkMap = linkMap;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<LocationDTO> getListLocation() {
		return listLocation;
	}

	public void setListLocation(List<LocationDTO> listLocation) {
		this.listLocation = listLocation;
	}

	public String getMapFilePath() {
		return mapFilePath;
	}

	public void setMapFilePath(String mapFilePath) {
		this.mapFilePath = mapFilePath;
	}

}
