package duyvm.capstone_web.controllers;

import java.util.List;

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

import duyvm.capstone_web.daos.CompanyDAO;
import duyvm.capstone_web.dtos.CompanyDTO;
import duyvm.capstone_web.utils.JsonParser;

@Controller
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/getAllCompanies")
	public String getAllCompanies(@RequestParam(value = "message", required = false) String message, HttpSession session, HttpServletRequest request) {
		try {
			JsonParser jsonParser = new JsonParser();

			// API getAllBuildings
			String getUrl = "http://13.229.117.90:7070/api/INQR/getAllBuildings";

			// Parse chuỗi json trả về thành 1 list các company objects và đưa vào session
			List<CompanyDTO> companyList = jsonParser.parseToCompanyObject(getUrl, restTemplate);
			session.setAttribute("companyList", companyList);
			
			if (message != null) {
				switch (message) {
				case "building_upload":
					request.setAttribute("uploadSuccess", "Successfully upload building.");
					break;

				case "building_create":
					request.setAttribute("createSuccess", "Building successfully created.");
					
				default:
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Error at getAllCompany: " + e.getMessage());
			return "error.jsp";
		}
		return "index.jsp";
	}

	@PostMapping("/create")
	public String postCreateCompany(@RequestParam("name") String companyName, HttpServletRequest request) {
		try {
			CompanyDAO companyDAO = new CompanyDAO();

			// API createNewCompany
			String postUrl = "http://13.229.117.90:7070/api/INQR/createNewCompany";

			// Tạo company object
			CompanyDTO companyDTO = companyDAO.createCompany(companyName);

			// Đẩy company object lên server
			ResponseEntity<String> response = companyDAO.importCompanyToServer(postUrl, companyDTO, restTemplate);

			// Code value 200 => OK
			if (response.getStatusCodeValue() == 200) {
				/* OK message */
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postCreateCompany: " + e.getMessage());
			return "error.jsp";
		}
		return "redirect:/company/getAllCompanies";
	}

	@PostMapping("/update")
	public String postUpdateCompany(CompanyDTO companyInfo, HttpServletRequest request) {
		try {
			CompanyDAO companyDAO = new CompanyDAO();

			// API updateCompany
			String putUrl = "http://13.229.117.90:7070/api/INQR/updateCompany";

			// Đẩy company object lên server để cập nhật
			ResponseEntity<String> responseEntity = companyDAO.updateCompanyToServer(putUrl, companyInfo, restTemplate);

			// Code value 200 => OK
			if (responseEntity.getStatusCodeValue() == 200) {
				/* OK message */
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at postUpdateCompany: " + e.getMessage());
			return "error.jsp";
		}
		return "redirect:/company/getAllCompanies";
	}

}
