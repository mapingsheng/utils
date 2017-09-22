package com.xes.teacherrestserver.base.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 用来创建excel
 * @author 吕小舜
 *
 */
public class ExcelUtil {
    
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String []title, String [][]values, HSSFWorkbook wb){
         // 第一步，创建一个webbook，对应一个Excel文件  
        if(wb == null){
            wb = new HSSFWorkbook();
        }
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet(sheetName);  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow(0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式  
        
        HSSFCell cell = null;  
        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);  
            cell.setCellValue(title[i]);  
            cell.setCellStyle(style);  
        }
        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1); 
            for(int j=0;j<values[i].length;j++){
                 row.createCell(j).setCellValue(values[i][j]);
            }
        }
        
       return wb;
    }
    
}