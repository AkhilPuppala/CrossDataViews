package model;

import java.util.Map;

public class Catalog {
    public Map<String, DatabaseInfo> databases; // dbName -> connection and list of tables
    
    public Catalog(String fileName) {

    }
}
