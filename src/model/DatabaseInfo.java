package model;

import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class DatabaseInfo {
    public String type;
    public String host;
    public String user;
    public String password;
    public String dbName;
    public String port;
    public HashSet<String> tables;

    public DatabaseInfo(String type, String host, String user, String password, String dbName, String port, HashSet<String> tables) {
        this.type = type;
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = port;
        this.tables = tables;
    }

    
}