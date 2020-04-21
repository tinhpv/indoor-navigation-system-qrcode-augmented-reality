package duyvm.capstone_web.dtos;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationDTO implements Serializable {

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

	@SerializedName("LinkQR")
	private String linkQr;
	
	@Expose
	@SerializedName("ListLocationBeside")
	private List<NeighbourDTO> listLocationBeside;

	@Expose
	@SerializedName("ListRoom")
	private List<RoomDTO> listRoom;

	public LocationDTO() {
		super();
	}

	public LocationDTO(String id, String name, double ratioX, double ratioY, String linkQr,
			List<NeighbourDTO> listLocationBeside, List<RoomDTO> listRoom) {
		super();
		this.id = id;
		this.name = name;
		this.ratioX = ratioX;
		this.ratioY = ratioY;
		this.linkQr = linkQr;
		this.listLocationBeside = listLocationBeside;
		this.listRoom = listRoom;
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

	public String getLinkQr() {
		return linkQr;
	}

	public void setLinkQr(String linkQr) {
		this.linkQr = linkQr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

//	public List<NeighbourDTO> getListNeighbour() {
//		return listNeighbour;
//	}
//
//	public void setListNeighbour(List<NeighbourDTO> listNeighbour) {
//		this.listNeighbour = listNeighbour;
//	}

	public List<RoomDTO> getListRoom() {
		return listRoom;
	}

	public List<NeighbourDTO> getListLocationBeside() {
		return listLocationBeside;
	}

	public void setListLocationBeside(List<NeighbourDTO> listLocationBeside) {
		this.listLocationBeside = listLocationBeside;
	}

	public void setListRoom(List<RoomDTO> listRoom) {
		this.listRoom = listRoom;
	}

}
