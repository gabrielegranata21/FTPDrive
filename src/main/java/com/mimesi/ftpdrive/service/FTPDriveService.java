package com.mimesi.ftpdrive.service;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mimesi.ftpdrive.constant.FTPConst;
import com.mimesi.ftpdrive.dto.FTPDriveDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

@Service
@PropertySource("classpath:application.properties")
public class FTPDriveService {

    private static final Logger logger = LogManager.getLogger(FTPDriveService.class);

    @Autowired
    private Environment env;

    @Autowired
    private RenameFTPDriveService renameFTPDriveService;

    /**
     * Method to get PDF from FONTE
     *
     * @param fonte
     */
    public FTPDriveDto getPDFromFonte(final Integer fonte) {
        FTPDriveDto ftpDto = new FTPDriveDto();

        final String fromPath = pdfFromPath(fonte);
        logger.info("Get File from: "+fromPath);
        final String toPath = pdfToPath(fonte);
        logger.info("File to be write on: "+toPath);

        try {
            Channel connectionChannel = enstablishSFTPConnection();
            connectionChannel.connect(5000);
            ChannelSftp channelSftp = (ChannelSftp) connectionChannel;
            logger.info("*** START TO DOWNLOAD FILE FROM "+fromPath);

            ftpDto = downloadFromFolder(channelSftp,fromPath,toPath,fonte);

        } catch (JSchException jSchException) {
            logger.error("Errore durante la connessione al server SFTP: "+jSchException.getMessage());
            logger.error("Connessione Interrotta");
            ftpDto.setResultDownload(false);
            ftpDto.setError(jSchException.getMessage());
        }

        if (fonte.equals(872) ||
                fonte.equals(873) ||
                fonte.equals(7896) ||
                fonte.equals(7899)) {
            renameFTPDriveService.renameSourcesPDF(toPath,fonte);
        } else {
            moveFileForCompressPDF(toPath);
        }

        return ftpDto;

    }

    /**
     * Method to Enstablish SFTP connection with FTPDrive
     * @return
     * @throws JSchException
     */
    private Channel enstablishSFTPConnection() throws JSchException {
        final String sftpUrl = env.getProperty("sftp.url.dbdrive").toString();
        final String sftpUsr = env.getProperty("sftp.usr.dbdrive").toString();
        final String sftpPwd = env.getProperty("sftp.pwd.dbdrive").toString();

        logger.info("********* START CONNECTION SFTP *********");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch jsch = new JSch();
        // jsch.setKnownHosts(env.getProperty("sftp.kownhosts.file"));
        Session jschSession = jsch.getSession(sftpUsr,sftpUrl);
        jschSession.setPassword(sftpPwd);
        jschSession.setConfig(config);
        jschSession.connect();
        logger.info("Client Version: " + jschSession.getClientVersion());
        logger.info("Host: " + jschSession.getHostKey().getHost());
        logger.info("Port: " + jschSession.getPort());

        return jschSession.openChannel("sftp");
    }

