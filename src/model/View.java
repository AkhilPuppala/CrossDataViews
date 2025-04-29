// package model;

// import java.util.List;

// public class View {
//     public List<Table> tables;
//     public List<Join> joins;
//     public List<Filter> filters;
//     public List<String> selectColumns;

//     public View(String fileName) {

//     }
// }

package model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class View {
    public List<Table> tables;
    public List<Join> joins;
    public List<Filter> filters;
    public List<String> selectColumns;

    public View(String fileName) {
        tables        = new ArrayList<>();
        joins         = new ArrayList<>();
        filters       = new ArrayList<>();
        selectColumns = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder        db  = dbf.newDocumentBuilder();
            Document                doc = db.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            parseTables(doc);
            parseJoins(doc);
            parseFilters(doc);
            parseSelect(doc);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse view XML", e);
        }
    }

    private void parseTables(Document doc) {
        NodeList tbls = doc.getElementsByTagName("Table");
        for (int i = 0; i < tbls.getLength(); i++) {
            Element tbl = (Element) tbls.item(i);
            String alias = tbl.getAttribute("alias");
            String name  = tbl.getAttribute("name");
            // System.out.println(alias);
            // System.out.println(name);
            tables.add(new Table(alias, name));
        }
    }

    private void parseJoins(Document doc) {
        NodeList joinNodes = doc.getElementsByTagName("Join");
        for (int i = 0; i < joinNodes.getLength(); i++) {
            Element joinEl = (Element) joinNodes.item(i);
            String type = joinEl.getAttribute("type");

            // inside each <Join> look for LeftTable, RightTable, OnCondition
            Element left  = (Element) joinEl.getElementsByTagName("LeftTable").item(0);
            Element right = (Element) joinEl.getElementsByTagName("RightTable").item(0);
            String leftAlias  = left .getAttribute("alias");
            String rightAlias = right.getAttribute("alias");

            String onCond = joinEl.getElementsByTagName("OnCondition")
                                  .item(0)
                                  .getTextContent()
                                  .trim();

            joins.add(new Join(type, leftAlias, rightAlias, onCond));
        }
    }

    private void parseFilters(Document doc) {
        NodeList conds = doc.getElementsByTagName("Condition");
        for (int i = 0; i < conds.getLength(); i++) {
            String condition = conds.item(i).getTextContent().trim();
            filters.add(new Filter(condition));
        }
    }

    private void parseSelect(Document doc) {
        NodeList cols = doc.getElementsByTagName("Select")
                          .item(0)
                          .getChildNodes();
        for (int i = 0; i < cols.getLength(); i++) {
            Node node = cols.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && "Column".equals(node.getNodeName())) {
                String col = node.getTextContent().trim();
                selectColumns.add(col);
            }
        }
    }

    // for quick testing
    public static void main(String[] args) {
        View v = new View("output.xml");
        System.out.println("Tables: " + v.tables);
        System.out.println("Joins: " + v.joins);
        System.out.println("Filters: " + v.filters);
        System.out.println("Select: " + v.selectColumns);
    }
}
