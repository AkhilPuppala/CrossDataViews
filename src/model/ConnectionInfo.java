package model;

public class ConnectionInfo {

    public String type;
    public String host;
    public String user;
    public String password;
    public String dbName;
    public String port;


    public ConnectionInfo(String type, String host, String user, String password, String dbName, String port) {
        this.type = type;
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String getPort() {
        return port;
    }
}