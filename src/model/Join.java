package model;

public class Join {
    public String type; // INNER, LEFT, etc.
    public String leftAlias;
    public String rightAlias;
    public String onCondition;

    public Join(String type, String leftAlias, String rightAlias, String onCondition) {
        this.type = type;
        this.leftAlias = leftAlias;
        this.rightAlias = rightAlias;
        this.onCondition = onCondition;
    }
     @Override
    public String toString() {
        return String.format(
            "Join(type=%s, left=%s, right=%s, on=%s)",
            type, leftAlias, rightAlias, onCondition
        );
    }
}