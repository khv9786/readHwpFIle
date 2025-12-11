package org.example.model;

import java.util.List;

public class TableResult {
    public String fileName;
    public String keyword;
    public List<List<String>> table;

    public TableResult(String fileName, String keyword, List<List<String>> table) {
        this.fileName = fileName;
        this.keyword = keyword;
        this.table = table;
    }
}

