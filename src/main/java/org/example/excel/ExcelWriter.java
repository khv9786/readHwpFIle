package org.example.excel;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.example.model.TableResult;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelWriter {

    public void write(String outputDir, List<TableResult> results) throws Exception {
        if (results == null || results.isEmpty()) {
            System.out.println("표 데이터 없음.");
            return;
        }

        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "hwp_table_" + today + ".xlsx";

        File dir = new File(outputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("올바르지 않은 출력 경로: " + outputDir);
        }

        String outputPath = outputDir + File.separator + fileName;

        Workbook wb = new XSSFWorkbook();
        Sheet tableSheet = wb.createSheet("Tables");

        int rowIndex = 0;

        for (TableResult tr: results) {
            // 파일명
            Row fileRow = tableSheet.createRow(rowIndex++);
            fileRow.createCell(0).setCellValue("파일: " + tr.fileName);
            fileRow.createCell(5).setCellValue("키워드: " + tr.keyword);

            // 표 내용
            for (List<String> rowData: tr.table) {
                Row row = tableSheet.createRow(rowIndex++);
                int c = 0;
                for (String v: rowData) {
                    row.createCell(c++).setCellValue(v);
                }
            }

            // 파일별 구분선
            rowIndex++;
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            wb.write(fos);
        }
        wb.close();

        System.out.println("엑셀 생성 완료: " + outputPath);
    }
}