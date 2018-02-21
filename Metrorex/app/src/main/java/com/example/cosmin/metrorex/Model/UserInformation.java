package com.example.cosmin.metrorex.Model;

import java.text.NumberFormat;

/**
 * Created by septy on 16.05.2017.
 */

public class UserInformation {
    private String name;
    private String number;
    private int tipAbonament;
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

    public int getTipAbonament() {
        return tipAbonament;
    }

    public void setTipAbonament(int tipAbonament) {
        this.tipAbonament = tipAbonament;
    }

    public int getNumarCalatorii() {
        return numarCalatorii;
    }

    public void setNumarCalatorii(int numarCalatorii) {
        this.numarCalatorii = numarCalatorii;
    }



    public UserInformation(){
        name="null";
        number="null";
        credit=0;
        tipAbonament=0;
        numarCalatorii=0;

    }

    public UserInformation(UserInformation userInformation) {
        this.name = userInformation.getName();
        this.number = userInformation.getNumber();
        this.tipAbonament = userInformation.getTipAbonament();
        this.numarCalatorii = userInformation.getNumarCalatorii();
        this.credit = userInformation.getCredit();

    }

    public UserInformation(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
