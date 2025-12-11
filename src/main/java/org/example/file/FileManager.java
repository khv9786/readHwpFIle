package org.example.file;

import java.io.File;
import java.util.*;

public class FileManager {

    private static final String HWP_EXT = ".hwp";

    public File[] hasFileDir(String path) {
        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("디렉토리가 존재하지 않습니다.");
            return null;
        }

        // **하위 디렉터리까지 전체 탐색하도록 변경**
        List<File> foundFiles = findHwpFilesRecursive(dir);

        if (foundFiles.isEmpty()) {
            System.out.println("해당 경로 및 하위 경로에 HWP 파일이 없습니다.");
            return null;
        }

        // 출력
        System.out.println("총 발견된 HWP 파일: " + foundFiles.size());
        for (File f : foundFiles) {
            System.out.println("- " + f.getAbsolutePath());
        }

        return foundFiles.toArray(new File[0]);
    }

    public static List<File> findHwpFilesRecursive(File dir) {
        List<File> result = new ArrayList<>();
        Queue<File> queue = new LinkedList<>();

        queue.add(dir);

        while (!queue.isEmpty()) {
            File current = queue.poll();

            File[] files = current.listFiles();
            if (files == null) continue;

            for (File file : files) {
                if (file.isDirectory()) {
                    queue.add(file);
                } else if (file.getName().toLowerCase().endsWith(HWP_EXT)) {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
