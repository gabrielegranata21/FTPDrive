package com.mimesi.ftpdrive.controller;

import com.mimesi.ftpdrive.dto.FTPDriveDto;
import com.mimesi.ftpdrive.service.FTPDriveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class FTPDriveController {

    private static final Logger logger = LogManager.getLogger(FTPDriveController.class);

    @Autowired
    private FTPDriveService ftpService;

    @GetMapping(path = "/pdf")
    public FTPDriveDto getFonteFromFTPDrive(@RequestParam("fonte") final Integer idFonte) {
        FTPDriveDto resposeFtP = new FTPDriveDto();
        logger.info("Get PDF for FONTE: "+idFonte);
        return ftpService.getPDFromFonte(idFonte);
    }
}
