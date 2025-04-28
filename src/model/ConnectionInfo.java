package model;

public class ConnectionInfo {
    public String type;
    public String host;
    public String user;
    public String password;
    public String dbName;

    public ConnectionInfo(String type, String host, String user, String password, String dbName) {
        this.type = type;
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
    }
}