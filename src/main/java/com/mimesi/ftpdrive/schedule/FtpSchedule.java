package com.mimesi.ftpdrive.schedule;

import com.mimesi.ftpdrive.dto.FTPDriveDto;
import com.mimesi.ftpdrive.service.FTPDriveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;

@Configuration
@EnableScheduling
public class FtpSchedule {

    private static final Logger logger = LogManager.getLogger(FtpSchedule.class);

    private static final String PATH = "src/main/resources/ftpdrive.properties";

    @Autowired
    private FTPDriveService ftpService;

    @Scheduled(cron = "0 30 04 * * SAT")
    public void getMilanoFinanza() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 70;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());

    }

    @Scheduled(cron = "0 00 01 * * TUE-FRI")
    public void getMF595() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 595;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());

    }

    @Scheduled(cron = "0 45 00 * * MON")
    public void getItaliaOggiSette() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 217;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());

    }

    @Scheduled(cron = "0 45 00 * * TUE-SAT")
    public void getItaliaOggi() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 216;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());

    }

}
