package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.List;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;

public class NeighbourDAO {

	public BuildingDTO attachNeighbour(BuildingDTO buildingDTO, String locationId, NeighbourDTO neighbourInfo) {
		List<NeighbourDTO> listLocationBeside = new ArrayList<NeighbourDTO>();
		NeighbourDTO neighbourDTO = new NeighbourDTO(neighbourInfo.getId(), neighbourInfo.getName(),
				neighbourInfo.getOrientation(), neighbourInfo.getDistance());
		
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					listLocationBeside = buildingDTO.getListFloor().get(i).getListLocation().get(j)
							.getListLocationBeside();
					listLocationBeside.add(neighbourDTO);
					buildingDTO.getListFloor().get(i).getListLocation().get(j)
							.setListLocationBeside(listLocationBeside);
					break;
				}
			}
		}
		return buildingDTO;
	}

	public BuildingDTO removeNeighbourFromLocation(String locationId, String neighbourId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j)
							.getListLocationBeside().size(); k++) {
						if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k)
								.getId().equals(neighbourId)) {
							buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside()
									.remove(k);
							break;
						}
					}
				}
			}
		}

		return buildingDTO;
	}

}
