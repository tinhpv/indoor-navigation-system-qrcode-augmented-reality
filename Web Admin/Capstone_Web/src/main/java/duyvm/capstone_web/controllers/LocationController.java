package duyvm.capstone_web.controllers;

import java.util.List;

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

@Controller
@RequestMapping("/location")
public class LocationController {

	@GetMapping("/map")
	public String showMapLocationPage() {
		try {
			//
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at ShowMapLocationPage: " + e.getMessage());
		}

		return "locationPoints.jsp";
	}

	@PostMapping({ "", "/" })
	public String createLocation(LocationDTO locationInfo, HttpSession session, HttpServletRequest request) {
		try {
			LocationDAO locationDAO = new LocationDAO();

			// Lấy Object Building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Lấy Object Floor từ session
			FloorDTO floorDTO = (FloorDTO) session.getAttribute("floor");

			// Tạo Object Floor mới và cập nhật Object Building
			buildingDTO = locationDAO.createLocation(buildingDTO, floorDTO, locationInfo);

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("createSuccess", "Tạo địa điểm thành công.");

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at createLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "locationPoints.jsp";
	}

	@GetMapping({ "/", "" })
	public String showLocation(@RequestParam("id") String locationId, HttpSession session) {
		try {
			LocationDAO locationDAO = new LocationDAO();
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");
			FloorDTO floorDTO = (FloorDTO) session.getAttribute("floor");
			List<LocationDTO> allLocations = locationDAO.getAllLocations(buildingDTO);
			LocationDTO locationDTO = locationDAO.getLocationBaseOnId(locationId, floorDTO);
			session.setAttribute("location", locationDTO);
			session.setAttribute("allLocations", allLocations);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at showLocation: " + e.getMessage() + ", " + e.getLocalizedMessage());
			return "error.jsp";
		}
		return "location.jsp";
	}

	@PostMapping("/remove")
	public String removeLocation(@RequestParam("id") String locationId, HttpSession session,
			HttpServletRequest request) {
		try {
			LocationDAO locationDAO = new LocationDAO();

			// Lấy Object Building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Xóa location khỏi Object floor
			buildingDTO = locationDAO.removeLocation(locationId, buildingDTO);

			// Xóa location khỏi tất cả những vị trí liền kề với nó
			buildingDTO = locationDAO.removeLocationFromNeighbour(locationId, buildingDTO);

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("deleteSuccess", "Đã xóa vị trí.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at RemoveLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "floorDetail.jsp";
	}

	@PostMapping("/edit")
	public String updateLocation(LocationDTO locationInfo, HttpSession session, HttpServletRequest request) {
		try {
			LocationDAO locationDAO = new LocationDAO();

			// Lấy Object Building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Cập nhật Object Building
			buildingDTO = locationDAO.updateLocation(locationInfo, buildingDTO);

			// Cập nhật thông tin tất cả Neighbour
			buildingDTO = locationDAO.updateAllNeighbourName(locationInfo.getId(), locationInfo.getName(), buildingDTO);

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("updateSuccess", "Cập nhật vị trí thành công.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at UpdateLocation: " + e.getMessage());
			return "error.jsp";
		}
		return "floorDetail.jsp";
	}
}
