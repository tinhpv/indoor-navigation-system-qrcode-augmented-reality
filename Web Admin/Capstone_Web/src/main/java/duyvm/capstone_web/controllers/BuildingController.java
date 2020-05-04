package duyvm.capstone_web.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
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
	private RestTemplate restTemplate;

	@GetMapping({ "/", "" })
	public String getBuildingPage() {
		try {
			// To do
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getBuildingPage: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

	@GetMapping("/getBuilding")
	public String getBuildingInformation(@RequestParam("buildingId") String buildingId, HttpSession session) {
		try {
			JsonParser jsonParser = new JsonParser();

			// API getAllLocations
			String getUrl = "http://13.229.117.90:7070/api/INQR/getAllLocations?buildingId=";
			getUrl += buildingId;

			// Parse chuỗi json trả về thành 1 building object và đưa vào session
			BuildingDTO buildingDTO = jsonParser.parseToBuildingObject(getUrl, restTemplate);
			session.setAttribute("building", buildingDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getBuildingInformation: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

	@PostMapping("/create")
	public String postCreateBuilding(@RequestParam("companyId") String companyId, BuildingDTO buildingInfo, HttpSession session, HttpServletRequest request) {
		try {
			BuildingDAO buildingDAO = new BuildingDAO();

			// API createNewBuilding
			String postUrl = "http://13.229.117.90:7070/api/INQR/createNewBuilding";

			// Tạo building object
			BuildingDTO buildingDTO = buildingDAO.createBuilding(buildingInfo);

			// Đẩy building object lên server
			ResponseEntity<String> response = buildingDAO.importBuildingToServer(postUrl, companyId, buildingDTO, restTemplate);

			// Respond code 200 => OK
			if (response.getStatusCodeValue() == 200) {

				// Hiện thông báo
				request.setAttribute("createSuccess", "Building successfully created on server");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postCreateBuilding: " + e.getMessage());
			return "error.jsp";
		}
		return "index.jsp";
	}

	@GetMapping("/upload")
	public String getBuildingUpload() {
		try {
			// To do
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getBuildingUpload: " + e.getMessage());
			return "error.jsp";
		}
		return "upload.jsp";
	}

	@PostMapping("/upload")
	public String postBuildingUpload(HttpSession session, HttpServletRequest request) {
		try {
			String messageString = "";

			FloorDAO floorDAO = new FloorDAO();
			BuildingDAO buildingDAO = new BuildingDAO();
			Utilities utilities = new Utilities();

			// API updateBuilding
			String putUrl = "http://13.229.117.90:7070/api/INQR/updateBuilding";

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Đẩy building object lên server để cập nhật
			ResponseEntity<String> response = buildingDAO.updateBuilding(putUrl, buildingDTO, restTemplate);

			// Code value 200 => OK
			// Sau khi cập nhật thành công, kiểm tra xem người dùng có đổi bản đồ các lầu
			if (response.getStatusCodeValue() == 200 && utilities.checkForChangedImage(buildingDTO)) {
				messageString += "Successfully update building information ";

				// API uploadFloorMap
				putUrl = "http://13.229.117.90:7070/api/INQR/uploadFloorMap";

				// Cập nhật bản đồ các lầu mà người dùng thay đổi
				response = floorDAO.importFloorMapToServer(putUrl, buildingDTO, restTemplate);

				// Code 200 => OK
				// Thông báo cho việc cập nhật hình ảnh các lầu
				if (response.getStatusCodeValue() == 200) {
					messageString += "and image of floor(s).";
				}
			} else if (response.getStatusCodeValue() == 200 && !utilities.checkForChangedImage(buildingDTO)) {
				messageString += "Successfully update building information.";
			}

			// Hiện thông báo
			request.setAttribute("uploadSuccess", messageString);

			// Remove session
			session.removeAttribute("building");
			session.removeAttribute("floor");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Error at postBuildingUpload: " + e.getMessage());
			return "error.jsp";
		}
		return "index.jsp";
	}

	@PostMapping("/edit")
	public String postEditBuilding(BuildingDTO buildingInfo, HttpSession session, HttpServletRequest request) {
		try {
			BuildingDAO buildingDAO = new BuildingDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Cập nhật thông tin của building object ở local và đẩy vào session
			buildingDTO = buildingDAO.editBuilding(buildingInfo, buildingDTO);
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("editSuccess", "Building's information changed locally.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postEditBuilding: " + e.getMessage());
			return "error.jsp";
		}
		return "building.jsp";
	}

	@GetMapping("/getQRCode/{buildingName}")
	public ResponseEntity<Object> getBuildingQrCodes(@RequestParam("id") String buildingId, HttpServletResponse response) {
		try {
			JsonParser jsonParser = new JsonParser();
			Utilities utilities = new Utilities();

			// Rest api
			String getUrl = "http://13.229.117.90:7070/api/INQR/getAllLocations?buildingId=";
			getUrl += buildingId;

			// Lấy thông tin tòa nhà và parse thành building object
			BuildingDTO buildingDTO = jsonParser.parseToBuildingObject(getUrl, restTemplate);

			// Convert tất cả các file qr về dưới local để compress
			File qrCodeDir = utilities.converQrCodeFile(buildingDTO);

			Stream<Path> walk = Files.walk(Paths.get(qrCodeDir.getAbsolutePath() + "/"));
			List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
			walk.close();

			ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
			for (int i = 0; i < result.size(); i++) {
				FileSystemResource resource = new FileSystemResource(result.get(i));
				ZipEntry zipEntry = new ZipEntry(resource.getFilename());
				zipEntry.setSize(resource.contentLength());
				zipOut.putNextEntry(zipEntry);
				StreamUtils.copy(resource.getInputStream(), zipOut);
				zipOut.closeEntry();
			}
			zipOut.finish();
			zipOut.close();
			System.out.print("OK");
//			response.setStatus(HttpServletResponse.SC_OK);
//			response.setHeader("Content-disposition", "attachment; filename=" + "download.zip");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getBuildingQrCode: " + e.getMessage());
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
}
