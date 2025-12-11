package org.example.file;

import java.io.File;

public class FileManager {


    public File[] hasFileDir(String path){
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("디렉토리가 존재하지 않습니다.");
            return null;
        }

        File[] hwpFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".hwp"));

        if (hwpFiles == null || hwpFiles.length == 0) {
            System.out.println("해당 경로에 HWP 파일이 없습니다.");
            return null;
        }

        System.out.println("총 발견된 HWP 파일: " + hwpFiles.length);
        for (File f : hwpFiles) {
            System.out.println("- " + f.getName());
        }
        return hwpFiles;
    }
}
