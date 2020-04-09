package duyvm.capstone_web.daos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.CompanyDTO;
import duyvm.capstone_web.dtos.FloorDTO;

public class BuildingDAO {

	public BuildingDTO createBuilding(BuildingDTO buildingInfo) throws ParseException {
		BuildingDTO result = null;
		List<FloorDTO> listFloor = new ArrayList<>();

		// Nếu người dùng ko nhập ngày hết han, lấy ngày hết hạn là 30 ngày tính từ thời
		// điểm hiện tại
		if (buildingInfo.getDayExpired().isEmpty()) {
			Date currentDate = new Date();

			// Thêm vào 30 ngày tính từ ngày hiện tại
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.add(Calendar.DATE, 30);

			// Format lại thành string
			String currentDatePlus30Days = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

			buildingInfo.setDayExpired(currentDatePlus30Days);

		}

		// Tạo UUID cho buildingId
		UUID uuid = UUID.randomUUID();
		System.out.println("Building ID: " + uuid.toString());

		// Tạo object building
		result = new BuildingDTO(uuid.toString(), buildingInfo.getName(), buildingInfo.getDescription(),
				buildingInfo.getDayExpired(), true, listFloor);

		return result;
	}

	public BuildingDTO editBuilding(BuildingDTO buildingInfo, BuildingDTO buildingDTO) {
		buildingDTO.setName(buildingInfo.getName());
		buildingDTO.setDescription(buildingInfo.getDescription());
		buildingDTO.setDayExpired(buildingInfo.getDayExpired());
		buildingDTO.setActive(buildingInfo.isActive());

		return buildingDTO;
	}

	public ResponseEntity<String> importBuilding(String postUrl, String companyId, BuildingDTO buildingDTO,
			RestTemplate restTemplate) {
		// Tạo header cho post request
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		List<BuildingDTO> newBuilding = new ArrayList<BuildingDTO>();
		newBuilding.add(buildingDTO);
		CompanyDTO companyDTO = new CompanyDTO(companyId, newBuilding);

		// Đặt object building vào body của request
		HttpEntity<CompanyDTO> entity = new HttpEntity<CompanyDTO>(companyDTO, httpHeaders);

		// Exchange
		ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.POST, entity, String.class);

		System.out.println("Return status code: " + response.getStatusCodeValue());

		return response;
	}

	public ResponseEntity<String> updateBuilding(String postUrl, BuildingDTO buildingDTO, RestTemplate restTemplate) {
		BuildingDTO newBuildingObject = buildingDTO;

		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(newBuildingObject);
		System.out.println("Json String: " + json);

		newBuildingObject = gson.fromJson(json, BuildingDTO.class);

		for (int i = 0; i < newBuildingObject.getListFloor().size(); i++) {
			System.out.println("Link map " + newBuildingObject.getListFloor().get(i).getId() + ": "
					+ newBuildingObject.getListFloor().get(i).getLinkMap());
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<BuildingDTO> entity = new HttpEntity<BuildingDTO>(newBuildingObject, httpHeaders);
//		ResponseEntity<String> response = restTemplate.postForEntity(postUrl, entity, String.class);
		ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.PUT, entity, String.class);

		System.out.println("Return status code: " + response);

		return response;
	}
}
