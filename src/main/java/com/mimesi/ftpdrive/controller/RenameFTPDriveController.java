package com.mimesi.ftpdrive.controller;

import com.mimesi.ftpdrive.dto.RenameFTPDriveDto;
import com.mimesi.ftpdrive.service.RenameFTPDriveService;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class RenameFTPDriveController {

    private static final Logger logger = LogManager.getLogger(RenameFTPDriveController.class);

    @Autowired
    private RenameFTPDriveService renameFTPDriveService;

    @GetMapping("/pdf/rename")
    @ApiOperation(value = "Rename Fonte", notes = "Rinominare una fonte partendo da un id e path specifico")
    public RenameFTPDriveDto renameFonteFromID(@RequestParam("PATH") final  String path,
                                               @RequestParam("ID-Fonte") final Integer idFonte) {
        logger.info("**** CALL FROM API REST RENAME FONTE ****");
        final RenameFTPDriveDto renameFTPDriveDto = renameFTPDriveService.getEditionSourcesPDF(path,idFonte);
        return renameFTPDriveDto;
    }
}
