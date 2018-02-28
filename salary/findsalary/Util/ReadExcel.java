package com.dsideal.space.findsalary.Util;

import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.util.Suspendable;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.binding.corba.wsdl.Array;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tuckey.web.filters.urlrewrite.Run;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadExcel {
    private int totalRows; //sheet中总行数
    private int totalCells; //每一行总单元格数

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalCells() {
        return totalCells;
    }

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    public final String POINT = ".";

    public JSONObject testReadExcel(FileInputStream fis, File file1) throws IOException {
        JSONObject retujo = new JSONObject();
        fis = new FileInputStream(file1);
        ArrayList<String> dataList = new ArrayList<>();
        //创建输入流
        //通过构造函数传参
        Workbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook(fis);
        } catch (Exception ex) {
            // 解决read error异常
            fis = new FileInputStream(file1);
            try {
                hssfWorkbook = new XSSFWorkbook(fis);
            } catch (Exception e) {
                throw new RuntimeException("请确认传入的文件是否正确。");
            }
        }
        //获取工作表
        Sheet sheet = hssfWorkbook.getSheetAt(0);
        try {
            totalRows = gainTotalRows(sheet);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("请确认是否有空行。");
        }
        System.out.println("totalRows:" + totalRows);
        boolean flag = false;
        List<String> exceptionInfomation2 = new ArrayList<>();
        int tinyindex = 0;
        String emptyRowAndColum = "";
        for (int rowNum = 0; rowNum < totalRows ; rowNum++) {
            Row hssfRow = sheet.getRow(rowNum);
            System.out.println("121212");
            System.out.println(hssfRow);

            if (hssfRow != null) {
                System.out.println("qqqqq");
                totalCells = hssfRow.getLastCellNum();
                String data = "";
                //读取列，从第一列开始,并将其转换为json
                for (short c = 0; c <= totalCells - 1; c++) {
                    Cell cell = hssfRow.getCell(c);
                    if (cell == null) {
                        data = data + "\"" + c + "\"" + ":" + "\"\",";
                        continue;
                    }
                    String hValue = "";
                    try {
                        hValue = getHValue(cell, c);
                        if (hValue.equals("财务编码") && flag == false) {
                            retujo.put("startrow", rowNum);
                            flag = true;
                            tinyindex = rowNum;
                        }
//                        if(flag==true&&tinyindex==rowNum&&hValue.equals("")){
////                            throw new RuntimeException("请保证表头连续,没有中断");
////                        }


                    } catch (RuntimeException e) {
                        int rowex = rowNum + 1;
                        int cellex = c + 1;
                        e.printStackTrace();
                        exceptionInfomation2.add("第" + rowex + "行，第" + cellex + "列：" + e.getMessage());
                    }

                    data = data + "\"" + c + "\"" + ":\"" + hValue.replace("'", "’").replace("\"", "”") + "\",";
                }
                data = data.substring(0, data.length() - 1);
                data = "{" + data + "}";
                if (flag == true) {
                    dataList.add(data);
                }
            }
            System.out.println("flag:" + flag);
            System.out.println("roNum:" + rowNum);
            System.out.println(hssfRow);
//            if(rowNum==tinyindex+1&&hssfRow.getCell(1).getStringCellValue().equals("")){
//                System.out.println("qqqqqqqqqqww");
//                System.out.println(hssfRow);
//                System.out.println(hssfRow.getCell(1).getStringCellValue());
//            }
            if ((hssfRow == null && flag) || (flag && rowNum == tinyindex + 1 && String.valueOf(getHValue(hssfRow.getCell(0), (short) 1)).equals(""))) {
                System.out.println("gagagaga");
                emptyRowAndColum = "其中存在空行，请按照规定格式上传。";
//                throw new RuntimeException();
            }

        }
        System.out.println("wwwwwwwwwwwwwwww");
        System.out.println(dataList.size());
        System.out.println("wwwwwwwwwwwwwwww");

        if (!flag) {
            throw new RuntimeException("请确认第一列存在\"财务编码\"。");
        }
        for (int checkindex = 0; checkindex < sheet.getRow(tinyindex).getLastCellNum() - 1; checkindex++) {
            Cell cell = sheet.getRow(tinyindex).getCell(checkindex);
            if (cell == null) {
                emptyRowAndColum = emptyRowAndColum + "请保证表头连续,没有中断";

            }
        }
        if (emptyRowAndColum.length() > 0) {
            throw new RuntimeException(emptyRowAndColum);
        }
//        System.out.println("82");
//        System.out.println(totalRows);
//        System.out.println(dataList.size());
//        System.out.println(dataList.toString());
//        if(totalRows!=dataList.size()){
//            exceptionInfomation2.add("其中存在空行，请按照规定格式上传。");
//        }
        if (exceptionInfomation2.size() > 0) {
            throw new RuntimeException(exceptionInfomation2.toString());
        }
        fis.close();
        retujo.put("dataList", dataList);

        return retujo;
    }

    //获得行数
    private int gainTotalRows(Sheet sheet) {
        int rowindex = sheet.getLastRowNum();
        System.out.println("rowindex:" + rowindex);
        //读取Row,从第一行开始
        for (int rowNum = 0; rowNum <= rowindex; rowNum++) {
            Row hssfRow1 = sheet.getRow(rowNum);
            if (hssfRow1 == null) {
                rowindex--;
            }else if( String.valueOf(getHValue(hssfRow1.getCell(0), (short) 1)).equals("")){
                rowindex--;
            }
        }
        return rowindex + 1;
    }


    //获得单元格值，以及在c的位置获取
    private String getHValue(Cell hssfCell, Short c) {

        int cellType = hssfCell.getCellType();

        if (cellType == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (cellType == hssfCell.CELL_TYPE_NUMERIC) {  //数值
            System.out.println();
            String cellValue = "";
            DecimalFormat df = new DecimalFormat("#.######");
            String cellDoublevalue = df.format(hssfCell.getNumericCellValue());//去科学计数
            if ((int) c == 0) {
                cellValue = String.valueOf(Integer.parseInt(cellDoublevalue));//第一行员工编号
                return cellValue;
            }
            if ((Double.parseDouble(cellDoublevalue) > ridpoint(cellDoublevalue)) && (ridpoint(cellDoublevalue) >= 0)) {//去小数点
                cellValue = String.valueOf(cellDoublevalue);
                return cellValue;
            } else if (ridpoint(cellDoublevalue) <= 0) {
                cellValue = String.valueOf(Double.parseDouble(cellDoublevalue));
            } else if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
                Date date = HSSFDateUtil.getJavaDate(Double.parseDouble(cellDoublevalue));
                cellValue = sdf.format(date);
            } else {
                try {
                    cellValue = String.valueOf(Long.parseLong(cellDoublevalue));
                    if((int)c !=1) {
                        cellValue = cellValue + ".00";
                    }
                }catch (NumberFormatException ne){
                    throw new RuntimeException(cellDoublevalue+",该值不符合规定。");
                }
            }
            return cellValue;
        } else {
            return hssfCell.getStringCellValue();
        }
    }

    //去除小数点
    private long ridpoint(String index) {
        try {
            if (index.contains(".")) {
                index = index.substring(0, index.indexOf("."));
                return Long.parseLong(index);
            } else {
                return Long.parseLong(index);
            }
        } catch (Exception e) {
            throw new RuntimeException(index + ",该数值不在999999999~0之间");
        }
    }


}

