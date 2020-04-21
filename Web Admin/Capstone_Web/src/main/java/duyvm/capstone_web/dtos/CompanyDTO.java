package duyvm.capstone_web.dtos;

import java.io.Serializable;
import java.util.List;

public class CompanyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private List<BuildingDTO> listBuilding;

	public CompanyDTO() {
		super();
	}

	public CompanyDTO(String id, List<BuildingDTO> listBuilding) {
		super();
		this.id = id;
		this.listBuilding = listBuilding;
	}

	public CompanyDTO(String id, String name, List<BuildingDTO> listBuilding) {
		super();
		this.id = id;
		this.name = name;
		this.listBuilding = listBuilding;
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

	public List<BuildingDTO> getListBuilding() {
		return listBuilding;
	}

	public void setListBuilding(List<BuildingDTO> listBuilding) {
		this.listBuilding = listBuilding;
	}

}
