package com.dsideal.space.findsalary.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class SalaryDao {


    public boolean saveSalaryList(ArrayList<String> salarylist, String year_month_id, String org_id, String org_type,ArrayList<String> personIdList) {
        String sql = "";
        String sql1 = "";
        String sqlselect2 = "";
        String sqlselect = "";
        int affsql;
        String sql2 = "DELETE FROM t_social_salary where YEAR_MONTH_ID = "+year_month_id+" and ORG_ID = "+org_id+" and ORG_TYPE = "+org_type;
        Db.update(sql2);
        sql1 = "Delete From t_social_salary_titles where YEAR_MONTH_ID = "+year_month_id+" AND ORG_ID = "+org_id+" and ORG_TYPE = "+org_type;
        Db.update(sql1);

        for (int i = 0; i < salarylist.size(); i++) {
            if (i == 0) {
//                sqlselect = "SELECT * FROM t_social_salary_titles where YEAR_MONTH_ID = "+year_month_id+" and ORG_ID = "+org_id+" and ORG_TYPE= "+org_type+";";
//                System.out.println("********");
//                List<Record> records = Db.query(sqlselect);
//                System.out.println("daodaodaodao");
//                System.out.println(records.size());
//                System.out.println("daodaodaodao");
//                switch(records.size()) {
//                    case 1:
//                        sql = "UPDATE t_social_salary_titles SET TITLES = '" + String.valueOf(salarylist.get(0)) + "' where YEAR_MONTH_ID=" + year_month_id + " and ORG_ID=" + org_id + " and ORG_TYPE=" + org_type;
//                        break;
//                    case 0:
                sql = "INSERT into t_social_salary_titles (TITLES,YEAR_MONTH_ID,ORG_id,ORG_type) values('" + salarylist.get(0) + "'," + year_month_id + "," + org_id + "," + org_type + ")";
//                        break;
//                    default:
//                        sql = "Delete From t_social_salary_titles where YEAR_MONTH_ID = "+year_month_id+" AND ORG_ID = "+org_id+" and ORG_TYPE = "+org_type+";"+"INSERT into t_social_salary_titles (TITLES,YEAR_MONTH_ID,ORG_id,ORG_type) values('" + String.valueOf(salarylist.get(0)) + "'," + year_month_id + "," + org_id + "," + org_type + ");";
//                        break;
//                }

                System.out.println(sql);
                try {
                    affsql = Db.update(sql);
                    System.out.println(affsql);
                    if (affsql != 1) {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jo = (JSONObject) JSON.parse(salarylist.get(i));
//                System.out.println(jo);
//                sqlselect2 = "SELECT * FROM t_social_salary where  ORG_ID = "+org_id+" and ORG_TYPE = "+org_type+" and YEAR_MONTH_ID = "+year_month_id;
//                System.out.println(sqlselect2);
//                List<Record> records = Db.query(sqlselect2);
//                System.out.println("daodaodao2");
//                System.out.println(records.size());
//                System.out.println("daodaodao2");
//                switch(records.size()) {
//                    case 1:
//                        sql1 = "UPDATE t_social_salary SET PERSON_ID = "+personIdList.get(i)+",WORKER_NUM= "+jo.getString("0")+",NAME='"+jo.getString("1")+"', SALARY_DETAILS = '" + String.valueOf(salarylist.get(i)) + "' where YEAR_MONTH_ID=" + year_month_id + " and ORG_ID=" + org_id + " and ORG_TYPE=" + org_type;
//                        break;
//                    case 0:
                sql1 = "INSERT INTO t_social_salary (PERSON_ID,WORKER_NUM,NAME,SALARY_DETAILS,ORG_ID,ORG_TYPE,YEAR_MONTH_ID) values("+personIdList.get(i)+",'" + jo.getString("0") + "','" + jo.getString("1") + "','" + String.valueOf(salarylist.get(i)) + "'," + org_id + "," + org_type + "," + year_month_id + ")";

//                        break;
//                    default:
//                        sql1 = "Delete From t_social_salary where YEAR_MONTH_ID = "+year_month_id+" AND ORG_ID = "+org_id+" and ORG_TYPE = "+org_type+";"+"INSERT INTO t_social_salary (PERSON_ID,WORKER_NUM,NAME,SALARY_DETAILS,ORG_ID,ORG_TYPE,YEAR_MONTH_ID) values("+personIdList.get(i)+"," + jo.getString("0") + ",'" + jo.getString("1") + "','" + String.valueOf(salarylist.get(i)) + "'," + org_id + "," + org_type + "," + year_month_id + ")";
//                        break;
//                }

//                sql1 = "Delete From t_social_salary where WORKER_NUM = "+jo.getString("2")+" and ORG_ID = "+org_id+" and ORG_TYPE = "+org_type+" and YEAR_MONTH_ID = "+year_month_id;
//                sql = "INSERT INTO t_social_salary (WORKER_NUM,NAME,SALARY_DETAILS,ORG_ID,ORG_TYPE,YEAR_MONTH_ID) values(" + jo.getString("2") + ",'" + jo.getString("3") + "','" + salarylist.get(i) + "'," + org_id + "," + org_type + "," + year_month_id + ")";
                System.out.println(sql1);
                try {
                int j = Db.update(sql1);
//                    if(j>0){
//                        throw new Exception(jo.getString("2"));
//                    }
//                    affsql = Db.update(sql);
//                    System.out.println(affsql);
//                    if (affsql != 1) {
//                        return false;
//                    }
                } catch (Exception e) {
                    throw new RuntimeException("数据插入失败");
//                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
