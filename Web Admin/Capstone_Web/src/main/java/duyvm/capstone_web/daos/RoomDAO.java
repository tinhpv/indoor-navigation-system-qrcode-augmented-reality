package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.RoomDTO;

public class RoomDAO {

	public BuildingDTO createRoom(BuildingDTO buildingDTO, String locationId, RoomDTO roomInfo) {
		List<RoomDTO> newRoomList = new ArrayList<RoomDTO>();
//		LocationDAO locationDAO = new LocationDAO();
//		FloorDAO floorDAO = new FloorDAO();
//		List<LocationDTO> newLocationList = new ArrayList<LocationDTO>();
//		List<FloorDTO> newFloorList = new ArrayList<FloorDTO>();
//		if (locationDTO.getListRoom() != null) {
//			newRoomList.addAll(locationDTO.getListRoom());
//		}
//		newRoomList.add(new RoomDTO(generateRoomId(buildingDTO), roomInfo.getName(), roomInfo.getRatioX(),
//				roomInfo.getRatioY(), roomInfo.isSpecialRoom()));
//		locationDTO.setListRoom(newRoomList);
//
//		newLocationList = floorDTO.getListLocation();
//		newLocationList.set(locationDAO.getIndexOfLocation(locationDTO.getId(), floorDTO), locationDTO);
//		floorDTO.setListLocation(newLocationList);
//
//		newFloorList = buildingDTO.getListFloor();
//		newFloorList.set(floorDAO.getIndexOfFloor(floorDTO.getId(), buildingDTO), floorDTO);
//		buildingDTO.setListFloor(newFloorList);

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					newRoomList = buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom();
					newRoomList.add(new RoomDTO(generateRoomId(buildingDTO), roomInfo.getName(), roomInfo.getRatioX(),
							roomInfo.getRatioY(), roomInfo.isSpecialRoom()));
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setListRoom(newRoomList);
					break;
				}
			}
		}

		return buildingDTO;
	}

//	private String generateRoomId(List<RoomDTO> roomList, String locationId) {
//		String result = null;
//		List<Integer> idList = new ArrayList<Integer>();
//		if (roomList == null || roomList.size() == 0) {
//			result = locationId + "_r_1";
//		} else {
//			for (int i = 0; i < roomList.size(); i++) {
//				idList.add(Integer.valueOf(StringUtils.substringAfterLast(roomList.get(i).getId(), "_")));
//			}
//			int newId = Collections.max(idList) + 1;
//			result = locationId + "_r_" + newId;
//		}
//		return result;
//	}

	private String generateRoomId(BuildingDTO buildingDTO) {
		String result = null;
		List<Integer> idList = new ArrayList<Integer>();
		List<RoomDTO> roomList = getAllRoom(buildingDTO);
		if (roomList == null || roomList.size() == 0) {
			result = "1";
		} else {
			for (int i = 0; i < roomList.size(); i++) {
				idList.add(Integer.valueOf(roomList.get(i).getId()));
			}
			result = String.valueOf(Collections.max(idList) + 1);
		}
		return result;
	}

	public BuildingDTO removeRoomFromLocation(String roomId, BuildingDTO buildingDTO) {

//		FloorDAO floorDAO = new FloorDAO();
//		LocationDAO locationDAO = new LocationDAO();
//
//		List<LocationDTO> newLocationList = new ArrayList<LocationDTO>();
//		List<FloorDTO> newFloorList = new ArrayList<FloorDTO>();
//
//		LocationDTO currentLocation = locationDAO.getLocationBaseOnId(locationId, floorDTO);
//		for (int i = 0; i < currentLocation.getListRoom().size(); i++) {
//			if (currentLocation.getListRoom().get(i).getId().equals(roomId)) {
//				currentLocation.getListRoom().remove(i);
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
				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom()
						.size(); k++) {
					if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k).getId()
							.equals(roomId)) {
						buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().remove(k);
						break;
					}
				}
			}
		}

		return buildingDTO;
	}

	public List<RoomDTO> getAllRoom(BuildingDTO buildingDTO) {
		List<RoomDTO> result = new ArrayList<RoomDTO>();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom()
						.size(); k++) {
					result.add(buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k));
				}
			}
		}
		return result;
	}
}
