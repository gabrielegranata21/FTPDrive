package com.mimesi.ftpdrive.dto;

public class FTPDriveDto {

    private Integer idFonte;

    private String fromPath;

    private String toPath;

    private String error;

    private boolean resultDownload;

    public Integer getIdFonte() { return idFonte; }

    public void setIdFonte(Integer idFonte) { this.idFonte = idFonte; }

    public String getFromPath() { return fromPath; }

    public void setFromPath(String fromPath) { this.fromPath = fromPath; }

    public String getToPath() { return toPath; }

    public void setToPath(String toPath) { this.toPath = toPath; }

    public String getError() { return error; }

    public void setError(String error) { this.error = error; }

    public boolean isResultDownload() { return resultDownload; }

    public void setResultDownload(boolean resultDownload) { this.resultDownload = resultDownload; }
}
