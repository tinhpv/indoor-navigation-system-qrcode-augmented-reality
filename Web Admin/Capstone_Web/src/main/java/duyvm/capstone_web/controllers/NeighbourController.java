package duyvm.capstone_web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import duyvm.capstone_web.daos.NeighbourDAO;
import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.NeighbourDTO;

@Controller
@RequestMapping("/neighbour")
public class NeighbourController {

	@PostMapping({ "/", "" })
	public String createNeighbour(@RequestParam("locationId") String locationId, NeighbourDTO neighbourInfo,
			HttpSession session, HttpServletRequest request) {
		try {
			NeighbourDAO neighbourDAO = new NeighbourDAO();

			// Lấy object Building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

//			 Lấy object Locaiton từ session
//			LocationDTO locationDTO = (LocationDTO) session.getAttribute("location");

			// Tạo object Neighbour mới trong Building
			buildingDTO = neighbourDAO.attachNeighbour(buildingDTO, locationId, neighbourInfo);

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("createSuccess", "Tạo liên hệ mới thành công.");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at CreateNeighbour: " + e.getMessage());
			return "error.jsp";
		}
		return "location.jsp";
	}

	@PostMapping("/remove")
	public String removeNeighbour(@RequestParam("locationId") String locationId,
			@RequestParam("neighbourId") String neighbourId, @RequestParam("currentPage") String currentPage,
			HttpSession session, HttpServletRequest request) {
		String returnString = "";
		try {
			NeighbourDAO neighbourDAO = new NeighbourDAO();

			// Lấy object building từ session
			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");

			// Xóa neighbour đã chọn khỏi object building
			buildingDTO = neighbourDAO.removeNeighbourFromLocation(locationId, neighbourId, buildingDTO);

			// Cập nhật thông tin trong session
			session.setAttribute("building", buildingDTO);

			// Hiện thông báo
			request.setAttribute("deleteSuccess", "Gỡ bỏ địa điểm liền kề thành công.");

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
			System.out.println("Error at RemoveNeighbour: " + e.getMessage());
			return "error.jsp";
		}

		return returnString;
	}

}
