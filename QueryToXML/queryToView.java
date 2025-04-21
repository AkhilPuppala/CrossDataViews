import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class queryToView {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java queryToView <input-xml-file>");
            return;
        }

        try {
            File inputFile = new File(args[0]);
            File outputFile = new File("output.xml");

            // Load and parse input XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document inputDoc = builder.parse(inputFile);
            inputDoc.getDocumentElement().normalize();

            // Create output document
            Document outputDoc = builder.newDocument();

            // Root element
            Element view = outputDoc.createElement("View");
            view.setAttribute("name", "InvoiceSummary");
            outputDoc.appendChild(view);

            // --- Tables ---
            Element tables = outputDoc.createElement("Tables");
            view.appendChild(tables);

            NodeList tableNodes = inputDoc.getElementsByTagName("table");
            for (int i = 0; i < tableNodes.getLength(); i++) {
                Element tableElem = (Element) tableNodes.item(i);
                String alias = tableElem.getAttribute("alias");
                String name = tableElem.getTextContent().trim();

                Element table = outputDoc.createElement("Table");
                table.setAttribute("name", name);
                table.setAttribute("alias", alias);
                tables.appendChild(table);
            }

            // --- Joins ---
            Element joins = outputDoc.createElement("Joins");
            view.appendChild(joins);

            NodeList joinList = inputDoc.getElementsByTagName("join");

            // Assuming the first <from> table is always the left-most table
            Element fromElem = (Element) inputDoc.getElementsByTagName("from").item(0);
            Element fromTable = (Element) fromElem.getElementsByTagName("table").item(0);
            String fromAlias = fromTable.getAttribute("alias");
            String fromName = fromTable.getTextContent().trim();

            for (int i = 0; i < joinList.getLength(); i++) {
                Element joinElem = (Element) joinList.item(i);

                Element join = outputDoc.createElement("Join");
                join.setAttribute("type", joinElem.getAttribute("type"));
                joins.appendChild(join);

                // Right table
                Element joinTblElem = (Element) joinElem.getElementsByTagName("table").item(0);
                String joinAlias = joinTblElem.getAttribute("alias");
                String joinName = joinTblElem.getTextContent().trim();

                // LeftTable
                Element left = outputDoc.createElement("LeftTable");
                left.setAttribute("alias", fromAlias);
                left.setAttribute("name", fromName);
                join.appendChild(left);

                // RightTable
                Element right = outputDoc.createElement("RightTable");
                right.setAttribute("alias", joinAlias);
                right.setAttribute("name", joinName);
                join.appendChild(right);

                // Condition
                String condition = joinElem.getElementsByTagName("condition").item(0).getTextContent().trim();
                Element onCondition = outputDoc.createElement("OnCondition");
                onCondition.setTextContent(condition);
                join.appendChild(onCondition);
            }

            // --- Filters ---
            Element filters = outputDoc.createElement("Filters");
            view.appendChild(filters);

            String filterCond = inputDoc.getElementsByTagName("where").item(0)
                    .getTextContent().trim();
            Element conditionElem = outputDoc.createElement("Condition");
            conditionElem.setTextContent(filterCond);
            filters.appendChild(conditionElem);

            // --- Select ---
            Element select = outputDoc.createElement("Select");
            view.appendChild(select);

            NodeList columns = inputDoc.getElementsByTagName("column");
            for (int i = 0; i < columns.getLength(); i++) {
                String colText = columns.item(i).getTextContent().trim();
                Element col = outputDoc.createElement("Column");
                col.setTextContent(colText);
                select.appendChild(col);
            }

            // Write to output file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(outputDoc), new StreamResult(outputFile));

            System.out.println("Transformation complete. Output saved to output.xml.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
