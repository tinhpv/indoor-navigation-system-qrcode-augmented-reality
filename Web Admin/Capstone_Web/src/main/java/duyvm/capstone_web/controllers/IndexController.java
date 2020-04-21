package duyvm.capstone_web.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class IndexController {

	@Autowired
	RestTemplate restTemplate;

	@GetMapping({ "/", "" })
	public String showIndexPage(HttpSession session) {
		try {
			// To do
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getAllCompany: " + e.getMessage());
			return "error.jsp";
		}
		// Redirect
		return "redirect:/company/getAllCompanies";
	}
}
