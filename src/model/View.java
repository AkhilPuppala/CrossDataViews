package model;

import java.util.List;

public class View {
    public List<Table> tables;
    public List<Join> joins;
    public List<Filter> filters;
    public List<String> selectColumns;

    public View(List<Table> tables, List<Join> joins, List<Filter> filters, List<String> selectColumns) {
        this.tables = tables;
        this.joins = joins;
        this.filters = filters;
        this.selectColumns = selectColumns;
    }
}
