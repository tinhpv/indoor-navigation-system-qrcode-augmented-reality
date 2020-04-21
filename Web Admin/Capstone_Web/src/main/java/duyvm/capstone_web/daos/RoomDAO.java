package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.List;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.RoomDTO;
import duyvm.capstone_web.utils.Utilities;

public class RoomDAO {

	public BuildingDTO createRoom(BuildingDTO buildingDTO, String locationId, RoomDTO roomInfo) {
		Utilities utilities = new Utilities();
		List<RoomDTO> listRoom = new ArrayList<RoomDTO>();
		RoomDTO roomDTO = new RoomDTO(utilities.generateRoomId(buildingDTO), roomInfo.getName(), roomInfo.getRatioX(),
				roomInfo.getRatioY(), roomInfo.isSpecialRoom());

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					listRoom = buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom();
					listRoom.add(roomDTO);
					buildingDTO.getListFloor().get(i).getListLocation().get(j).setListRoom(listRoom);
					break;
				}
			}
		}

		return buildingDTO;
	}

	public BuildingDTO removeRoomById(String roomId, BuildingDTO buildingDTO) {
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
}
