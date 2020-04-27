package duyvm.capstone_web.utils;

import java.text.ParseException;
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

	public BuildingDTO parseToBuildingObject(String getUrl, RestTemplate restTemplate) throws ParseException {
		BuildingDTO result = new BuildingDTO();

		// Gọi gson builder
		Gson gson = new GsonBuilder().serializeNulls().create();

		// Gọi api và lấy kết quả trả về là json dưới dạng String
		String returnJsonString = restTemplate.getForObject(getUrl, String.class);

		// Parse chuỗi json thành object json
		JSONObject dataObject = new JSONObject(returnJsonString);

		// Lấy thông tin của tòa nhà nằm trong kết quả trả về
		JSONObject buildingObject = dataObject.getJSONObject("Data");

		// Parse thông tin của tòa nhà trong json object vào BuildingDTO
		result = gson.fromJson(buildingObject.toString(), BuildingDTO.class);

		// Chỉnh lại format của expiredDate từ mm/dd/yyyy thành yyyy-MM-dd
		// Parse string thành date
		Date newDateFormat = new SimpleDateFormat("MM/dd/yyyy").parse(result.getDayExpired());
		// Format date thành string với format yyyy-MM-dd để hiện lên trang web
		String newDateString = new SimpleDateFormat("yyyy-MM-dd").format(newDateFormat);
		result.setDayExpired(newDateString);

		// Tiến hành xử lý các list null và cắt chuỗi id
		// Chuyển đổi null list floor thành empty list floor
		if (result.getListFloor() == null) {
			result.setListFloor(new ArrayList<FloorDTO>());
		} else {
			for (int i = 0; i < result.getListFloor().size(); i++) {
				// Cắt id cho floor
				result.getListFloor().get(i).setId(idTrimmer(result.getListFloor().get(i).getId()));
				if (result.getListFloor().get(i).getListLocation() == null) {
					// Chuyển đổi null list location thành empty list location
					result.getListFloor().get(i).setListLocation(new ArrayList<LocationDTO>());
				} else {
					for (int j = 0; j < result.getListFloor().get(i).getListLocation().size(); j++) {
						// Cắt id cho location
						result.getListFloor().get(i).getListLocation().get(j).setId(idTrimmer(result.getListFloor().get(i).getListLocation().get(j).getId()));

						// Chuyển đổi null list neighbour thành empty list neighbour
						if (result.getListFloor().get(i).getListLocation().get(j).getListLocationBeside() == null) {
							result.getListFloor().get(i).getListLocation().get(j).setListLocationBeside(new ArrayList<NeighbourDTO>());
						} else {
							for (int k = 0; k < result.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().size(); k++) {
								// Cắt id cho neighbour
								result.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k)
										.setId(idTrimmer(result.getListFloor().get(i).getListLocation().get(j).getListLocationBeside().get(k).getId()));
							}
						}

						// Chuyển đổi null list room thành empty list room
						if (result.getListFloor().get(i).getListLocation().get(j).getListRoom() == null) {
							result.getListFloor().get(i).getListLocation().get(j).setListRoom(new ArrayList<RoomDTO>());
						} else {
							for (int k = 0; k < result.getListFloor().get(i).getListLocation().get(j).getListRoom().size(); k++) {
								// Cắt id cho room
								result.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k)
										.setId(idTrimmer(result.getListFloor().get(i).getListLocation().get(j).getListRoom().get(k).getId()));
							}
						}
					}
				}
			}
		}
		return result;

	}

	public List<CompanyDTO> parseToCompanyObject(String getUrl, RestTemplate restTemplate) {
		List<CompanyDTO> result = new ArrayList<CompanyDTO>();
		List<BuildingDTO> listOfBuildings = new ArrayList<BuildingDTO>();

		// Gọi api và lấy chuỗi kết quả trả về
		String returnJsonString = restTemplate.getForObject(getUrl, String.class);

		// Parse chuỗi json thành object json
		JSONObject dataObject = new JSONObject(returnJsonString);

		// Lấy thông tin của các công ty nằm trong json object
		JSONArray companyData = dataObject.getJSONArray("Data");

		for (int i = 0; i < companyData.length(); i++) {
			// Lấy thông tin của công ty
			JSONObject companyObj = companyData.getJSONObject(i);

			// Lấy thông tin các tòa nhà thuộc công ty
			JSONArray buildingData = companyObj.getJSONArray("ListBuilding");

			listOfBuildings = new ArrayList<BuildingDTO>();

			for (int j = 0; j < buildingData.length(); j++) {
				// Lấy thông tin của tòa nhà
				JSONObject buildingObj = buildingData.getJSONObject(j);

				BuildingDTO buildingDTO = new BuildingDTO(buildingObj.getString("Id"), buildingObj.getString("Name"));

				// Thêm tòa nhà vào danh sách
				listOfBuildings.add(buildingDTO);
			}

			CompanyDTO companyDTO = new CompanyDTO(companyObj.getString("Id"), companyObj.getString("Name"), listOfBuildings);

			// Thêm công ty vào danh sách
			result.add(companyDTO);
		}

		return result;
	}

	// Cắt chuỗi id để lấy id sơ khai
	private final String idTrimmer(String id) {
		String result = null;
		result = StringUtils.substringAfterLast(id, "_");
		return result;
	}
}
