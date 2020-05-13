package duyvm.capstone_web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import duyvm.capstone_web.daos.NeighbourDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;
import duyvm.capstone_web.utils.Utilities;

@Controller
@RequestMapping("/neighbour")
public class NeighbourController {

	@GetMapping("/create")
	public String getCreateNeighbour(@RequestParam("floorId") String floorId, @RequestParam("locationId") String locationId, HttpSession session) {
		try {
			Utilities utilities = new Utilities();
			List<LocationDTO> filteredList = new ArrayList<LocationDTO>();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Tìm floor object dựa theo floor id
			FloorDTO floorDTO = utilities.getFloorById(floorId, buildingDTO);

			// Tìm location object dựa theo location id
			LocationDTO locationDTO = utilities.getLocationById(locationId, buildingDTO);

			// Filter những neighbor mà location có thể attach
			filteredList = utilities.filterAvailableLocations(buildingDTO, floorDTO, locationDTO);

			// Đặt floor object, location object, filteredList vào session
			session.setAttribute("location", locationDTO);
			session.setAttribute("floor", floorDTO);
			session.setAttribute("filteredList", filteredList);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getCreateNeighbour: " + e.getMessage());
			return "error.jsp";
		}
		return "attachNeighbour.jsp";
	}

	@PostMapping("/create")
	public String postCreateNeighbour(@RequestParam("locationId") String locationId, NeighbourDTO neighbourInfo, HttpSession session, HttpServletRequest request) {
		try {
			NeighbourDAO neighbourDAO = new NeighbourDAO();
			Utilities utilities = new Utilities();

			// Lấy building object từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Tìm location object dựa theo location id
			LocationDTO locationDTO = utilities.getLocationById(locationId, buildingDTO);

			// Kiểm tra neighbour object đã tồn tại chưa
			if (!utilities.checkExistedNeighbour(neighbourInfo, locationDTO)) {
				// Tạo neighbour object mới trong
				buildingDTO = neighbourDAO.attachNeighbour(buildingDTO, locationId, neighbourInfo);

				// Cập nhật thông tin trong session
				session.setAttribute("building", buildingDTO);

				// Hiện thông báo
				request.setAttribute("createSuccess", "Neighbour created locally.");
			} else {
				// Hiện thông báo
				request.setAttribute("createFailed", "Failed to create, this location has already attach with " + neighbourInfo.getName());
				System.out.println("This relationship has already exist!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postCreateNeighbour: " + e.getMessage());
			return "error.jsp";
		}
		return "attachNeighbour.jsp";
	}

	@PostMapping("/remove")
	public String postRemoveNeighbour(@RequestParam MultiValueMap<String, String> map, HttpSession session, HttpServletRequest request) {
		try {
			NeighbourDAO neighbourDAO = new NeighbourDAO();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Key of modal
			String modalKey = map.getFirst("modalKey");

			// Location id
			String locationId = map.getFirst("locationId");

			// Danh sách ids của neighbour
			List<String> listNeighbourIds = map.get("neighbourGroup" + modalKey);

			for (int i = 0; i < listNeighbourIds.size(); i++) {
				// Gỡ bỏ neighbour đã chọn khỏi building object
				buildingDTO = neighbourDAO.removeNeighbourFromLocation(locationId, listNeighbourIds.get(i), buildingDTO);
			}

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("removeSuccess", "Successfully remove " + listNeighbourIds.size() + " neighbour(s).");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postRemoveNeighbour: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

	@PostMapping("/changeStatus")
	public String postChangeNeighbourStatus(@RequestParam MultiValueMap<String, String> map, HttpSession session, HttpServletRequest request) {
		try {
			NeighbourDAO neighbourDAO = new NeighbourDAO();
			Utilities utilities = new Utilities();
			boolean status;

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Key of modal
			String modalKey = map.getFirst("modalKey");

			// Location id
			String locationId = map.getFirst("locationId");

			// Danh sách ids của neighbour
			List<String> listNeighbourIds = map.get("neighbourGroup" + modalKey);

			for (int i = 0; i < listNeighbourIds.size(); i++) {
				// Lấy trạng thái của neighbour
				if (utilities.getNeighbourStatus(buildingDTO, locationId, listNeighbourIds.get(i))) {
					status = false;
				} else {
					status = true;
				}

				buildingDTO = neighbourDAO.changeNeighbourStatus(buildingDTO, locationId, listNeighbourIds.get(i), status);

				buildingDTO = neighbourDAO.changeNeighbourStatus(buildingDTO, listNeighbourIds.get(i), locationId, status);
			}

			session.setAttribute("building", buildingDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postDisableNeighbour: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}
}
