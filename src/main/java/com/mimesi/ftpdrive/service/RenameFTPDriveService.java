package com.mimesi.ftpdrive.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.mimesi.ftpdrive.constant.FTPConst;
import com.mimesi.ftpdrive.dto.RenameFTPDriveDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@PropertySource("classpath:application.properties")
public class RenameFTPDriveService {

    private static final Logger logger = LogManager.getLogger(FTPDriveService.class);
    private static final byte[] buffer = new byte[100000];

    @Autowired
    private Environment env;

    @Autowired
    private FTPDriveService ftpDriveService;

    /**
     * Metodo per acquisire le edizioni partendo dal path dei file
     */
    public RenameFTPDriveDto renameSourcesPDF(final String fromPathFiles,
                                                  final Integer idFonte) {
        final RenameFTPDriveDto responseDto = new RenameFTPDriveDto();
        logger.info("******** START TO GET ALL FILE FROM "+fromPathFiles);

        // Creiamo la lista generica che verrà popolata con
        // la lista dei file, suddivisi per le varie edizioni
        final String pdfPathRename = pdfToPathRename(idFonte);
        final List<List<File>> listFontePDF = setEditionSourcesPDF(fromPathFiles,idFonte);
        for (List<File> edition : listFontePDF) {
            for(File singleFile : edition){
                if (idFonte.equals(872) || idFonte.equals(873)) {
                    if(singleFile.getName().substring(21,22).equals("S") ||
                            singleFile.getName().substring(21,22).equals("T") ||
                            singleFile.getName().substring(21,22).equals("Z") ){
                        logger.info("File: "+singleFile.getName());
                        logger.info("PAGINA DA CONSIDERARE DOPPIA, DEVE ESSERE RITAGLIATA");
                        final byte[] pdfBytes = readPDFFile(singleFile.getAbsolutePath());
                        byte[][] tilePdf = tilePdf(pdfBytes);
                        int numPage = Integer.parseInt(singleFile.getName().substring(16,18));
                        for (byte[] currPag: tilePdf){
                            final StringBuilder fileFinalName = defineFileName(singleFile.getName(), String.valueOf(numPage), idFonte);
                            String fileNameToBeWrite = pdfPathRename + File.separatorChar + fileFinalName;
                            try {
                                write(currPag,fileNameToBeWrite);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            numPage ++;
                        }
                    } else {
                        renameSinglePage(singleFile,idFonte,pdfPathRename);
                    }
                } else {
                    renameSinglePage(singleFile,idFonte,pdfPathRename);
                }
            }
        }
        logger.info("*** END RENAME FONTE "+idFonte+" ****");
        return responseDto;
    }

    /**
     * Method to rename pdf file from single Page
     * @param singleFile
     * @param idFonte
     * @param pdfPathRename
     */
    private void renameSinglePage(final File singleFile,
                                  final Integer idFonte,
                                  final String pdfPathRename) {
        try {
            logger.info("PAGINA SINGOLA, STO PER RINOMINARE E COPIARE");
            logger.info("File che sto RINOMINANDO: "+singleFile.getName());
            final Path fromPath = Paths.get(singleFile.getAbsolutePath());
            String numPageFile = "";

            if(idFonte.equals(7896)) {
                numPageFile = singleFile.getName().substring(21, 23);
            } else if (idFonte.equals(7899)) {
                numPageFile = singleFile.getName().substring(20, 22);
            } else if (idFonte.equals(872) || idFonte.equals(873)) {
                numPageFile = singleFile.getName().substring(16, 18);
            }

            final StringBuilder fileFinalName = defineFileName(singleFile.getName(), numPageFile, idFonte);
            logger.info("FILE TO BE WRITE: "+fileFinalName+ " INTO "+pdfPathRename);
            final Path finalBatchFolder = Paths.get(pdfPathRename+File.separatorChar+fileFinalName);
            Files.copy(fromPath,finalBatchFolder);
        } catch (IOException ex) {
            logger.error("Errore durante la copia del file rinominato");
            logger.error(ex.getMessage());
        }
    }

    /**
     * Method for inserting specific editions of sources into a Source List
     * @param fromPathFiles
     * @param idFonte
     * @return
     */
    private List<List<File>> setEditionSourcesPDF(final String fromPathFiles,
                                                  final Integer idFonte) {
        final List<List<File>> listFileSource = new ArrayList<>();
        final List<File> listFileNazionale = new ArrayList<>();
        final List<File> listFileSardegna = new ArrayList<>();

        final File filesPAth = new File(fromPathFiles);
        final String[] filenameList = filesPAth.list();

        if(idFonte.equals(872)) {
            logger.info("***** IT'S READING TUTTOSPORT SOURCE *****");

            final List<File> listFilePiemonte = new ArrayList<>();
            final List<File> listFileSicilia = new ArrayList<>();

            for(int i=0;i<filenameList.length;i++) {
                if(filenameList[i].matches(FTPConst.REGEX_TUTTOSPORT_NAZIONALE)) {
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione NAZIONALE");
                    listFileNazionale.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                } else if (filenameList[i].matches(FTPConst.REGEX_TUTTOSPORT_PIEMONTE)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione PIEMONTE");
                    listFilePiemonte.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                } else if (filenameList[i].matches(FTPConst.REGEX_TUTTOSPORT_SICILIA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione SICILIA");
                    listFileSicilia.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_TUTTOSPORT_SARDEGNA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione SARDEGNA");
                    listFileSardegna.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }
            }

            logger.info("ADDING LIST OF EDITION IN FATHER LIST FOR TUTTO SPORT");
            listFileSource.add(listFileNazionale);
            listFileSource.add(listFilePiemonte);
            listFileSource.add(listFileSicilia);
            listFileSource.add(listFileSardegna);

        }else if (idFonte.equals(873)) {
            logger.info("***** IT'S READING CORRIERE dello SPORT SOURCE *****");

            List<File> listFileCampania = new ArrayList<>();
            final List<File> listFilePuglia = new ArrayList<>();
            final List<File> listFileRoma = new ArrayList<>();
            final List<File> listFileVeneto = new ArrayList<>();
            final List<File> listFileStadio = new ArrayList<>();
            final List<File> listFileStadioBologna = new ArrayList<>();
            final List<File> listFileSupplemento = new ArrayList<>();

            for(int i = 0; i< Objects.requireNonNull(filenameList).length; i++) {
                if(filenameList[i].matches(FTPConst.REGEX_CORRSPORT_NAZIONALE)) {
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione NAZIONALE");
                    listFileNazionale.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                } else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_CAMPANIA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione CAMPANIA");
                    listFileCampania.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                } else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_PUGLIA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione PUGLIA");
                    listFilePuglia.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_ROMA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione ROMA");
                    listFileRoma.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_SARDEGNA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione SARDEGNA");
                    listFileSardegna.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_VENETO)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione VENETO");
                    listFileVeneto.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_STADIO)){
                    logger.info("Il file "+filenameList[i]+ " fa parte del CORR.STADIO");
                    listFileStadio.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_STADIO_BOLOGNA)){
                    logger.info("Il file "+filenameList[i]+ " fa parte deL CORR.STADIO edizione BOLOGNA");
                    listFileStadioBologna.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }  else if (filenameList[i].matches(FTPConst.REGEX_CORRSPORT_SUPPLEMENTO)){
                    logger.info("Il file "+filenameList[i]+ " fa parte dell'edizione SUPPLEMENTO");
                    listFileSupplemento.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                }
            }

            logger.info("ADDING LIST OF EDITION IN FATHER LIST FOR CORR.SPORT");
            listFileSource.add(listFileNazionale);
            listFileSource.add(listFileCampania);
            listFileSource.add(listFilePuglia);
            listFileSource.add(listFileRoma);
            listFileSource.add(listFileSardegna);
            listFileSource.add(listFileVeneto);
            listFileSource.add(listFileStadio);
            listFileSource.add(listFileStadioBologna);
            listFileSource.add(listFileSupplemento);

        } else if (idFonte.equals(7896)){
            logger.info("***** IT'S READING IL RISVEGLIO SOURCE *****");
            final List<File> listRisveglio = new ArrayList<>();

            for(int i = 0; i< Objects.requireNonNull(filenameList).length; i++) {
                if(filenameList[i].matches(FTPConst.REGEX_RISVEGLIO)) {
                    listRisveglio.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                } else {
                    logger.error("IL FILE "+filenameList[i]+" NON RISPETTA LA REGEX");
                }
            }

            logger.info("ADDING LIST OF EDITION IN FATHER LIST FOR IL RISVEGLIO");
            listFileSource.add(listRisveglio);

        } else if (idFonte.equals(7899)){
            logger.info("***** IT'S READING LA VAL SUSA SOURCE *****");
            final List<File> listValSusa = new ArrayList<>();

            for(int i = 0; i< Objects.requireNonNull(filenameList).length; i++) {
                if(filenameList[i].matches(FTPConst.REGEX_VALSUSA)) {
                    listValSusa.add(new File(fromPathFiles+File.separatorChar+filenameList[i]));
                } else {
                    logger.error("IL FILE "+filenameList[i]+" NON RISPETTA LA REGEX");
                }
            }

            logger.info("ADDING LIST OF EDITION IN FATHER LIST FOR IL RISVEGLIO");
            listFileSource.add(listValSusa);

        }

        return listFileSource;
    }

    /**
     * Method for get path of source in wich we have a renamed file
     * @param idFonte
     * @return
     */
    private String pdfToPathRename (final Integer idFonte) {
        final String yearMonth = FTPDriveService.datePatternFolder(FTPConst.YEAR_MONTH);
        final String day = FTPDriveService.datePatternFolder(FTPConst.DAY);
        final String pdfPathRename = env.getProperty("parent.folder.pdf") + File.separatorChar
                + yearMonth + File.separatorChar + day;
        return pdfPathRename;
    }

    /**
     * Method for define final filename from regex source
     * @param originalFilename
     * @param numPageFile
     * @param idFonte
     * @return
     */
    public StringBuilder defineFileName (final String originalFilename,
                                         final String numPageFile,
                                         final Integer idFonte) {
        StringBuilder filename = new StringBuilder();
        if (idFonte.equals(7896) || idFonte.equals(7899)) {
            filename.append(idFonte+"_binpage");
        } else {
            filename.append("0" +idFonte+ "_binpage");
        }

        if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_NAZIONALE)) {
            filename.append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_CAMPANIA)){
            filename.append(FTPConst.EDIZIONE_CAMPANIA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_PUGLIA)){
            filename.append(FTPConst.EDIZIONE_PUGLIA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_ROMA)){
            filename.append(FTPConst.EDIZIONE_ROMA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_SARDEGNA)){
            filename.append(FTPConst.EDIZIONE_SARDEGNA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_VENETO)){
            filename.append(FTPConst.EDIZIONE_VENETO).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_STADIO)){
            filename.append(FTPConst.EDIZIONE_STADIO).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_STADIO_BOLOGNA)){
            filename.append(FTPConst.EDIZIONE_STADIO_BOLOGNA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_CORRSPORT_SUPPLEMENTO)){
            filename.append(FTPConst.EDIZIONE_SUPPLEMENTO).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_TUTTOSPORT_NAZIONALE)) {
            filename.append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_TUTTOSPORT_PIEMONTE)){
            filename.append(FTPConst.EDIZIONE_PIEMONTE).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_TUTTOSPORT_SICILIA)){
            filename.append(FTPConst.EDIZIONE_SICILIA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_TUTTOSPORT_SARDEGNA)) {
            filename.append(FTPConst.EDIZIONE_SARDEGNA).append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_RISVEGLIO)){
            filename.append(numPageFile).append(".pdf");
        } else if (originalFilename.matches(FTPConst.REGEX_VALSUSA)){
            filename.append(numPageFile).append(".pdf");
        } else {
            logger.error("Il file "+originalFilename+ " non matcha con nessun edizione della fonte "+idFonte);
        }

        return filename;
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
            logger.error("Errore nel metodo readPDFFile");
            logger.error("File non trovato: "+fo.getMessage());
        } catch (IOException e) {
            logger.error("Errore nel metodo readPDFFile");
            logger.error("Errore durante I/O: "+e.getMessage());
        } finally {
            try {
                fis.close();
                bis.close();
            } catch (IOException e) {
                logger.error("Errore nel metodo readPDFFile");
                logger.error("Errore durante la chiusura degli stream: "+e.getMessage());
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
            logger.error("Errore nel metodo tilePdf");
            logger.error("Errore durante I/O: "+e.getMessage());
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
                logger.error("Errore nel metodo tilePdf");
                logger.error("Errore DocumentException: "+e.getMessage());
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
