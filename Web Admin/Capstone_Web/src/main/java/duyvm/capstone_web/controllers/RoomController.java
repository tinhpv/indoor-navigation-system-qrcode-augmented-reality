package duyvm.capstone_web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import duyvm.capstone_web.daos.RoomDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.RoomDTO;

@Controller
@RequestMapping("/room")
public class RoomController {

	@GetMapping("/map")
	public String showRoomLocationPage() {
		return "roomPoints.jsp";
	}

	@PostMapping({ "/", "" })
	public String createRoom(@RequestParam("locationId") String locationId, RoomDTO roomInfo, HttpSession session,
			HttpServletRequest request) {
		try {
			RoomDAO roomDAO = new RoomDAO();

			// Lấy object Building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Lấy object Location từ session
//			LocationDTO locationDTO = (LocationDTO) session.getAttribute("location");

			// Tạo phòng mới
			buildingDTO = roomDAO.createRoom(buildingDTO, locationId, roomInfo);

			// Cập nhật thông tin cho session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("createSuccess", "Tạo phòng thành công");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at CreateRoom: " + e.getMessage());
			return "error.jsp";
		}
		return "roomPoints.jsp";
	}

	@PostMapping("/remove")
	public String removeRoom(@RequestParam("roomId") String roomId, @RequestParam("currentPage") String currentPage,
			HttpSession session, HttpServletRequest request) {
		String returnString = "";
		try {
			RoomDAO roomDAO = new RoomDAO();

			// Lấy Object Building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Xóa Object Room
			buildingDTO = roomDAO.removeRoomFromLocation(roomId, buildingDTO);

			// Cập nhật dữ liệu trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("deleteSuccess", "Xóa phòng thành công.");

			switch (currentPage) {
			case "floorDetail":
				returnString = "floorDetail.jsp";
				break;
			case "floor":
				returnString = "location.jsp";
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at RemoveRoom: " + e.getMessage());
			return "error.jsp";
		}

		return returnString;
	}

}
