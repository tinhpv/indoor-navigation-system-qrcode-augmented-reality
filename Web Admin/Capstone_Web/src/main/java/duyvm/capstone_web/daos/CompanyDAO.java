package duyvm.capstone_web.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.CompanyDTO;

public class CompanyDAO {

	public CompanyDTO createCompany(String companyName) {
		CompanyDTO result = null;

		// ID của company được randomize bằng UUID
		String companyId = UUID.randomUUID().toString();

		result = new CompanyDTO(companyId, companyName, new ArrayList<BuildingDTO>());

		return result;
	}

	public ResponseEntity<String> importCompanyToServer(String postUrl, CompanyDTO companyDTO,
			RestTemplate restTemplate) {

		// Tạo header cho request
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		// Tạo body cho request
		HttpEntity<CompanyDTO> entity = new HttpEntity<CompanyDTO>(companyDTO, httpHeaders);

		// Exchange method = POST
		ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.POST, entity, String.class);

		return response;
	}

	public ResponseEntity<String> updateCompanyToServer(String putUrl, CompanyDTO companyInfo,
			RestTemplate restTemplate) {

		// Tạo header cho request
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		// Tạo body cho request
		HttpEntity<CompanyDTO> entity = new HttpEntity<CompanyDTO>(companyInfo, httpHeaders);

		// Exchange method = PUT
		ResponseEntity<String> response = restTemplate.exchange(putUrl, HttpMethod.PUT, entity, String.class);

		return response;
	}
}
