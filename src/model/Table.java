package model;

public class Table {
    public String alias;
    public String name;

    public Table(String alias, String name) {
        this.alias = alias;
        this.name = name;
    }
     @Override
    public String toString() {
        return String.format("Table(alias=%s, name=%s)", alias, name);
    }
}