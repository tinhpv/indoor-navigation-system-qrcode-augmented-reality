package duyvm.capstone_web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import duyvm.capstone_web.daos.LocationDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.utils.Utilities;

@Controller
@RequestMapping("/location")
public class LocationController {

	@GetMapping("/create")
	public String getCreateLocation(@RequestParam("floorId") String floorId, HttpSession session) {
		try {
			Utilities utilities = new Utilities();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Tìm floor object dựa theo id
			FloorDTO floorDTO = utilities.getFloorById(floorId, buildingDTO);

			// Đặt floor object vào session
			session.setAttribute("floor", floorDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getCreateLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "createLocation.jsp";
	}

	@PostMapping("/create")
	public String postCreateLocation(LocationDTO locationInfo, HttpSession session, HttpServletRequest request) {
		try {
			LocationDAO locationDAO = new LocationDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Lấy floor object có trong session
			FloorDTO floorDTO = (FloorDTO) session.getAttribute("floor");

			// Tạo một location object mới
			buildingDTO = locationDAO.createLocation(buildingDTO, floorDTO, locationInfo);

			// Cập nhật building object trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("createSuccess", "Location created locally.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postCreateLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "createLocation.jsp";
	}

	@PostMapping("/remove")
	public String postRemoveLocation(@RequestParam("id") String locationId, HttpSession session,
			HttpServletRequest request) {
		try {
			LocationDAO locationDAO = new LocationDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Gỡ bỏ location object
			buildingDTO = locationDAO.removeLocation(locationId, buildingDTO);

			// Gỡ bỏ những neighbour object có liên kết với location object được chọn
			buildingDTO = locationDAO.removeNeighbourById(locationId, buildingDTO);

			// Cập nhật building trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("removeSuccess", "Location removed locally.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postRemoveLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

	@GetMapping("/edit")
	public String getEditLocation(@RequestParam("floorId") String floorId,
			@RequestParam("locationId") String locationId, HttpSession session, HttpServletRequest request) {
		try {
			Utilities utilities = new Utilities();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Tìm floor object dựa theo id
			FloorDTO floorDTO = utilities.getFloorById(floorId, buildingDTO);

			// Tìm location object dựa theo id
			LocationDTO locationDTO = utilities.getLocationById(locationId, buildingDTO);

			// Thêm floor object và location object vào request
			request.setAttribute("floor", floorDTO);
			request.setAttribute("location", locationDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getEditLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "updateLocation.jsp";
	}

	@PostMapping("/edit")
	public String postEditLocation(LocationDTO locationInfo, HttpSession session, HttpServletRequest request) {
		try {
			LocationDAO locationDAO = new LocationDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Cập nhật thông tin của location object
			buildingDTO = locationDAO.updateLocation(locationInfo, buildingDTO);

			// Cập nhật thông tin tất cả neighbour object có liên kết với location được chọn
			buildingDTO = locationDAO.updateAllNeighbourName(locationInfo.getId(), locationInfo.getName(), buildingDTO);

			// Cập nhật building trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("editSuccess", "Location info updated locally.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postEditLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}
}
