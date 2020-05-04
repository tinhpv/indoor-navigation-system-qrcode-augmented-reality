package duyvm.capstone_web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import duyvm.capstone_web.daos.RoomDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.FloorDTO;
import duyvm.capstone_web.dtos.LocationDTO;
import duyvm.capstone_web.dtos.RoomDTO;
import duyvm.capstone_web.utils.Utilities;

@Controller
@RequestMapping("/room")
public class RoomController {
	
	@GetMapping("/create")
	public String getCreateRoom(@RequestParam("floorId") String floorId, @RequestParam("locationId") String locationId,
			HttpSession session) {
		try {
			Utilities utilities = new Utilities();

			// Lấy building object có trong session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Tìm floor object dựa theo floor id
			FloorDTO floorDTO = utilities.getFloorById(floorId, buildingDTO);

			// Tìm location object dựa theo location id
			LocationDTO locationDTO = utilities.getLocationById(locationId, buildingDTO);

			// Đặt floor object và location object vào session
			session.setAttribute("floor", floorDTO);
			session.setAttribute("location", locationDTO);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getCreateRoom: " + e.getMessage());
			return "error.jsp";
		}
		return "createRoom.jsp";
	}

	@PostMapping("/create")
	public String postCreateRoom(@RequestParam("locationId") String locationId, RoomDTO roomInfo, HttpSession session,
			HttpServletRequest request) {
		try {
			RoomDAO roomDAO = new RoomDAO();
			Utilities utilities = new Utilities();

			// Lấy building object từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Kiểm tra tên room có bị trùng không
			if (!utilities.checkExistedRoom(roomInfo, buildingDTO)) {
				// Tạo room object
				buildingDTO = roomDAO.createRoom(buildingDTO, locationId, roomInfo);

				// Cập nhật thông tin cho session
				session.setAttribute("building", buildingDTO);

				// Hiện thông báo
				request.setAttribute("createSuccess", "Room created locally.");
			} else {
				// Hiện thông báo
				request.setAttribute("createFailed", "\"" + roomInfo.getName() + "\"" + " already exist!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postCreateRoom: " + e.getMessage());
			return "error.jsp";
		}
		return "createRoom.jsp";
	}

	@PostMapping("/remove")
	public String postRemoveRoom(@RequestParam MultiValueMap<String, String> map, HttpSession session,
			HttpServletRequest request) {
		try {
			RoomDAO roomDAO = new RoomDAO();

			// Lấy building object từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Key của modal
			String modalKey = map.getFirst("modalKey");

			// Danh sách id của các room
			List<String> listRoomIds = map.get("roomGroup" + modalKey);

			for (int i = 0; i < listRoomIds.size(); i++) {
				// Xóa room object dựa theo room id
				buildingDTO = roomDAO.removeRoomById(listRoomIds.get(i), buildingDTO);
			}

			// Cập nhật dữ liệu trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("removeSuccess", "Successfully remove " + listRoomIds.size() + " room(s).");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postRemoveRoom: " + e.getMessage());
			return "error.jsp";
		}
		return "floor.jsp";
	}

}
