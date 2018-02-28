package com.dsideal.space.findsalary.entity;

public class Title {
    private String text;
    private String year_month_id;

    @Override
    public String toString() {
        return "Title{" +
                "text='" + text + '\'' +
                ", year_month_id='" + year_month_id + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getYear_month_id() {
        return year_month_id;
    }

    public void setYear_month_id(String year_month_id) {
        this.year_month_id = year_month_id;
    }

    public Title(String text, String year_month_id) {
        this.text = text;
        this.year_month_id = year_month_id;
    }
}
