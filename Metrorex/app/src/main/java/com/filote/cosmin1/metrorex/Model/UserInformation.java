package com.filote.cosmin1.metrorex.Model;

import java.util.Date;

/**
 * Created by Filote Cosmin on 16.05.2017.
 * This class is a POJO .
 */

public class UserInformation {
    private String name;
    private String number;
    private String tipAbonament;
    private String status;
    private Date expirareAbonament;
    private int numarCalatorii;
    private int credit;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTipAbonament() {
        return tipAbonament;
    }

    public void setTipAbonament(String tipAbonament) {
        this.tipAbonament = tipAbonament;
    }

    public int getNumarCalatorii() {
        return numarCalatorii;
    }

    public void setNumarCalatorii(int numarCalatorii) {
        this.numarCalatorii = numarCalatorii;
    }

    public Date getExpirareAbonament() {
        return expirareAbonament;
    }

    public void setExpirareAbonament(Date expirareAbonament) {
        this.expirareAbonament = expirareAbonament;
    }


    public UserInformation() {
        name = "null";
        number = "null";
        credit = 0;
        tipAbonament = "Expirat";
        status="null";
        numarCalatorii = 0;

    }

    public UserInformation(UserInformation userInformation) {
        this.name = userInformation.getName();
        this.number = userInformation.getNumber();
        this.tipAbonament = userInformation.getTipAbonament();
        this.numarCalatorii = userInformation.getNumarCalatorii();
        this.credit = userInformation.getCredit();
        this.expirareAbonament = userInformation.getExpirareAbonament();
        this.status=userInformation.getStatus();

    }

    public UserInformation(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
