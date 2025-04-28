package model;

import java.util.Map;

public class TableSchema {
    public Map<String, String> fields; // fieldName -> dataType

    public TableSchema(Map<String, String> fields) {
        this.fields = fields;
    }
}
