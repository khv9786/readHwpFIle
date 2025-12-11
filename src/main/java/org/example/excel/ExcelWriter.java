package org.example.excel;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelWriter {

    public void write(String outputDir, List<List<String>> table) throws Exception {

        if(table == null){
            System.out.println("해당 제목의 표가 존재하지 않음.");
            return;
        }
        // 오늘 날짜 yyyyMMdd
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 파일명 자동 생성
        String fileName = "hwp_table_" + today + ".xlsx";

        // 디렉토리 보정
        File dir = new File(outputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("올바르지 않은 출력 경로: " + outputDir);
        }

        // 최종 경로
        String outputPath = outputDir + File.separator + fileName;

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("data");

        int r = 0;
        for (List<String> rowData : table) {
            Row row = sheet.createRow(r++);
            int c = 0;
            for (String v : rowData) {
                Cell cell = row.createCell(c++);
                cell.setCellValue(v);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            wb.write(fos);
        }
        wb.close();

        System.out.println("엑셀 생성 완료: " + outputPath);
    }
}