package duyvm.capstone_web.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.CompanyDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;
import duyvm.capstone_web.dtos.RoomDTO;

public class JsonParser {

	public BuildingDTO parseToBuildingObject(String getUrl, RestTemplate restTemplate) throws Exception {
		BuildingDTO result = new BuildingDTO();

		// Gọi gson builder
		Gson gson = new GsonBuilder().serializeNulls().create();

		// Gọi api và lấy kết quả trả về là chuỗi json
		String returnJsonString = restTemplate.getForObject(getUrl, String.class);

		// Parse chuỗi json thành object json
		JSONObject dataObject = new JSONObject(returnJsonString);

		// Lấy thông tin của tòa nhà nằm trong kết quả trả về
		JSONObject buildingObject = dataObject.getJSONObject("Data");

		// Parse thông tin của tòa nhà trong object json vào class BuildingDTO
		result = gson.fromJson(buildingObject.toString(), BuildingDTO.class);

		// Chỉnh lại ngày từ mm/dd/yyyy thành yyyy-MM-dd
		// Parse String thành date
		Date newDateFormat = new SimpleDateFormat("MM/dd/yyyy").parse(result.getDayExpired());
		// Format Date thành String với format yyyy-MM-dd để hiện lên trang jsp
		String newDateString = new SimpleDateFormat("yyyy-MM-dd").format(newDateFormat);
		result.setDayExpired(newDateString);

		// Xử lý các list null
		result = processNullList(result);

		// Tiến hành cắt chuỗi id cho Floor, Location, Neighbour, Room
		result = trimBuildingIds(result);

		return result;

	}

	public List<CompanyDTO> parseToCompanyObject(String getUrl, RestTemplate restTemplate) throws Exception {
		List<CompanyDTO> result = new ArrayList<CompanyDTO>();
		List<BuildingDTO> listOfBuildings = new ArrayList<BuildingDTO>();

		// Gọi api và lấy chuỗi kết quả trả về
		String returnJsonString = restTemplate.getForObject(getUrl, String.class);

		// Parse chuỗi json thành object json
		JSONObject dataObject = new JSONObject(returnJsonString);

		// Lấy thông tin của các công ty nằm trong Data
		JSONArray companyData = dataObject.getJSONArray("Data");

		for (int i = 0; i < companyData.length(); i++) {
			// Lấy thông tin của công ty
			JSONObject companyObj = companyData.getJSONObject(i);

			// Lấy thông tin các tòa nhà thuộc công ty này
			JSONArray buildingData = companyObj.getJSONArray("ListBuilding");

			listOfBuildings = new ArrayList<BuildingDTO>();

			for (int j = 0; j < buildingData.length(); j++) {
				// Lấy thông tin của tòa nhà
				JSONObject buildingObj = buildingData.getJSONObject(j);

				BuildingDTO buildingDTO = new BuildingDTO(buildingObj.getString("Id"), buildingObj.getString("Name"));

				// Thêm tòa nhà vào danh sách
				listOfBuildings.add(buildingDTO);
			}

			CompanyDTO companyDTO = new CompanyDTO(companyObj.getString("Id"), companyObj.getString("Name"),
					listOfBuildings);

			// Thêm công ty vào danh sách
			result.add(companyDTO);
		}

		return result;
	}

	// Cắt chuỗi để lấy id sơ khai
	private final String idTrimmer(String id) {
		String result = null;
		result = StringUtils.substringAfterLast(id, "_");
		return result;
	}

	private final BuildingDTO trimBuildingIds(BuildingDTO buildingDTO) {

		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {

			// Cắt ID cho floor
			buildingDTO.getListFloor().get(i).setId(idTrimmer(buildingDTO.getListFloor().get(i).getId()));

			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {

				// Cắt ID cho location
				buildingDTO.getListFloor().get(i).getListLocation().get(j)
						.setId(idTrimmer(buildingDTO.getListFloor().get(i).getListLocation().get(j).getId()));

				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside()
						.size(); k++) {

					// Cắt id cho neighbour
					buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k)
							.setId(idTrimmer(buildingDTO.getListFloor().get(i).getListLocation().get(j)
									.getListLocationBeside().get(k).getId()));

				}

				for (int k = 0; k < buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom()
						.size(); k++) {

					// Cắt id cho room
					buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k).setId(idTrimmer(
							buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k).getId()));
				}
			}
		}

		return buildingDTO;
	}

	private final BuildingDTO processNullList(BuildingDTO buildingDTO) {

		// Thay tất cả các listLocation, listNeighbour, listRoom, listFloor nào có giá
		// trị null thành empty
		if (buildingDTO.getListFloor() == null) {

			buildingDTO.setListFloor(new ArrayList<FloorDTO>());

		} else {
			for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {

				if (buildingDTO.getListFloor().get(i).getListLocation() == null) {

					buildingDTO.getListFloor().get(i).setListLocation(new ArrayList<LocationDTO>());

				} else {
					for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {

						if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListLocationBeside() == null) {

							buildingDTO.getListFloor().get(i).getListLocation().get(j)
									.setListLocationBeside(new ArrayList<NeighbourDTO>());
						}

						if (buildingDTO.getListFloor().get(i).getListLocation().get(j).getListRoom() == null) {

							buildingDTO.getListFloor().get(i).getListLocation().get(j)
									.setListRoom(new ArrayList<RoomDTO>());
						}
					}
				}
			}
		}

		return buildingDTO;
	}
}
