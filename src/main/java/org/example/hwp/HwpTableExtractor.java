package org.example.hwp;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.Control;
import kr.dogfoot.hwplib.object.bodytext.control.ControlTable;
import kr.dogfoot.hwplib.object.bodytext.control.ControlType;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.object.bodytext.control.table.Row;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.reader.HWPReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HwpTableExtractor {

    /**
     * hwpFiles: 검사할 파일 배열
     * keyword: 찾을 제목(문자열). 해당 문단을 찾고 그 "다음 문단"에서 표 제어(ControlTable)를 찾는다.
     * 반환: 표의 행-열 텍스트 목록 (빈 리스트면 표를 못 찾음)
     */
    public List<List<String>> extract(File[] hwpFiles, String keyword) {
        List<List<String>> result = new ArrayList<>();

        if (hwpFiles == null || hwpFiles.length == 0) {
            return result;
        }

        for (File file : hwpFiles) {
            if (file == null || !file.exists() || !file.isFile()) continue;

            HWPFile hwp = null;
            try {
                hwp = HWPReader.fromFile(file.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("파일 읽기 실패: " + file.getName() + " -> " + e.getMessage());
                continue;
            }

            if (hwp == null || hwp.getBodyText() == null) {
                System.out.println("읽을 수 없는 HWP (null): " + file.getName());
                continue;
            }

            List<List<String>> found = extractFromSingleHwp(hwp, keyword);
            if (!found.isEmpty()) {
                System.out.println("표 발견: " + file.getName());
                result.addAll(found); // 여러 파일의 표 결과 누적
            }
        }

        return result;
    }

    // 단일 HWPFile에서 키워드 아래 첫 번째 표만 추출
    private List<List<String>> extractFromSingleHwp(HWPFile hwp, String keyword) {
        List<List<String>> tableData = new ArrayList<>();

        for (Section section : hwp.getBodyText().getSectionList()) {
            // Section.getParagraphs() returns Paragraph[]
            Paragraph[] paragraphs = section.getParagraphs();
            if (paragraphs == null) continue;

            boolean keywordFound = false;

            for (int i = 0; i < paragraphs.length; i++) {
                Paragraph p = paragraphs[i];
                if (p == null) continue;

                String paraText = safeGetParagraphText(p);
                if (!keywordFound) {
                    if (paraText != null && paraText.contains(keyword)) {
                        keywordFound = true;
                    }
                    continue;
                }

                // keywordFound == true -> 이 문단(혹은 이후 문단)에 표가 있는지 검사
                // 표는 보통 다음 문단의 ControlList 안에 있으므로 현재 문단의 컨트롤들도 검사
                List<Control> ctrlList = p.getControlList();
                if (ctrlList != null) {
                    for (Control ctrl : ctrlList) {
                        if (ctrl == null) continue;
                        if (ctrl.getType() == ControlType.Table) {
                            ControlTable table = (ControlTable) ctrl;
                            return convertControlTableToList(table);
                        }
                    }
                }

                // 만약 현재 문단에 표가 없고 다음 문단을 검사해야 한다면 continue하여 다음 루프에서 검사됨
            }
        }

        return tableData;
    }

    // ControlTable -> List<List<String>> 변환 (Row 순회 -> Cell 순회)
    private List<List<String>> convertControlTableToList(ControlTable table) {
        List<List<String>> rowsOut = new ArrayList<>();
        if (table == null) return rowsOut;

        List<Row> rowList = table.getRowList();
        if (rowList == null) return rowsOut;

        for (Row r : rowList) {
            if (r == null) continue;
            List<Cell> cellList = r.getCellList();
            List<String> row = new ArrayList<>();
            if (cellList != null) {
                for (Cell c : cellList) {
                    if (c == null) {
                        row.add("");
                        continue;
                    }
                    // Cell 내부의 ParagraphList 전체 텍스트 결합
                    String cellText = safeGetCellText(c);
                    row.add(cellText != null ? cellText : "");
                }
            }
            rowsOut.add(row);
        }

        return rowsOut;
    }

    // Paragraph에서 안전하게 텍스트 추출 (null-safe)
    private String safeGetParagraphText(Paragraph p) {
        try {
            // Paragraph.getNormalString() 이 존재하면 사용
            String s = p.getNormalString();
            if (s != null) return s.trim();
        } catch (Throwable ignored) {}

//        try {
//            // 대체 접근 (구조에 따라 다름)
//            if (p.getText() != null) {
//                String s = p.getText().getNormalString();
//                if (s != null) return s.trim();
//            }
//        } catch (Throwable ignored) {}

        return null;
    }

    // Cell에서 텍스트를 안전하게 취합
    private String safeGetCellText(Cell cell) {
        if (cell == null) return "";
        try {
            // 대부분의 구현에서 cell.getParagraphList().getNormalString() 으로 전체 셀텍스트를 얻음
            String s = cell.getParagraphList().getNormalString();
            if (s != null) return s.trim();
        } catch (Throwable ignored) {}

        // fallback: 각 paragraph를 순회하여 합침
        try {
            // cell.getParagraphList() 객체는 iterable일 수 있으므로 for-each로 합치기
            StringBuilder sb = new StringBuilder();
            Iterable<?> paraIterable = (Iterable<?>) cell.getParagraphList();
            for (Object obj : paraIterable) {
                if (obj instanceof Paragraph) {
                    String t = safeGetParagraphText((Paragraph) obj);
                    if (t != null) sb.append(t);
                }
            }
            return sb.toString().trim();
        } catch (Throwable ignored) {}

        return "";
    }
}