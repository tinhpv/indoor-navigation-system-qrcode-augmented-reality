package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.List;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;

public class NeighbourDAO {

	public BuildingDTO attachNeighbour(BuildingDTO buildingDTO, String locationId, NeighbourDTO neighbourInfo) {
		List<NeighbourDTO> newNeighbourList = new ArrayList<NeighbourDTO>();
//		LocationDAO locationDAO = new LocationDAO();
//		List<LocationDTO> allLocationList = locationDAO.getAllLocations(buildingDTO);
//		LocationDTO neighbourLocation = locationDAO.getLocationBaseOnId(neighbourInfo.getId(), floorDTO);
//		if (!allLocationList.contains(neighbourLocation)) {
//			//
//		} else {
//
//			FloorDAO floorDAO = new FloorDAO();
//			List<NeighbourDTO> newNeighbourList = new ArrayList<NeighbourDTO>();
//			List<LocationDTO> newLocationList = new ArrayList<LocationDTO>();
//			List<FloorDTO> newFloorList = new ArrayList<FloorDTO>();
//			if (locationDTO.getListNeighbour() != null) {
//				newNeighbourList.addAll(locationDTO.getListNeighbour());
//			}
//			newNeighbourList.add(new NeighbourDTO(neighbourLocation.getId(), neighbourLocation.getName(),
//					neighbourInfo.getOrientation(), neighbourInfo.getDistance()));
//			locationDTO.setListNeighbour(newNeighbourList);
//
//			newLocationList = floorDTO.getListLocation();
//			newLocationList.set(locationDAO.getIndexOfLocation(locationDTO.getId(), floorDTO), locationDTO);
//			floorDTO.setListLocation(newLocationList);
//
//			newFloorList = buildingDTO.getListFloor();
//			newFloorList.set(floorDAO.getIndexOfFloor(floorDTO.getId(), buildingDTO), floorDTO);
//			buildingDTO.setListFloor(newFloorList);
//
//			return buildingDTO;
//		}

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					newNeighbourList = buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside();
					newNeighbourList.add(new NeighbourDTO(neighbourInfo.getId(), neighbourInfo.getName(),
							neighbourInfo.getOrientation(), neighbourInfo.getDistance()));
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setListLocationBeside(newNeighbourList);
					break;
				}
			}
		}

		return buildingDTO;
	}

	public BuildingDTO removeNeighbourFromLocation(String locationId, String neighbourId, BuildingDTO buildingDTO) {

//		FloorDAO floorDAO = new FloorDAO();
//		LocationDAO locationDAO = new LocationDAO();
//
//		List<LocationDTO> newLocationList = new ArrayList<LocationDTO>();
//		List<FloorDTO> newFloorList = new ArrayList<FloorDTO>();
//
//		LocationDTO currentLocation = locationDAO.getLocationBaseOnId(locationId, floorDTO);
//		for (int i = 0; i < currentLocation.getListNeighbour().size(); i++) {
//			if (currentLocation.getListNeighbour().get(i).getId().equals(neighbourId)) {
//				currentLocation.getListNeighbour().remove(i);
//			}
//		}
//
//		newLocationList = floorDTO.getListLocation();
//		newLocationList.set(locationDAO.getIndexOfLocation(locationId, floorDTO), currentLocation);
//		floorDTO.setListLocation(newLocationList);
//
//		newFloorList = buildingDTO.getListFloor();
//		newFloorList.set(floorDAO.getIndexOfFloor(floorDTO.getId(), buildingDTO), floorDTO);
//		buildingDTO.setListFloor(newFloorList);

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside()
							.size(); k++) {
						if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId()
								.equals(neighbourId)) {
							buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().remove(k);
							break;
						}
					}
				}
			}
		}

		return buildingDTO;
	}

}
