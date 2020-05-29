package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.List;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;
import duyvm.capstone_web.dtos.RoomDTO;
import duyvm.capstone_web.utils.Utilities;

public class LocationDAO {

	public BuildingDTO createLocation(BuildingDTO buildingDTO, FloorDTO floorDTO, LocationDTO locationInfo) {
		Utilities utilities = new Utilities();
		List<LocationDTO> listLocation = new ArrayList<LocationDTO>();

		String locationId = utilities.generateLocationId(buildingDTO);
		LocationDTO locationDTO = new LocationDTO(locationId, locationInfo.getName(), locationInfo.getRatioX(), locationInfo.getRatioY(), null, null, null,
				new ArrayList<NeighbourDTO>(), new ArrayList<RoomDTO>());

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorDTO.getId())) {
				listLocation = buildingDTO.getListFloor().get(i).getListLocation();
				listLocation.add(locationDTO);
				buildingDTO.getListFloor().get(i).setListLocation(listLocation);
				break;
			}
		}
		return buildingDTO;
	}

	public BuildingDTO removeLocation(String locationId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getListLocation() != null) {
				for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
					if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
						buildingDTO.getListFloor().get(i).getListLocation().remove(j);
					}
				}
			}
		}
		return buildingDTO;
	}

	public BuildingDTO updateAllNeighbourName(String locationId, String locationName, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().size(); k++) {
					if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId().equals(locationId)) {
						buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).setName(locationName);
					}
				}
			}
		}
		return buildingDTO;
	}

	public BuildingDTO removeNeighbourById(String locationId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getListLocation() != null) {
				for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
					if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside() != null) {
						for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().size(); k++) {
							if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId().equals(locationId)) {
								buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().remove(k);
							}
						}
					}
				}
			}
		}
		return buildingDTO;
	}

	public BuildingDTO updateLocation(LocationDTO locationInfo, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationInfo.getId())) {
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setName(locationInfo.getName());
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setRatioX(locationInfo.getRatioX());
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setRatioY(locationInfo.getRatioY());
				}
			}
		}

		return buildingDTO;
	}
}
