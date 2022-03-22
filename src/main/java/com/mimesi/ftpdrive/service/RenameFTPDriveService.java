package com.mimesi.ftpdrive.service;

import com.mimesi.ftpdrive.constant.FTPConst;
import com.mimesi.ftpdrive.dto.RenameFTPDriveDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@PropertySource("classpath:application.properties")
public class RenameFTPDriveService {

    private static final Logger logger = LogManager.getLogger(FTPDriveService.class);

    @Autowired
    private Environment env;

    /**
     * Metodo per acquisire le edizioni partendo dal path dei file
     */
    public RenameFTPDriveDto getEditionSourcesPDF(final String fromPathFiles,
                                                  final Integer idFonte) {
        final RenameFTPDriveDto responseDto = new RenameFTPDriveDto();
        logger.info("******** START TO GET ALL FILE FROM "+fromPathFiles);

        // Creiamo la lista generica che verrà popolata con
        // la lista dei file, suddivisi per le varie edizioni
        final List<List<File>> listFontePDF = setEditionSourcesPDF(fromPathFiles,idFonte);

        for (List<File> edition : listFontePDF) {
            for(File singleFile : edition){

            }
        }


        return responseDto;
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

            logger.info("ADDING LIST OF EDITION IN FATHER LIST FOR CORR.SPORT");
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

        }

        return listFileSource;
    }
}
