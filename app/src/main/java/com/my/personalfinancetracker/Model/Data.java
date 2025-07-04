package com.my.personalfinancetracker.Model;

public class Data {
    private int amount;
    private String type;
    private String note;
    private String id;
    private String date;

    public Data(String date, String id, String note, String type, int amount) {
        this.date = date;
        this.id = id;
        this.note = note;
        this.type = type;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Data(){

    }
}
