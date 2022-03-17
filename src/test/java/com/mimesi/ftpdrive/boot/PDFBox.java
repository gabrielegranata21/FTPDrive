package com.mimesi.ftpdrive.boot;

import com.itextpdf.text.pdf.PdfReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFBox {

    @Test
    public void testPDFUnion() throws IOException {
        List<File> listNazFile = new ArrayList<File>();
        final String path = "\\\\192.168.0.172\\pdftemp\\calderonepdf\\FTPDrive\\TuttoSport\\merge";
        final File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }

        final String pathFromFiles = "\\\\192.168.0.172\\pdftemp\\calderonepdf\\FTPDrive\\TuttoSport\\20220317";
        final File filesPAth = new File(pathFromFiles);
        final String[] filenameList = filesPAth.list();

        for(int i=0;i<filenameList.length;i++) {
            System.out.println(filenameList[i]);
            // ACQUISIAMO IL TT NAZIONALE
            Pattern pattern = Pattern.compile("[A-Z]{3}NAZTUT[0-9]{9}[A-Z]{4}@[0-9]{3}.pdf");
            Matcher match = pattern.matcher(filenameList[i]);
            while (match.find()){
                String matchGroup = match.group();
                System.out.println(matchGroup);
                listNazFile.add(new File(pathFromFiles+File.separatorChar+matchGroup));
            }
        }

        while (!listNazFile.isEmpty()) {

        }
        PdfReader reader = new PdfReader(pathFromFiles);
    }
}
