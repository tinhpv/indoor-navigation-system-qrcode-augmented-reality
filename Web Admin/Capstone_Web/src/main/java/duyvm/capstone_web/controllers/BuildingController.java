package duyvm.capstone_web.controllers;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import duyvm.capstone_web.daos.BuildingDAO;
import duyvm.capstone_web.daos.FloorDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.utils.JsonParser;
import duyvm.capstone_web.utils.Utilities;

@Controller
@RequestMapping("/building")
public class BuildingController {

	@Autowired
	RestTemplate restTemplate;

	@GetMapping({ "/", "" })
	public String showBuildingPage() {
		try {

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at showBuildingPage: " + e.getMessage());
		}
		return "building.jsp";
	}

	@GetMapping("/getBuilding")
	public String getBuildingInformation(@RequestParam("buildingId") String buildingId, HttpSession session)
			throws Exception {
		try {
			JsonParser jsonParser = new JsonParser();

			// Rest api
			String getUrl = "http://13.229.117.90:7070/api/INQR/getAllLocations?buildingId=";
			getUrl += buildingId;

			// Lấy thông tin tòa nhà và parse thành building object
			BuildingDTO buildingDTO = jsonParser.parseToBuildingObject(getUrl, restTemplate);

			// Gửi building object vào session
			session.setAttribute("building", buildingDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getBuildingInformation: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

	@GetMapping("/createBuilding")
	public String getCreateBuilding(@RequestParam("companyId") String companyId,
			@RequestParam("companyName") String companyName, HttpServletRequest request, HttpSession session) {
		try {
			session.invalidate();
			request.setAttribute("companyId", companyId);
			request.setAttribute("companyName", companyName);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getCreateBuilding: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

	@PostMapping("/createBuilding")
	public String postCreateBuilding(@RequestParam("companyId") String companyId, BuildingDTO buildingInfo,
			HttpSession session, HttpServletRequest request) throws ParseException {
		try {
			BuildingDAO buildingDAO = new BuildingDAO();

			// Tạo object building
			BuildingDTO buildingDTO = buildingDAO.createBuilding(buildingInfo);

			// Rest api
			String postUrl = "http://13.229.117.90:7070/api/INQR/createNewBuilding";

			// Tạo tòa nhà mới trên server
			ResponseEntity<String> response = buildingDAO.importBuilding(postUrl, companyId, buildingDTO, restTemplate);

			// Trả về mã 200 == thành công
			if (response.getStatusCodeValue() == 200) {
				// Cập nhật building trong session
				session.setAttribute("building", buildingDTO);

				// Hiện thông báo thành công
				request.setAttribute("createSuccess", "Building created.");
			} else {
				// Hiện thông báo thất bại
				request.setAttribute("createSuccess", "Failed at create building.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at createBuilding: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

//	@PostMapping("/importBuilding")
//	public String importBuilding(HttpSession session) {
//		BuildingDAO buildingDAO = new BuildingDAO();
//
//		BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");
//
//		String postUrl = "http://13.229.117.90:7070/api/INQR/createNewBuilding";
//
//		String response = buildingDAO.importBuilding(postUrl, buildingDTO, restTemplate);
//
//		System.out.println("Response: " + response);
//
////		if (response.equals("OK")) {
//		postUrl = "http://13.229.117.90:7070/api/INQR/uploadFloorMap";
//
//		FloorDAO floorDAO = new FloorDAO();
//		floorDAO.importFloorMapToServer(postUrl, buildingDTO, restTemplate);
////		}
//
//		return "building.jsp";
//	}

	@PostMapping("/updateBuilding")
	public String updateBuildingToServer(HttpSession session, HttpServletRequest request) {
		try {
			String messageString = "";
			BuildingDAO buildingDAO = new BuildingDAO();
			Utilities utilities = new Utilities();

			// Lấy building object trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Rest api
			String postUrl = "http://13.229.117.90:7070/api/INQR/updateBuilding";

			// Cập nhật thông tin tòa nhà trên server
			ResponseEntity<String> response = buildingDAO.updateBuilding(postUrl, buildingDTO, restTemplate);

			// Cập nhật thông tin thành công thì cập nhật ảnh của các lầu
			if (response.getStatusCodeValue() == 200 && utilities.checkForChangedImage(buildingDTO)) {
				messageString += "Building update successfully.";

				// Rest api
				postUrl = "http://13.229.117.90:7070/api/INQR/uploadFloorMap";

				FloorDAO floorDAO = new FloorDAO();

				// Cập nhật thông tin ảnh của các lầu
				response = floorDAO.importFloorMapToServer(postUrl, buildingDTO, restTemplate);

				// Thông báo cho việc cập nhật hình ảnh các lầu
				if (response.getStatusCodeValue() == 200) {
					messageString += " Floor image update successfully.";
				} else {
					messageString += " Failed to update floor image";
				}
			} else if (response.getStatusCodeValue() == 200 && !utilities.checkForChangedImage(buildingDTO)) {
				messageString += "Building update successfully.";
			} else {
				messageString += "Failed to update building.";
			}

			System.out.println(messageString);

			// Hiện thông báo
			request.setAttribute("updateMessage", messageString);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Error at updateBuildingToserver: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

	@PostMapping("/edit")
	public String editBuilding(BuildingDTO buildingInfo, HttpSession session, HttpServletRequest request) {
		try {
			BuildingDAO buildingDAO = new BuildingDAO();

			// Lấy building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Cập nhật thông tin
			buildingDTO = buildingDAO.editBuilding(buildingInfo, buildingDTO);

			// Cập nhật building trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("updateSuccess", "Cập nhật thông tin tòa nhà thành công");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at EditBuilding: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

}
