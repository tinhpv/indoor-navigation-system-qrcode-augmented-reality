package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;
import duyvm.capstone_web.dtos.RoomDTO;

public class LocationDAO {

	public BuildingDTO createLocation(BuildingDTO buildingDTO, FloorDTO floorDTO, LocationDTO locationInfo) {
//		FloorDAO floorDAO = new FloorDAO();
		List<LocationDTO> newLocationList = new ArrayList<LocationDTO>();
//		List<FloorDTO> newFloorList = buildingDTO.getListFloor();
		List<RoomDTO> listRoom = new ArrayList<>();
		List<NeighbourDTO> listNeighbour = new ArrayList<>();
//		if (floorDTO.getListLocation() != null) {
//			newLocationList.addAll(floorDTO.getListLocation());
//		}
//		newLocationList.add(new LocationDTO(generateLocationId(buildingDTO), locationInfo.getName(),
//				locationInfo.getRatioX(), locationInfo.getRatioY(), null, listNeighbour, listRoom));
//		floorDTO.setListLocation(newLocationList);
//		newFloorList.set(floorDAO.getIndexOfFloor(floorDTO.getId(), buildingDTO), floorDTO);
//		buildingDTO.setListFloor(newFloorList);

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorDTO.getId())) {

				if (buildingDTO.getListFloor().get(i).getListLocation() == null) {
					buildingDTO.getListFloor().get(i).setListLocation(new ArrayList<LocationDTO>());
				}

				newLocationList = buildingDTO.getListFloor().get(i).getListLocation();
				newLocationList.add(new LocationDTO(generateLocationId(buildingDTO), locationInfo.getName(),
						locationInfo.getRatioX(), locationInfo.getRatioY(), null, listNeighbour, listRoom));
				buildingDTO.getListFloor().get(i).setListLocation(newLocationList);
				break;

			}
		}
		return buildingDTO;
	}

//	private String generateLocationId(List<LocationDTO> locationList, String floorId) {
//		String result = null;
//		List<Integer> idList = new ArrayList<Integer>();
//		if (locationList == null || locationList.size() == 0) {
//			result = floorId + "_l_1";
//		} else {
//			for (int i = 0; i < locationList.size(); i++) {
//				idList.add(Integer.valueOf(StringUtils.substringAfterLast(locationList.get(i).getId(), "_")));
//			}
//			int newId = Collections.max(idList) + 1;
//			result = floorId + "_l_" + newId;
//		}
//		return result;
//	}

	private String generateLocationId(BuildingDTO buildingDTO) {
		String result = null;
		List<Integer> idList = new ArrayList<Integer>();
//		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
//			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
//				idList.add(Integer.valueOf(buildingDTO.getListFloor().get(i).getList))
//			}
//		}
		List<LocationDTO> locationList = getAllLocations(buildingDTO);
		if (locationList == null || locationList.size() == 0) {
			result = "1";
		} else {
			for (int i = 0; i < locationList.size(); i++) {
				idList.add(Integer.valueOf(locationList.get(i).getId()));
			}
			result = String.valueOf(Collections.max(idList) + 1);
		}
		return result;
	}

	public LocationDTO getLocationBaseOnId(String locationId, FloorDTO floorDTO) {
		for (int i = 0; i < floorDTO.getListLocation().size(); i++) {
			if (floorDTO.getListLocation().get(i).getId().equals(locationId)) {
				return floorDTO.getListLocation().get(i);
			}
		}
		return null;
	}

	public int getIndexOfLocation(String locationId, FloorDTO floorDTO) {
		for (int i = 0; i < floorDTO.getListLocation().size(); i++) {
			if (floorDTO.getListLocation().get(i).getId().equals(locationId)) {
				return i;
			}
		}
		return -1;
	}

	public List<LocationDTO> getAllLocations(BuildingDTO buildingDTO) {
		List<LocationDTO> result = new ArrayList<LocationDTO>();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			// Trường hợp null
			if (buildingDTO.getListFloor().get(i).getListLocation() == null
					|| buildingDTO.getListFloor().get(i).getListLocation().size() == 0) {

			} else {
				//
				for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
					result.add(buildingDTO.getListFloor().get(i).getListLocation().get(j));
				}
			}
		}
		return result;
	}

	public BuildingDTO removeLocation(String locationId, BuildingDTO buildingDTO) {
//		FloorDAO floorDAO = new FloorDAO();
//
//		List<LocationDTO> newLocationList = floorDTO.getListLocation();
//		List<FloorDTO> newFloorList = buildingDTO.getListFloor();
//
//		int currentLocationIndex = getIndexOfLocation(locationId, floorDTO);
//		newLocationList.remove(currentLocationIndex);
//
//		floorDTO.setListLocation(newLocationList);
//		newFloorList.set(floorDAO.getIndexOfFloor(floorDTO.getId(), buildingDTO), floorDTO);
//		buildingDTO.setListFloor(newFloorList);

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
				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside()
						.size(); k++) {
					if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId()
							.equals(locationId)) {
						buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k)
								.setName(locationName);
					}
				}
			}
		}
		return buildingDTO;
	}

	public BuildingDTO removeLocationFromNeighbour(String locationId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getListLocation() != null) {
				for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
					if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside() != null) {
						for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j)
								.getListLocationBeside().size(); k++) {
							if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k)
									.getId().equals(locationId)) {
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
//		LocationDTO locationDTO = getLocationBaseOnId(locationInfo.getId(), floorDTO);
//		locationDTO.setName(locationInfo.getName());
//		locationDTO.setRatioX(locationInfo.getRatioX());
//		locationDTO.setRatioY(locationInfo.getRatioY());

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationInfo.getId())) {
//					buildingDTO.getListFloor().get(i).getListLocation().set(j, locationDTO);
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setName(locationInfo.getName());
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setRatioX(locationInfo.getRatioX());
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setRatioY(locationInfo.getRatioY());
				}
			}
		}

		return buildingDTO;
	}
}
