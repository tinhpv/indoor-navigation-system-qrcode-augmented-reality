package duyvm.capstone_web.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;
import duyvm.capstone_web.dtos.RoomDTO;

public class Utilities {

	public boolean checkForChangedImage(BuildingDTO buildingDTO) {
		boolean isChanged = false;
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getMapFilePath() != null && !buildingDTO.getListFloor().get(i).getMapFilePath().isEmpty()) {
				isChanged = true;
				break;
			}
		}
		return isChanged;
	}

	public boolean checkForValidImageFile(MultipartFile mapFile) {
		boolean result = false;

		// Get extension of chosen file (.jpg, .png, .docx, ...)
		String fileExtension = FilenameUtils.getExtension(mapFile.getOriginalFilename()).toLowerCase();

		// Only .jpg or .png file are accepted
		if (fileExtension.equals("jpg") || fileExtension.equals("png")) {
			result = true;
		}
		return result;
	}

	public File convertMapFile(MultipartFile file, String floorId, String buildingId) throws IOException {
		File mapDir = new File(System.getProperty("java.io.tmpdir"), "map/" + buildingId + "/");
		// Tạo thư mục lưu trữ hình nếu như không tồn tại hoặc không phải là directory
		if (!mapDir.exists() || !mapDir.isDirectory()) {
			mapDir.mkdirs();
		}

		// File hình ảnh có tên theo format "buildingId_floorId.png"
		File convFile = new File(mapDir.getAbsolutePath() + "/" + buildingId + "_" + floorId + ".png");
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		return convFile;
	}

	public File converQrCodeFile(BuildingDTO buildingDTO) throws IOException {

		// Tạo mới thư mục qr code với tên được random
		String randomDirName = UUID.randomUUID().toString();
		File qrcodeDir = new File(System.getProperty("java.io.tmpdir"), "qrcode/" + randomDirName + "-" + buildingDTO.getName());
		if (!qrcodeDir.exists() || !qrcodeDir.isDirectory()) {
			qrcodeDir.mkdirs();
		}

		List<LocationDTO> listLocation = getAllLocationOfBuilding(buildingDTO);

		for (int i = 0; i < listLocation.size(); i++) {
			if (listLocation.get(i).getLinkQr() != null && listLocation.get(i).getName() != null) {
				InputStream in = new URL(listLocation.get(i).getLinkQr()).openStream();
				Files.copy(in, Paths.get(qrcodeDir + "/" + listLocation.get(i).getName() + ".png"), StandardCopyOption.REPLACE_EXISTING);
			}
		}

		return qrcodeDir;
	}

	private List<LocationDTO> getAllLocationOfBuilding(BuildingDTO buildingDTO) {
		List<LocationDTO> result = new ArrayList<LocationDTO>();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				result.add(buildingDTO.getListFloor().get(i).getListLocation().get(j));
			}
		}
		return result;
	}

	private List<LocationDTO> getAllStairsOfBuilding(BuildingDTO buildingDTO) {
		List<LocationDTO> result = new ArrayList<LocationDTO>();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getName().toLowerCase().contains("stairs")) {
					result.add(buildingDTO.getListFloor().get(i).getListLocation().get(j));
				}
			}
		}
		return result;
	}

	public List<RoomDTO> getAllRoomOfBuilding(BuildingDTO buildingDTO) {
		List<RoomDTO> result = new ArrayList<RoomDTO>();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().size(); k++) {
					result.add(buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k));
				}
			}
		}
		return result;
	}

	public List<LocationDTO> filterAvailableLocations(BuildingDTO buildingDTO, FloorDTO floorDTO, LocationDTO locationDTO) {
		List<LocationDTO> listOfAvaiableLocations = new ArrayList<LocationDTO>();
		List<LocationDTO> listOfStairs = new ArrayList<LocationDTO>();

		listOfAvaiableLocations.addAll(floorDTO.getListLocation());

		if (locationDTO.getName().toLowerCase().contains("stairs")) {

			listOfStairs = getAllStairsOfBuilding(buildingDTO);

			listOfAvaiableLocations.addAll(listOfStairs);
		}

		listOfAvaiableLocations = listOfAvaiableLocations.stream().distinct().collect(Collectors.toList());

		return listOfAvaiableLocations;
	}

	public boolean checkExistedNeighbour(NeighbourDTO neighbourInfo, LocationDTO locationDTO) {
		boolean isExisted = false;
		for (int i = 0; i < locationDTO.getListLocationBeside().size(); i++) {
			if (locationDTO.getListLocationBeside().get(i).getId().equals(neighbourInfo.getId())) {
				isExisted = true;
			}
		}
		return isExisted;
	}

	public String generateFloorId(BuildingDTO buildingDTO) {
		String result = null;
		List<Integer> idList = new ArrayList<Integer>();
		if (buildingDTO.getListFloor() == null || buildingDTO.getListFloor().size() == 0) {
			result = "1";
		} else {
			for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
				idList.add(Integer.valueOf(buildingDTO.getListFloor().get(i).getId()));
			}
			int newId = Collections.max(idList) + 1;
			result = String.valueOf(newId);
		}
		return result;
	}

	public String generateLocationId(BuildingDTO buildingDTO) {
		String result = null;
		List<Integer> idList = new ArrayList<Integer>();
		List<LocationDTO> locationList = getAllLocationOfBuilding(buildingDTO);
		if (locationList == null || locationList.size() == 0) {
			result = "1";
		} else {
			for (int i = 0; i < locationList.size(); i++) {
				idList.add(Integer.valueOf(locationList.get(i).getId()));
			}
			int newId = Collections.max(idList) + 1;
			result = String.valueOf(newId);
		}
		return result;
	}

	public String generateRoomId(BuildingDTO buildingDTO) {
		String result = null;
		List<Integer> idList = new ArrayList<Integer>();
		List<RoomDTO> roomList = getAllRoomOfBuilding(buildingDTO);
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

	public FloorDTO getFloorById(String floorId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorId)) {
				return buildingDTO.getListFloor().get(i);
			}
		}
		return null;
	}

	public LocationDTO getLocationById(String locationId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
					return buildingDTO.getListFloor().get(i).getListLocation().get(j);
				}
			}
		}
		return null;
	}

	public boolean checkExistedLocationForCreate(LocationDTO locationInfo, BuildingDTO buildingDTO) {
		boolean result = false;
		String locationName = locationInfo.getName().toLowerCase();
		locationName = locationName.replaceAll("\\s+", "");
		List<LocationDTO> listOfAllLocations = getAllLocationOfBuilding(buildingDTO);
		for (int i = 0; i < listOfAllLocations.size(); i++) {
			String locationListName = listOfAllLocations.get(i).getName().toLowerCase();
			locationListName = locationListName.replaceAll("\\s+", "");
			if (locationListName.equals(locationName)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean checkExistedLocationForEdit(LocationDTO locationInfo, BuildingDTO buildingDTO) {
		boolean result = false;
		List<LocationDTO> listOfAllLocations = getAllLocationOfBuilding(buildingDTO);
		String locationName = locationInfo.getName().toLowerCase();
		locationName = locationName.replaceAll("\\s+", "");
		for (int i = 0; i < listOfAllLocations.size(); i++) {
			String locationListName = listOfAllLocations.get(i).getName().toLowerCase();
			locationListName = locationListName.replaceAll("\\s+", "");
			if (locationListName.equals(locationName)) {
				if (!listOfAllLocations.get(i).getId().equals(locationInfo.getId())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public boolean checkExistedRoom(RoomDTO roomInfo, BuildingDTO buildingDTO) {
		boolean result = false;
		List<RoomDTO> listOfAllRooms = getAllRoomOfBuilding(buildingDTO);
		String roomName = roomInfo.getName().toLowerCase();
		roomName = roomName.replaceAll("\\s+", "");
		for (int i = 0; i < listOfAllRooms.size(); i++) {
			String roomListName = listOfAllRooms.get(i).getName().toLowerCase();
			roomListName = roomListName.replaceAll("\\s+", "");
			if (roomListName.equals(roomName)) {
				if (!listOfAllRooms.get(i).isSpecialRoom() || !roomInfo.isSpecialRoom()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	public boolean checkLocationExistedSpecialRoom(String locationId, RoomDTO roomInfo, BuildingDTO buildingDTO) {
		boolean result = false;
		LocationDTO locationDTO = getLocationById(locationId, buildingDTO);
		String roomName = roomInfo.getName().toLowerCase();
		roomName = roomName.replaceAll("\\s+", "");
		for (int i = 0; i < locationDTO.getListRoom().size(); i++) {
			if (locationDTO.getListRoom().get(i).isSpecialRoom()) {
				String specialRoomName = locationDTO.getListRoom().get(i).getName().toLowerCase();
				specialRoomName = specialRoomName.replaceAll("\\s+", "");
				if (specialRoomName.equals(roomName)) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}

	public boolean getNeighbourStatus(BuildingDTO buildingDTO, String locationId, String neighbourId) {
		boolean result = true;

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				if (locationId.equals(buildingDTO.getListFloor().get(i).getListLocation().get(j).getId())) {
					for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().size(); k++) {
						if (neighbourId.equals(buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId())) {
							result = buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).isActive();
							break;
						}
					}
				}
			}
		}

		return result;
	}

//	public List<LocationDTO> checkForMissingNeighbour(BuildingDTO buildingDTO) {
//		List<LocationDTO> listOfLocation = new ArrayList<LocationDTO>();
//		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
//			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
//				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().size(); k++) {
//					if (checkExistedLocationWithNeighbour(buildingDTO, locationId, neighbourId))
//				}
//			}
//		}
//	}
//
//	private boolean checkExistedLocationWithNeighbour(BuildingDTO buildingDTO, String locationId, String neighbourId) {
//		boolean isExisted = false;
//		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
//			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
//				if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getId().equals(locationId)) {
//					for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().size(); k++) {
//						if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId().equals(neighbourId)) {
//							isExisted = true;
//						}
//					}
//				}
//			}
//		}
//		return isExisted;
//	}
}
