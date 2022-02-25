package com.mimesi.ftpdrive.boot;

import com.jcraft.jsch.*;
import org.junit.jupiter.api.Test;

public class SFTPConnection {

    @Test
    void testConnectionSFTP() throws JSchException{
        System.out.println("********* START CONNECTION SFTP *********");
        JSch jsch = new JSch();
        jsch.setKnownHosts("C:\\.ssh\\known_hosts");
        Session jschSession = jsch.getSession("Fonti","dbidrive.dbinformation.it");
        jschSession.setPassword("3Z7984CD");
        jschSession.setServerAliveInterval(92000);
        jschSession.connect();
        System.out.println("Client Version: " + jschSession.getClientVersion());
        System.out.println("Host: " + jschSession.getHostKey().getHost());
        System.out.println("Port: " + jschSession.getPort());

        // ChannelSftp channelSftp= (ChannelSftp) jschSession.openChannel("sftp");
        Channel sftp = jschSession.openChannel("sftp");
        sftp.connect(5000);
        String remoteFile = "/ClassMedia/ITALIAOGGI/20220223/*.pdf";
        String localDir = "\\\\192.168.0.172\\pdftemp\\calderonepdf\\ClassEditore\\ITALIAOGGI\\0126_binpage1.pdf";

        try {
            ChannelSftp channelSftp = (ChannelSftp) sftp;
            channelSftp.get(remoteFile,localDir);
            channelSftp.exit();
        } catch (SftpException sftpEx) {
            sftpEx.printStackTrace();
        }

    }
}
