package com.mimesi.ftpdrive.boot;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class FtpDriveApplicationTests {

	@Test
	void contextLoads() {

		String datePatternYear = defineDate("yyyyMM");
		String datePatternDay = defineDate("dd");

		System.out.println("\\\\192.168.0.172\\pdftemp\\"+ datePatternYear + "\\" + datePatternDay);
	}

	String defineDate (final String pattern){
		SimpleDateFormat formFile = new SimpleDateFormat(pattern);
		Date date = new Date();
		String datePatternFolder = formFile.format(date);
		return datePatternFolder;
	}

	@Test
	void moveFile () throws IOException {
		final Path fromPath = Paths.get("\\\\192.168.0.172\\pdftemp\\calderonepdf\\ClassEditore\\ITALIAOGGI\\0216_binpage1.pdf");

		final File file = new File("\\\\192.168.0.172\\pdftemp\\Batches\\todo\\202202\\24\\");
		if(!file.exists()){
			file.mkdirs();
		}

		final Path toPath = Paths.get("\\\\192.168.0.172\\pdftemp\\Batches\\todo\\202202\\24\\"+fromPath.getFileName());

		Files.copy(fromPath,toPath);

	}

	private void httpRequestPromopress() {
		try {
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			URLConnection request = new URL("https://promopress.kataweb.it/download/mattinopadova/20211118.zip").openConnection();
			System.setProperty("http.maxRedirects", "100");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
