package com.dsideal.space.findsalary.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsideal.space.examine.exceloutput.ExamineController;
import com.dsideal.space.findsalary.Util.ReadExcel;
import com.dsideal.space.findsalary.dao.SalaryDao;
import com.dsideal.util.HttpClientUtil;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class FindSalaryController extends Controller {
    private Logger logger = LoggerFactory.getLogger(ExamineController.class);
    private int startIndex ;
    public void index() throws  Exception{
        JSONObject retuJoin = new JSONObject();
        List<String> exceptionMessage = new ArrayList<>();
        UploadFile file = getFile("file","",10*1024*1024,"utf-8");//获取文件
        String org_id = getPara("org_id");
        String org_type = getPara("org_type");
        String year_month_id = getPara("year_month_id");
        year_month_id = year_month_id.replace("-", "");

        //验证是否为Excel格式
        try{
            checkExcelType(file);
        }catch (Throwable t){
            t.printStackTrace();
            setAttr("success",false);
            setAttr("info",t.getMessage());
            renderJson();
            return;
        }


        File filedele2 = new File(file.getSaveDirectory() + "/" + file.getFileName());
        File file1 = null;
        try {
            file1 = file.getFile();
        }catch (Exception e){
            e.printStackTrace();
            setAttr("success",false);
            setAttr("info","请确认是否传入文件。");
            renderJson();
            return;
        }
        FileInputStream fis= null;
        ArrayList<String> salaryList = null;
        ArrayList<String> personIdList = new ArrayList<String>();

        personIdList.add(0,"3333333");
        try{
            ReadExcel re = new ReadExcel();//将excel的内容转为json list

            try {
                retuJoin = re.testReadExcel(fis, file1);
                filedele2.delete();
                salaryList = (ArrayList)retuJoin.get("dataList");
//                for(String str1:salaryList)


            }catch (Throwable r){
                setAttr("success",false);
                exceptionMessage.add(r.getMessage());
                setAttr("info",exceptionMessage);
                renderJson();
                return;
            }
            if(salaryList==null||salaryList.size()==0){
                setAttr("success",false);
                setAttr("info",exceptionMessage);
                renderJson();
                return;
            }
            String finance_no ="";
            String finance_no2="";
            int x = salaryList.size();
            int j =1;
            JSONObject jo0 = (JSONObject)JSON.parse(jsonString((salaryList.get(0))));
            int lastcellnum = re.getTotalCells();
            startIndex=  checkFirstColumn(salaryList);
            if(startIndex==-1){
                setAttr("success",false);
                setAttr("info","请确认excel中第一列是否有“财务编码”。");
                renderJson();
                return;
            }
//            String worker_numname =jo0.getString("0");
            String sumName = jo0.getString(String.valueOf(lastcellnum-1));
            String person_name  = jo0.getString("1");
//            if(!worker_numname.equals("财务编码")){
//                exceptionMessage.add("请确认第2行，第1列的内容，应为\"财务编码\"");
//            }

            if(!person_name.equals("姓名")){
                exceptionMessage.add("请确认第2列的表头内容应为\"姓名\"");
            }

            if(!sumName.equals("实发合计")){
                exceptionMessage.add("请确认第"+lastcellnum+"列的表头内容应为\"实际合发\"");
            }
            for (int i = 1; i < x; i++) {
                int row = i+(int)retuJoin.get("startrow")+1;

                String exceptionInfomation="";
                JSONObject jo = (JSONObject) JSON.parse(jsonString(salaryList.get(i)));
                finance_no=jo.getString("0");
                String result="";
                try {
                    result = getPersonName(finance_no, org_id);
                }catch (Throwable re1){
                    String excepMessage = re1.getMessage();
                    exceptionInfomation = excepMessage;
                }
                for(;j<i;j++){
                    int row2 = j+1;
                    JSONObject jo2 = (JSONObject) JSON.parse(salaryList.get(j));
                    finance_no2=jo2.getString("0");
                    if(finance_no==finance_no2||finance_no.equals(finance_no2)){
                        exceptionMessage.add("第"+row+"行与第"+row2+"行：员工编号为"+finance_no2+"的工资条重复，请检查。");
                    }
                }
                JSONObject jo3 = (JSONObject) JSON.parse(result);
                System.out.println("jo3");
                System.out.println(jo3);

                System.out.println("jo3:");
                if(jo3!=null){
                    if(!jo.getString("1").equals(jo3.getString("person_name"))){
                        exceptionInfomation = exceptionInfomation+"请核对此人姓名，是否为"+jo3.getString("person_name");
                    }
                    salaryList.set(i,String.valueOf(jo));
                    personIdList.add(i, jo3.getString("person_id"));

                }else{
                    personIdList.add(i, "no-name");
                }
                if(!exceptionInfomation.equals("")){
                    exceptionMessage.add("第"+row+"行："+exceptionInfomation);
                }
            }
            if(exceptionMessage.size()>0){
                setAttr("success",false);
                setAttr("info",exceptionMessage);
                renderJson();
                return;
            }
        }catch (Throwable re){
            re.printStackTrace();
            String excepMessage = re.getMessage();
            setAttr("success",false);
            setAttr("info",excepMessage);
            renderJson();
            return;
        }
        SalaryDao sd = new SalaryDao();
        boolean success;
        System.out.println("sssssssssssssssssssssssssss");
        System.out.println(salaryList.size());
        System.out.println("ssssssssssssssssssssssssssss");
        try {
            success = sd.saveSalaryList(salaryList, year_month_id, org_id, org_type,personIdList);
        }catch (Throwable re){
            String excepMessage = re.getMessage();
            setAttr("success",false);
            setAttr("info",excepMessage);
            renderJson();
            return;
        }
        if (success==false){
            setAttr("success",false);
            setAttr("info","操作有误，或数据有误");
            renderJson();
            return;
        }
        setAttr("success",true);
        renderJson();
        return;
    }


    private String getPersonName(String financeId,String orgId)  {
        String url = HttpClientUtil.getApiPrev()+"/dsideal_yy/admin/new_base/finance/gerPersonInfoByFinanceNo?"+"finance_no="+financeId+"&bureau_id="+orgId;
        try {
            String result = (String) HttpClientUtil.httpGetReq(url,null);
            JSONObject jotemp = (JSONObject) JSON.parse(result);
            if(jotemp.getString("success")=="false"||jotemp.getString("success").equals("false")){

                throw new Exception("请核对员工编码"+financeId+"的准确性;");
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("请核对员工编码"+financeId+"的准确性");
        }

    }

    private static String jsonString(String s){
        char[] temp = s.toCharArray();
        int n = temp.length;
        for(int i =0;i<n;i++){
            if(temp[i]==':'&&temp[i+1]=='"'){
                for(int j =i+2;j<n;j++){
                    if(temp[j]=='"'){
                        if(temp[j+1]!=',' &&  temp[j+1]!='}'){
                            temp[j]='”';
                        }else if(temp[j+1]==',' ||  temp[j+1]=='}'){
                            break ;
                        }
                    }
                }
            }
        }
        return new String(temp);
    }
    //判断是否是excel格式
    private void checkExcelType(UploadFile file){
        try {
            String filename = file.getFileName();
            String filetype1 = filename.substring(filename.length() - 3, filename.length());
            String filetype2 = filename.substring(filename.length() - 4, filename.length());
            File filedele = new File(file.getSaveDirectory() + "/" + file.getFileName());
            if (!filetype1.equals("xls") && !filetype2.equals("xlsx")) {
                filedele.delete();
                throw new RuntimeException();
            }
        }catch (Throwable T){
            T.printStackTrace();
            throw new RuntimeException("请传入excel");
        }

    }

    //返回“财务编码”所在的行数
    private int checkFirstColumn(List<String> salaryListTemp){
        for(int i=0;i<salaryListTemp.size();i++){
            JSONObject jo0 = (JSONObject)JSON.parse(jsonString(salaryListTemp.get(i)));
            String worker_numname =jo0.getString("0");
            if(worker_numname.equals("财务编码")){
                return i;
            }
        }
        return -1;
    }
}
