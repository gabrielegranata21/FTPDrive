package com.mimesi.ftpdrive.boot;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PDFBox {

    public static final String REGEX_TUTTOSPORT_NAZIONALE = "[A-Z]{3}NAZTUT[0-9]{9}[A-Z]{4}@[0-9]{3}.pdf";
    public static final String REGEX_TUTTOSPORT_PIEMONTE = "[A-Z]{3}PIEMON[0-9]{9}[A-Z]{4}@[0-9]{3}.pdf";
    private static final byte[] buffer = new byte[100000];

    @Test
    public void testPDFUnion(){
        List<File> listNazFile = new ArrayList<File>();
        List<File> listPiemonteFile = new ArrayList<File>();
        final String path = "\\\\192.168.0.172\\pdftemp\\calderonepdf\\FTPDrive\\TuttoSport\\merge";
        final File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }

        final String pathFromFiles = "\\\\192.168.0.172\\pdftemp\\calderonepdf\\FTPDrive\\TuttoSport\\20220317";
        final File filesPAth = new File(pathFromFiles);
        final String[] filenameList = filesPAth.list();

        for(int i=0;i<filenameList.length;i++) {

            if(filenameList[i].matches(REGEX_TUTTOSPORT_NAZIONALE)) {
                System.out.println("Il file "+filenameList[i]+ " fa parte dell'edizione NAZIONALE");
                listNazFile.add(new File(pathFromFiles+File.separatorChar+filenameList[i]));
            } else if (filenameList[i].matches(REGEX_TUTTOSPORT_PIEMONTE)){
                System.out.println("Il file "+filenameList[i]+ " fa parte dell'edizione PIEMONTE");
                listPiemonteFile.add(new File(pathFromFiles+File.separatorChar+filenameList[i]));
            }

        }

        for(File singleFile:listNazFile) {
            // TTSNAZTUT170322101PRIA@000
            // Verifica pagina doppia, regola S-Z-T
            if (singleFile.getName().substring(21,22).equals("S") ||
                    singleFile.getName().substring(21,22).equals("T") ||
                        singleFile.getName().substring(21,22).equals("Z")) {
                System.out.println("File: "+singleFile.getName());
                System.out.println("PAGINA DOPPIA, DEVE ESSERE RITAGLIATA");
                final byte[] pdfBytes = readPDFFile(singleFile.getAbsolutePath());
                byte[][] tilePdf = tilePdf(pdfBytes);
                int numPage = Integer.parseInt(singleFile.getName().substring(16,18));
                for (byte[] currPag: tilePdf){
                    String fileNameToBeWrite = path + File.separatorChar + "0872_binpage" + numPage + ".pdf";
                    try {
                        write(currPag,fileNameToBeWrite);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    numPage ++;
                }
                // cut(pathFromFiles,singleFile.getName(),path,Integer.parseInt(singleFile.getName().substring(16,18)));
            } else {
                try{
                    final Path fromPath = Paths.get(singleFile.getAbsolutePath());
                    String fileFinalName = "0872_binpage"+singleFile.getName().substring(16,18)+".pdf";
                    final Path finalBatchFolder = Paths.get(path+File.separatorChar+fileFinalName);
                    Files.copy(fromPath,finalBatchFolder);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public static void write(byte[] data, String to) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new ByteArrayInputStream(data);
            out = new FileOutputStream(to);

            while (true) {
                synchronized (buffer) {
                    int amountRead = in.read(buffer);

                    if (amountRead == -1) {
                        break;
                    }

                    out.write(buffer, 0, amountRead);
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }
        }
    }

    /*
     * I/O SU DISCO
     */
    public static byte[] readPDFFile(String filename) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        byte[] data = {};
        try {
            fis = new FileInputStream(new File(filename));
            bis = new BufferedInputStream(fis);
            data = new byte[bis.available()];
            bis.read(data);
        } catch (FileNotFoundException fo) {
            System.out.print("File non trovato: "+fo.getMessage());
        } catch (IOException e) {
            System.out.print("Errore durante I/O: "+e.getMessage());
        } finally {
            try {
                fis.close();
                bis.close();
            } catch (IOException e) {
                System.out.print("Errore durante la chiusura degli stream: "+e.getMessage());
            }
        }

        return data;
    }

    public static byte[][] tilePdf(byte[] in) {
        byte[][] result = new byte[2][];

        PdfReader reader = null;
        try {
            reader = new PdfReader(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Rectangle pagesize = reader.getPageSizeWithRotation(1);

        for (int i = 0; i < 2; i++) {
            Rectangle newPageSize = new Rectangle(pagesize.getWidth() / 2, pagesize.getHeight());
            Document document = new Document(newPageSize);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = null;
            try {
                writer = PdfWriter.getInstance(document, baos);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            document.open();

            PdfContentByte content = writer.getDirectContent();
            PdfImportedPage page = writer.getImportedPage(reader, 1);

            float x = -pagesize.getWidth() * (i % 2) / 2;
            float y = 0;
            content.addTemplate(page, 1, 0, 0, 1, x, y);
            document.newPage();
            document.close();
            result[i] = baos.toByteArray();
        }

        return result;
    }

}
