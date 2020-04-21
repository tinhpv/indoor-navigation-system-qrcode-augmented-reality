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

		// Kiểm tra trường hợp người dùng không nhập expired date
		if (buildingInfo.getDayExpired().isEmpty()) {
			Date currentDate = new Date();

			// Lấy ngày hiện tại và thêm vào 30 ngày
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.add(Calendar.DATE, 30);

			// Format date thành string với format: (yyyy-MM-dd)
			String currentDatePlus30Days = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

			// Đặt thời gian hết hạn cho tòa nhà
			buildingInfo.setDayExpired(currentDatePlus30Days);
		}

		// Id của tòa nhà được randomize bằng UUID
		String buildingId = UUID.randomUUID().toString();

		// Tạo building object
		result = new BuildingDTO(buildingId, buildingInfo.getName(), buildingInfo.getDescription(),
				buildingInfo.getDayExpired(), true, new ArrayList<FloorDTO>());

		return result;
	}

	public BuildingDTO editBuilding(BuildingDTO buildingInfo, BuildingDTO buildingDTO) {
		// Chỉnh sửa thông tin của building object
		buildingDTO.setName(buildingInfo.getName());
		buildingDTO.setDescription(buildingInfo.getDescription());
		buildingDTO.setDayExpired(buildingInfo.getDayExpired());
		buildingDTO.setActive(buildingInfo.isActive());

		return buildingDTO;
	}

	public ResponseEntity<String> importBuildingToServer(String postUrl, String companyId, BuildingDTO buildingDTO,
			RestTemplate restTemplate) {

		// Tạo company object với listBuilding chứa 1 building object
		List<BuildingDTO> newBuilding = new ArrayList<BuildingDTO>();
		newBuilding.add(buildingDTO);
		CompanyDTO companyDTO = new CompanyDTO(companyId, newBuilding);

		// Header của request
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		// Đặt company object vào body của request
		HttpEntity<CompanyDTO> entity = new HttpEntity<CompanyDTO>(companyDTO, httpHeaders);

		// Exchange
		ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.POST, entity, String.class);

		return response;
	}

	public ResponseEntity<String> updateBuilding(String putUrl, BuildingDTO buildingDTO, RestTemplate restTemplate) {

		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(buildingDTO);
		buildingDTO = gson.fromJson(json, BuildingDTO.class);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<BuildingDTO> entity = new HttpEntity<BuildingDTO>(buildingDTO, httpHeaders);
		ResponseEntity<String> response = restTemplate.exchange(putUrl, HttpMethod.PUT, entity, String.class);

		return response;
	}
}
