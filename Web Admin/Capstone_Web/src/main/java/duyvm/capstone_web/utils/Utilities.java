package duyvm.capstone_web.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import duyvm.capstone_web.dtos.BuildingDTO;
import duyvm.capstone_web.dtos.LocationDTO;

public class Utilities {

	public boolean checkForChangedImage(BuildingDTO buildingDTO) {
		boolean changedImage = false;
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			if (buildingDTO.getListFloor().get(i).getMapFilePath() != null
					&& !buildingDTO.getListFloor().get(i).getMapFilePath().isEmpty()) {
				changedImage = true;
			}
		}
		return changedImage;
	}

	public File convertMapFile(MultipartFile file, String floorId, String buildingId) {

		// Kiểm tra thư mục hình ảnh
		File imageDir = new File("img/");
		if (!imageDir.exists() || !imageDir.isDirectory()) {
			imageDir.mkdir();
			System.out.println("img directory created at: " + imageDir.getAbsolutePath());
		}

		File convFile = new File("img/" + buildingId + "_" + floorId + ".png");
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			System.out.println("File written: " + convFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convFile;
	}

	public File converQrCodeFile(BuildingDTO buildingDTO) throws IOException {

		// Kiểm tra thư mục qr code
		File qrcodeDir = new File(System.getProperty("java.io.tmpdir"), "qrcode/" + buildingDTO.getId());
		if (!qrcodeDir.exists() || !qrcodeDir.isDirectory()) {
			qrcodeDir.mkdirs();
			System.out.println("qrcode directory created at: " + qrcodeDir.getAbsolutePath());
		}

		System.out.println("1");

//		Path qrCodePath = Paths.get(qrcodeDir.getAbsolutePath());
//		
//		System.out.println("qrCodePath" + qrCodePath);
//
//		SeekableByteChannel destFileChannel = Files.newByteChannel(qrCodePath);
		// ...
//		destFileChannel.close(); // removing this will throw java.nio.file.AccessDeniedException:

		// Clear directory
		FileUtils.cleanDirectory(qrcodeDir);
		System.out.println("2");

		List<LocationDTO> buildingLocation = getAllBuildingLocation(buildingDTO);

		System.out.println("3");
		for (int i = 0; i < buildingLocation.size(); i++) {
			if (buildingLocation.get(i).getLinkQr() != null && buildingLocation.get(i).getName() != null) {
				InputStream in = new URL(buildingLocation.get(i).getLinkQr()).openStream();
				Files.copy(in, Paths.get(qrcodeDir + "/" + buildingLocation.get(i).getName() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
			}
		}
		System.out.println("4");

		return qrcodeDir;
	}

	private List<LocationDTO> getAllBuildingLocation(BuildingDTO buildingDTO) {
		List<LocationDTO> result = new ArrayList<LocationDTO>();
		for (int i = 0; i < buildingDTO.getListFloor().size(); i++) {
			for (int j = 0; j < buildingDTO.getListFloor().get(i).getListLocation().size(); j++) {
				result.add(buildingDTO.getListFloor().get(i).getListLocation().get(j));
			}
		}

		return result;
	}
}
