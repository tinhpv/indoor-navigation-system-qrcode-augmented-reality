package duyvm.capstone_web.daos;

import java.io.IOException;
import java.util.ArrayList;
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

	public ResponseEntity<String> importFloorToServer(MultipartFile mapFile, BuildingDTO buildingDTO,
			FloorDTO floorInfo, RestTemplate restTemplate, String postUrl) throws IOException {
		Utilities utilities = new Utilities();

		// Tạo request header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		// Tạo body cho request
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
		String floorId = utilities.generateFloorId(buildingDTO);
		bodyMap.add("buildingId", buildingDTO.getId());
		String filePath = utilities.convertMapFile(mapFile, floorId, buildingDTO.getId()).getAbsolutePath();
		bodyMap.add("floorId", floorId);
		bodyMap.add("floorName", floorInfo.getName());
		bodyMap.add(floorId, new FileSystemResource(filePath));

		// Đặt body vào request body
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		// Exchange
		ResponseEntity<String> response = restTemplate.postForEntity(postUrl, requestEntity, String.class);

		return response;
	}

	public BuildingDTO createFloor(String floorMap, BuildingDTO buildingDTO, FloorDTO floorInfo) {
		FloorDTO floorDTO = new FloorDTO();
		Utilities utilities = new Utilities();

		// Generate floor object id
		String floorId = utilities.generateFloorId(buildingDTO);

		floorDTO = new FloorDTO(floorId, floorInfo.getName(), floorMap, null, new ArrayList<LocationDTO>());

		// Thêm object floor vào building object
		List<FloorDTO> listFloor = new ArrayList<FloorDTO>();
		if (!buildingDTO.getListFloor().isEmpty()) {
			listFloor.addAll(buildingDTO.getListFloor());
		}
		listFloor.add(floorDTO);
		buildingDTO.setListFloor(listFloor);

		return buildingDTO;
	}

	public BuildingDTO removeFloor(String floorId, BuildingDTO buildingDTO) {
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getId().equals(floorId)) {
				buildingDTO.getListFloor().remove(i);
			}
		}
		return buildingDTO;
	}

	public BuildingDTO editFloor(MultipartFile mapFile, FloorDTO floorInfo, BuildingDTO buildingDTO)
			throws IOException {
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

		// Tạo body cho request
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
		bodyMap.add("buildingId", buildingDTO.getId());

		// Thêm vào body những image mà người dùng thay đổi
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getMapFilePath() != null
					&& !buildingDTO.getListFloor().get(i).getMapFilePath().isEmpty()) {
				bodyMap.add(buildingDTO.getListFloor().get(i).getId(),
						new FileSystemResource(buildingDTO.getListFloor().get(i).getMapFilePath()));
			}
		}

		// Đặt body vào request body
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		// Exchange
		ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.PUT, requestEntity, String.class);

		return response;
	}
}
