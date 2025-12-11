package org.example;

import org.example.excel.ExcelWriter;
import org.example.file.FileManager;
import org.example.hwp.HwpTableExtractor;
import org.example.model.TableResult;

import java.io.File;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static FileManager fileManager;


    public static void main(String[] args) throws Exception {
        fileManager = new FileManager();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("HWP 경로 입력: ");
            String path = sc.nextLine();

            if (path == null) {
                System.out.println("올바르지 않은 경로 입력");
                continue;
            }
            File[] hwpFiles = fileManager.hasFileDir(path);
            if (hwpFiles == null) {continue;}


            System.out.print("표 제목(키워드) 입력: ex) 일반 현황, 수로현황 ... etc ");
            String[] keywords = sc.nextLine().split(",");

            HwpTableExtractor extractor = new HwpTableExtractor();
            List<TableResult> tableResult = extractor.extractTables(hwpFiles, keywords);

            ExcelWriter writer = new ExcelWriter();
            writer.write(path, tableResult);

            System.out.println("반복하려면 1, 종료하려면 2");
            String cmd = sc.nextLine();
            if(cmd.equals("2")){
                break;
            }
        }
    }
}
