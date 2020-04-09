package duyvm.capstone_web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import duyvm.capstone_web.daos.FloorDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;

@Controller
@RequestMapping("/floor")
public class FloorController {

	@Autowired
	RestTemplate restTemplate;

	@GetMapping({ "/", "" })
	public String showFloorPage() {
		return "floor.jsp";
	}

	@PostMapping({ "/", "" })
	public String createFloor(@RequestParam("mapFile") MultipartFile mapFile, FloorDTO floorInfo, HttpSession session,
			HttpServletRequest request) {
		try {
			FloorDAO floorDAO = new FloorDAO();

			// Lấy building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			String postUrl = "http://13.229.117.90:7070/api/INQR/createNewFloor";
			ResponseEntity<String> result = floorDAO.importBuildingFloor(mapFile, buildingDTO, floorInfo, restTemplate,
					postUrl);

			if (result.getStatusCode() == HttpStatus.OK) {
				buildingDTO = floorDAO.createFloor(result.getBody(), buildingDTO, floorInfo);
				// Cập nhật building trong session
				session.setAttribute("building", buildingDTO);

				// Hiện thông báo
				request.setAttribute("createSuccess", "Floor created successfully.");
			} else {
				return "error.jsp";
			}

			// Kiểm tra người dùng có chọn hình ảnh hay không
//			if (mapFile.getOriginalFilename() != null && !mapFile.getOriginalFilename().isEmpty()) {
//				// Tạo object floor với đường dẫn file hình ảnh
//				buildingDTO = floorDAO.createFloor(mapFile, buildingDTO, floorInfo);
//			} else {
//				// Tạo object floor ko có đường dẫn file hình ảnh
//				buildingDTO = floorDAO.createFloor(null, buildingDTO, floorInfo);
//			}

//			buildingDTO = floorDAO.createFloor(null, buildingDTO, floorInfo);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Error at createFloor: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

//	public static File convert(MultipartFile file) {
//		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
//		File convFile = new File("TênFile" + "." + fileExtension);
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

	@GetMapping("/floorDetail")
	public String getFloorDetail(@RequestParam("id") String floorId, HttpSession session) {
		try {
			FloorDAO floorDAO = new FloorDAO();

			// Lấy building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Lấy object floor dựa vào id
			FloorDTO floorDTO = floorDAO.getFloorBaseOnId(floorId, buildingDTO);

			// Đặt object floor vào session
			session.setAttribute("floor", floorDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Error at getFloorDetail: " + e.getMessage());
			return "error.jsp";
		}
		return "floorDetail.jsp";
	}

	@PostMapping("removeFloor")
	public String removeFloor(@RequestParam("id") String floorId, HttpSession session, HttpServletRequest request) {
		try {
			FloorDAO floorDAO = new FloorDAO();

			// Lấy building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Xóa object floor khỏi object building
			buildingDTO = floorDAO.removeFloor(floorId, buildingDTO);

			// Cập nhật building trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("deleteSuccess", "Floor removed successfully.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at RemoveFloor: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

	@PostMapping("edit")
	public String editFloor(@RequestParam("fileMap") MultipartFile fileMap, FloorDTO floorInfo, HttpSession session,
			HttpServletRequest request) {
		try {
			FloorDAO floorDAO = new FloorDAO();

			// Lấy building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Kiểm tra người dùng có chọn file hình ảnh ko
			if (fileMap.getOriginalFilename() != null && !fileMap.getOriginalFilename().isEmpty()) {
				// Update thông tin của floor trong object building bao gồm cả hình ảnh
				buildingDTO = floorDAO.updateFloor(fileMap, floorInfo, buildingDTO);
			} else {
				// Update thông tin của floor trong object building không bao gồm file hình ảnh
				buildingDTO = floorDAO.updateFloor(null, floorInfo, buildingDTO);
			}

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("updateSuccess", "Building info changed successfully.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at EditFloor: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}
}
