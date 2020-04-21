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
	private RestTemplate restTemplate;

	@GetMapping({ "/", "" })
	public String showFloorPage() {
		try {
			// To do
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at showFloorPage: " + e.getMessage());
		}
		return "floor.jsp";
	}

	@PostMapping("/create")
	public String postCreateFloor(@RequestParam("mapFile") MultipartFile mapFile, FloorDTO floorInfo,
			HttpSession session, HttpServletRequest request) {
		try {
			FloorDAO floorDAO = new FloorDAO();
			
			//API createNewFloor
			String postUrl = "http://13.229.117.90:7070/api/INQR/createNewFloor";

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");
			
			// Đẩy thông tin của floor lên server để tạo mới
			ResponseEntity<String> result = floorDAO.importFloorToServer(mapFile, buildingDTO, floorInfo, restTemplate,
					postUrl);

			if (result.getStatusCode() == HttpStatus.OK) {
				// Nếu tạo mới thành công, result trả về sẽ là đường link hình ảnh của floor
				// Thêm mới một floor object vào building object
				buildingDTO = floorDAO.createFloor(result.getBody(), buildingDTO, floorInfo);
				
				// Cập nhật building trong session
				session.setAttribute("building", buildingDTO);

				// Hiện thông báo
				request.setAttribute("createSuccess", "Create floor successful on both server and local.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Error at postCreateFloor: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

	@PostMapping("/remove")
	public String postRemoveFloor(@RequestParam("id") String floorId, HttpSession session, HttpServletRequest request) {
		try {
			FloorDAO floorDAO = new FloorDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Xóa floor object khỏi building object dựa vào id của floor
			buildingDTO = floorDAO.removeFloor(floorId, buildingDTO);

			// Cập nhật building object trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("removeSuccess", "Floor removed locally.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postRemoveFloor: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

	@PostMapping("/edit")
	public String postEditFloor(@RequestParam("fileMap") MultipartFile fileMap, FloorDTO floorInfo, HttpSession session,
			HttpServletRequest request) {
		try {
			FloorDAO floorDAO = new FloorDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Kiểm tra người dùng có chọn file hình ảnh hay không
			if (fileMap.getOriginalFilename() != null && !fileMap.getOriginalFilename().isEmpty()) {
				// Update thông tin của floor object trong building object bao gồm cả file hình ảnh
				buildingDTO = floorDAO.editFloor(fileMap, floorInfo, buildingDTO);
			} else {
				// Update thông tin của floor object trong building object không bao gồm file hình ảnh
				buildingDTO = floorDAO.editFloor(null, floorInfo, buildingDTO);
			}

			// Cập nhật thông tin building trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("editSuccess", "Floor's information changed locally.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postEditFloor: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}
}
