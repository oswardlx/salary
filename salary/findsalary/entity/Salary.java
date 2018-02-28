package com.dsideal.space.findsalary.entity;

public class Salary {
    private String worder_num;
    private String name;
    private String salary_details;
    private String org_id;
    private String org_type;
    private String year_month_id;

    public Salary(String worder_num, String name, String salary_details, String org_id, String org_type, String year_month_id) {
        this.worder_num = worder_num;
        this.name = name;
        this.salary_details = salary_details;
        this.org_id = org_id;
        this.org_type = org_type;
        this.year_month_id = year_month_id;
    }

    @Override
    public String toString() {
        return "Salary{" +
                "worder_num='" + worder_num + '\'' +
                ", name='" + name + '\'' +
                ", salary_details='" + salary_details + '\'' +
                ", org_id='" + org_id + '\'' +
                ", org_type='" + org_type + '\'' +
                ", year_month_id='" + year_month_id + '\'' +
                '}';
    }

    public String getWorder_num() {
        return worder_num;
    }

    public void setWorder_num(String worder_num) {
        this.worder_num = worder_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary_details() {
        return salary_details;
    }

    public void setSalary_details(String salary_details) {
        this.salary_details = salary_details;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getOrg_type() {
        return org_type;
    }

    public void setOrg_type(String org_type) {
        this.org_type = org_type;
    }

    public String getYear_month_id() {
        return year_month_id;
    }

    public void setYear_month_id(String year_month_id) {
        this.year_month_id = year_month_id;
    }
}
