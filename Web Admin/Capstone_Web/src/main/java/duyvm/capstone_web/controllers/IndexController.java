package duyvm.capstone_web.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	
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