    /**
     * Method to Download list file from specific remote FTP Folder
     * @param channelSftp
     * @param folder
     * @param toPath
     */
    private FTPDriveDto downloadFromFolder(final ChannelSftp channelSftp,
                                           final String folder,
                                           final String toPath,
                                           final Integer idFonte) {
        final FTPDriveDto ftpDto = new FTPDriveDto();
        ftpDto.setIdFonte(idFonte);
        ftpDto.setFromPath(folder);
        ftpDto.setToPath(toPath);
        try {
            Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(folder);
            logger.info("Entries: "+entries);

            //download all from folder
            for (ChannelSftp.LsEntry en : entries) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                logger.info("File will be download: "+en.getFilename());
                logger.info("[ File For Download: "+ folder + en.getFilename() +"  ] Write to: "+ toPath);

                channelSftp.get(folder + en.getFilename(), toPath);
                logger.info("Download Ended ----> Successfully Write in "+toPath);
            }
            ftpDto.setResultDownload(true);
        } catch (SftpException sftpException) {
            logger.error("Errore durante il download: "+sftpException.getMessage());
            if (sftpException.getMessage().contains("No such file")) {
                logger.error("Nessun file presente nella data odierna");
                ftpDto.setError(sftpException.getMessage() + "Nessun file presente nella data odierna");
                ftpDto.setResultDownload(false);
            }
        } finally {
            channelSftp.exit();
            channelSftp.disconnect();
            return ftpDto;
        }
    }


    /**
     * Method to generate paths from specific @Fonte, from FTPDrive
     * @param fonte
     * @return
     */
    private String pdfFromPath(final Integer fonte) {
        String fromPath = "";
        String datePatternFolder = "";

        switch (fonte) {
            case 70:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_70
                        + datePatternFolder + "/";
                break;
            case 216:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_216
                        + datePatternFolder + "/";
                break;
            case 217:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_217
                        + datePatternFolder + "/";
                break;
            case 595:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_595
                        + datePatternFolder + "/";
                break;
            case 632:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_632
                        + datePatternFolder + "/";
                break;
            case 872:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_872
                        + datePatternFolder + "/";
                break;
            case 873:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_873
                        + datePatternFolder + "/";
                break;
            case 1499:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_1499
                        + datePatternFolder + "/";
                break;
            case 15262:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_15262
                        + datePatternFolder + "/";
                break;
            case 15263:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_15263
                        + datePatternFolder + "/";
                break;
            case 7896:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_7896
                        + datePatternFolder + "/";
                break;
            case 7897:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_7897
                        + datePatternFolder + "/";
                break;
            case 7899:
                datePatternFolder = datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                fromPath = FTPConst.BASE_PATH_7899
                        + datePatternFolder + "/";
                break;
        }
        return fromPath;

    }

    /**
     * Method to generate paths from specific @Fonte
     * @param fonte
     * @return
     */
    private String pdfToPath(final Integer fonte) {
        String toPath = "";

        switch (fonte) {
            case 70:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.milanofinanza.70");
                break;
            case 216:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.italiaoggi.216");
                break;
            case 217:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.italiaoggisette.217");
                break;
            case 595:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.mifi.595");
                break;
            case 632:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.corrumbria.632");
                break;
            case 872:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.tuttosport.872")
                        + File.separatorChar + datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                createFolder(new File(toPath));
                break;
            case 873:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.corrieresport.873")
                        + File.separatorChar + datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                createFolder(new File(toPath));
                break;
            case 1499:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.corrsiena.1499");
                break;
            case 15262:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.corrarezzo.15262");
                break;
            case 15263:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.corrviterbo.15263");
                break;
            case 7896:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.risveglio.7896")
                        + File.separatorChar + datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                createFolder(new File(toPath));
                break;
            case 7897:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.risvegliopopolare.7897");
                break;
            case 7899:
                toPath = env.getProperty("parent.folder") + File.separatorChar
                        + env.getProperty("pattern.valsusa.7899")
                        + File.separatorChar + datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
                createFolder(new File(toPath));
                break;
        }

        return toPath;
    }

    /**
     * Method for generate currentDate from specific pattern string
     * @param pattern
     * @return
     */
    public static String datePatternFolder (final String pattern) {
        SimpleDateFormat formFile = new SimpleDateFormat(pattern);
        Date date = new Date();
        String datePatternFolder = formFile.format(date);

        return datePatternFolder;
    }

    /**
     * Method for Move file in current date folder
     * @param toPath
     */
    private void moveFileForCompressPDF (final String toPath) {
        logger.info("***** MOVE FILE FROM "+toPath+" *****");
        /**
         *

        final String batchFolderPath = FTPConst.PARENT_FOLDER + File.separatorChar
                + FTPConst.BATCH_PATH_FOLDER + File.separatorChar + datePatternFolder(FTPConst.DATE_PATTERN_FIRST);
         */
        final String batchFolderFinal = env.getProperty("parent.folder") + File.separatorChar
                + env.getProperty("batch.path.folder") + File.separatorChar + datePatternFolder(FTPConst.YEAR_MONTH)
                + File.separatorChar + datePatternFolder(FTPConst.DAY) + File.separatorChar;

        final Path fromPath = Paths.get(toPath);
        final File finalFileBatch = new File(batchFolderFinal);

        if(!finalFileBatch.exists()) {
            finalFileBatch.mkdirs();
        }

        try{
            final Path finalBatchFolder = Paths.get(batchFolderFinal+fromPath.getFileName());
            Files.copy(fromPath,finalBatchFolder);
        } catch (IOException ioException) {
            logger.error("Errore Durante la Copia del File verso "+batchFolderFinal);
            logger.error("Reason: "+ ioException.getMessage());
        }

        logger.info("***** END MOVE FILE INTO "+batchFolderFinal+" *****");
    }

    private void createFolder(final File file) {
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
