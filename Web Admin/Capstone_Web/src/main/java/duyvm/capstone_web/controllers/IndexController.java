package duyvm.capstone_web.controllers;

import java.awt.PageAttributes.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.CompanyDTO;
import duyvm.capstone_web.utils.JsonParser;
import duyvm.capstone_web.utils.Utilities;

@Controller
public class IndexController {

	@Autowired
	RestTemplate restTemplate;

	@GetMapping(value = "/test", produces = "application/zip")
	public void test(HttpServletResponse response, HttpSession session) throws IOException {
//		Stream<Path> walk = Files.walk(Paths.get("qrcode/fpt_demo"));
//		List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
//		walk.close();
//
//		ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
//		for (int i = 0; i < result.size(); i++) {
//			FileSystemResource resource = new FileSystemResource(result.get(i));
//			ZipEntry zipEntry = new ZipEntry(resource.getFilename());
//			zipEntry.setSize(resource.contentLength());
//			zipOut.putNextEntry(zipEntry);
//			StreamUtils.copy(resource.getInputStream(), zipOut);
//			zipOut.closeEntry();
//		}
//		zipOut.finish();
//		zipOut.close();
//		response.setStatus(HttpServletResponse.SC_OK);
//		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "duy.zip");
//		response.setHeader("Content-Disposition", "attachment; filename=" + "1.zip");
		
		
		File file = new File(System.getProperty("java.io.tmpdir"), "qrcode/");
		System.out.println("File path: " + file.getAbsolutePath());
	}

	@GetMapping("/downloadQrCode/{buildingName}")
	public ResponseEntity<Object> getBuildingQrCodes(@RequestParam("buildingId") String buildingId,
			HttpServletResponse response) throws Exception {
		JsonParser jsonParser = new JsonParser();
		Utilities utilities = new Utilities();

		// Rest api
		String getUrl = "http://13.229.117.90:7070/api/INQR/getAllLocations?buildingId=";
		getUrl += buildingId;

		// Lấy thông tin tòa nhà và parse thành building object
		BuildingDTO buildingDTO = jsonParser.parseToBuildingObject(getUrl, restTemplate);

		// Convert tất cả các file qr về dưới local để compress
		File qrCodeDir = utilities.converQrCodeFile(buildingDTO);

		Stream<Path> walk = Files.walk(Paths.get(qrCodeDir.getAbsolutePath() + "/"));
		List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
		walk.close();

		ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
		for (int i = 0; i < result.size(); i++) {
			FileSystemResource resource = new FileSystemResource(result.get(i));
			ZipEntry zipEntry = new ZipEntry(resource.getFilename());
			zipEntry.setSize(resource.contentLength());
			zipOut.putNextEntry(zipEntry);
			StreamUtils.copy(resource.getInputStream(), zipOut);
			zipOut.closeEntry();
		}
		zipOut.finish();
		zipOut.close();
		response.setStatus(HttpServletResponse.SC_OK);
//			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
//					"attachment; filename=\"" + buildingDTO.getName() + ".zip" + "\"");
		response.setHeader("Content-disposition", "attachment; filename=\"" + "download.zip" + "");

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@GetMapping({ "/", "" })
	public String showIndexPage(HttpSession session) {
		try {
			JsonParser jsonParser = new JsonParser();

			String getUrl = "http://13.229.117.90:7070/api/INQR/getAllBuildings";

			List<CompanyDTO> companyList = jsonParser.parseToCompanyObject(getUrl, restTemplate);

			session.setAttribute("companyList", companyList);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getAllCompany: " + e.getMessage());
		}
		return "index.jsp";
	}

	@GetMapping("/getAllCompany")
	public String getAllCompany(HttpSession session) {
		try {
			JsonParser jsonParser = new JsonParser();

			String getUrl = "http://13.229.117.90:7070/api/INQR/getAllBuildings";

			List<CompanyDTO> companyList = jsonParser.parseToCompanyObject(getUrl, restTemplate);

			session.setAttribute("companyList", companyList);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error at getAllCompany: " + e.getMessage());
		}
		return "index.jsp";
	}

//
//	@GetMapping({ "/getJson" })
//	public ResponseEntity<Object> getJsonString(HttpSession session) throws IOException {
//		File tempFile = new File("test.json");
//		FileWriter fileWriter = new FileWriter(tempFile);
//		try {
//			BuildingDTO buildingDTO = (BuildingDTO) session.getAttribute("building");
//
//			BuildingDTO parseObject = buildingDTO;
//
//			// Chỉnh lại ngày từ "yyyy-MM-dd" thành "MM/dd/yyyy"
//			// Parse String thành date
//			Date newDateFormat = new SimpleDateFormat("yyyy-MM-dd").parse(parseObject.getDayExpired());
//			// Format Date thành String và lưu vào building object
//			String newDateString = new SimpleDateFormat("MM/dd/yyyy").format(newDateFormat);
//			parseObject.setDayExpired(newDateString);
//
//			if (parseObject != null) {
//				Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
////				System.out.println("Json String: " + gson.toJson(buildingDTO).toString());
//
//				// Code duoi day phu trach viec tao file
//				fileWriter.write(gson.toJson(parseObject).toString());
//				fileWriter.flush();
//
//				InputStreamResource resrouce = new InputStreamResource(new FileInputStream(tempFile));
//				HttpHeaders httpHeaders = new HttpHeaders();
//				httpHeaders.add("Content-Disposition",
//						String.format("attachment; filename=\"%s\"", tempFile.getName()));
//				httpHeaders.add("Cache-Controll", "no-cache, no-store, must-revalidate");
//				httpHeaders.add("Pragma", "no-cache");
//				httpHeaders.add("Expires", "0");
//
//				ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(httpHeaders)
//						.contentLength(tempFile.length()).contentType(MediaType.parseMediaType("application/txt"))
//						.body(resrouce);
//
//				return responseEntity;
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Error at getJsonString: " + e.getMessage());
//		} finally {
//			if (fileWriter != null) {
//				fileWriter.close();
//			}
//		}
//		return new ResponseEntity<Object>("Error occured!", HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//
//	@PostMapping("/readString")
//	public String readJsonString(@RequestParam("jsonString") String jsonString, HttpSession session) {
//		try {
//			Gson gson = new Gson();
//			BuildingDTO buildingDTO = gson.fromJson(jsonString, BuildingDTO.class);
//			session.setAttribute("building", buildingDTO);
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Error at ReadJsonString: " + e.getMessage());
//		}
//		return "index.jsp";
//	}
}
