package com.mimesi.ftpdrive.schedule;

import com.mimesi.ftpdrive.dto.FTPDriveDto;
import com.mimesi.ftpdrive.dto.RenameFTPDriveDto;
import com.mimesi.ftpdrive.service.FTPDriveService;
import com.mimesi.ftpdrive.service.RenameFTPDriveService;
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

    @Autowired
    private FTPDriveService ftpService;

    @Autowired
    private RenameFTPDriveService renameFTPDriveService;

    @Scheduled(cron = "0 00 04 * * SAT")
    public void getMilanoFinanza() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 70;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());

    }

    @Scheduled(cron = "0 50 00 * * TUE-FRI")
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

    @Scheduled(cron = "0 30 00 * * *")
    public void getTuttoSport() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 872;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());
    }

    @Scheduled(cron = "0 00 01 * * *")
    public void getCorriereSport() {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 872;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());
    }

    @Scheduled(cron = "0 10 04 * * *")
    public void getCorriereUmbria () {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 632;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());
    }

    @Scheduled(cron = "0 20 04 * * *")
    public void getCorriereArezzo () {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 15262;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());
    }

    @Scheduled(cron = "0 30 04 * * *")
    public void getCorriereSiena () {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 1499;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());
    }

    @Scheduled(cron = "0 40 04 * * *")
    public void getCorriereViterbo () {
        logger.info("Current time is :: " + Calendar.getInstance().getTime());
        final Integer idFonte = 15263;

        logger.info("Fonte: "+idFonte);

        final FTPDriveDto ftpResponse = ftpService.getPDFromFonte(idFonte);

        logger.info("Risultato Dowload Fonte "+idFonte+": "+ftpResponse.isResultDownload());
    }
}
