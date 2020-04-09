package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.utils.Utilities;

public class FloorDAO {

	public ResponseEntity<String> importBuildingFloor(MultipartFile mapFile, BuildingDTO buildingDTO,
			FloorDTO floorInfo, RestTemplate restTemplate, String postUrl) {
		Utilities utilities = new Utilities();
		// Tạo request header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();

		String floorId = generateFloorId(buildingDTO);
		bodyMap.add("buildingId", buildingDTO.getId());
		String filePath = utilities.convertMapFile(mapFile, floorId, buildingDTO.getId()).getAbsolutePath();
		bodyMap.add("floorId", floorId);
		bodyMap.add("floorName", floorInfo.getName());
		bodyMap.add(floorId, new FileSystemResource(filePath));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

//				ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.POST, requestEntity, String.class);

		ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);

		return response;
	}

	public BuildingDTO createFloor(String floorMap, BuildingDTO buildingDTO, FloorDTO floorInfo) {
		List<LocationDTO> listLocation = new ArrayList<>();
		FloorDTO floorDTO = new FloorDTO();
//		Utilities utilities = new Utilities();

		// Tạo id cho lầu
		String floorId = generateFloorId(buildingDTO);

		floorDTO = new FloorDTO(floorId, floorInfo.getName(), floorMap, null, listLocation);

		// Thêm object floor vào object building
		List<FloorDTO> newListFloor = new ArrayList<FloorDTO>();
		if (buildingDTO.getListFloor() != null) {
			newListFloor.addAll(buildingDTO.getListFloor());
		}
		newListFloor.add(floorDTO);
		buildingDTO.setListFloor(newListFloor);

		return buildingDTO;
	}

	private String generateFloorId(BuildingDTO buildingDTO) {
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

	public FloorDTO getFloorBaseOnId(String floorId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorId)) {
				return buildingDTO.getListFloor().get(i);
			}
		}
		return null;
	}

	public BuildingDTO removeFloor(String floorId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorId)) {
				buildingDTO.getListFloor().remove(i);
			}
		}
		return buildingDTO;
	}

	public BuildingDTO updateFloor(MultipartFile mapFile, FloorDTO floorInfo, BuildingDTO buildingDTO) {
		Utilities utilities = new Utilities();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorInfo.getId())) {
				buildingDTO.getListFloor().get(i).setName(floorInfo.getName());
				if (mapFile != null) {
					String filePath = utilities.convertMapFile(mapFile, floorInfo.getId(), buildingDTO.getId())
							.getAbsolutePath();
					buildingDTO.getListFloor().get(i).setMapFilePath(filePath);
				} else {
					buildingDTO.getListFloor().get(i).setMapFilePath(null);
				}
			}
		}
		return buildingDTO;
	}

	public ResponseEntity<String> importFloorMapToServer(String postUrl, BuildingDTO buildingDTO,
			RestTemplate restTemplate) {

		// Tạo request header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();

		bodyMap.add("buildingId", buildingDTO.getId());

		// Detect xem người dùng có thay đổi hoặc thêm ảnh không
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getMapFilePath() != null
					&& !buildingDTO.getListFloor().get(i).getMapFilePath().isEmpty()) {
				bodyMap.add(buildingDTO.getListFloor().get(i).getId(),
						new FileSystemResource(buildingDTO.getListFloor().get(i).getMapFilePath()));
			}
		}

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.PUT, requestEntity, String.class);

//		ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);

		System.out.println("Response status code: " + response);

		return response;
	}

//	private File convertFile(MultipartFile file, String floorId, String buildingId) {
//		File convFile = new File("img/" + buildingId + "_" + floorId + ".png");
//		try {
//			convFile.createNewFile();
//			FileOutputStream fos = new FileOutputStream(convFile);
//			fos.write(file.getBytes());
//			fos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return convFile;
//	}
}
