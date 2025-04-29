package model;

public class Filter {
    public String condition;

    public Filter(String condition) {
        this.condition = condition;
    }
     @Override
    public String toString() {
        return String.format("Filter(condition=%s)", condition);
    }
}
