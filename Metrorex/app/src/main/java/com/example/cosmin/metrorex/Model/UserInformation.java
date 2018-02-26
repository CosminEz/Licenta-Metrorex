package com.example.cosmin.metrorex.Model;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Filote Cosmin on 16.05.2017.
 */

public class UserInformation {
    private String name;
    private String number;
    private String tipAbonament;
    private Date expirareAbonament;
    private int numarCalatorii;
    private int credit;

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

    public UserInformation(){
        name="null";
        number="null";
        credit=0;
        tipAbonament="Expirat";

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        expirareAbonament=date;
        numarCalatorii=0;

    }

    public UserInformation(UserInformation userInformation) {
        this.name = userInformation.getName();
        this.number = userInformation.getNumber();
        this.tipAbonament = userInformation.getTipAbonament();
        this.numarCalatorii = userInformation.getNumarCalatorii();
        this.credit = userInformation.getCredit();
        this.expirareAbonament=userInformation.getExpirareAbonament();

    }

    public UserInformation(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
