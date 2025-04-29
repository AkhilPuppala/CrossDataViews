package db;

import model.ConnectionInfo;
import model.TableSchema;
import org.basex.core.Context;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.XQuery;
import org.basex.api.client.ClientSession;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;

public class RecursiveBaseXFetcher implements Fetcher {

    @Override
    public List<Map<String, Object>> fetchData(String tableName, ConnectionInfo connectionInfo) {
        List<Map<String, Object>> result = new ArrayList<>();
        ClientSession session = null;
        try {
            // Connect to BaseX server
            session = new ClientSession(connectionInfo.getHost(), Integer.parseInt(connectionInfo.getPort()),
                                        connectionInfo.getUser(), connectionInfo.getPassword());

            // Open the database
            // passcode
            session.execute(new Open(tableName));

            // Query all <row> elements inside <root>
            String query = "for $r in /root/row return serialize($r)";
            String response = session.execute(new XQuery(query));

            // Split response into individual <row> XML strings
            String[] rows = response.split("\n(?=<row>)"); // Split at newline before <row>

            for (String xmlRow : rows) {
                Map<String, Object> parsedRow = parseXmlRow(xmlRow.trim());
                if (!parsedRow.isEmpty()) {
                    result.add(parsedRow);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    private Map<String, Object> parseXmlRow(String xmlRow) {
        Map<String, Object> row = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlRow)));
            Element element = doc.getDocumentElement();
            parseElement(element, row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    private void parseElement(Element element, Map<String, Object> map) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                NodeList grandChildren = childElement.getChildNodes();
                boolean hasElementChild = false;

                for (int j = 0; j < grandChildren.getLength(); j++) {
                    if (grandChildren.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        hasElementChild = true;
                        break;
                    }
                }

                if (hasElementChild) {
                    // Nested structure: example <Items><Item>1</Item><Item>2</Item></Items>
                    List<Object> list = new ArrayList<>();
                    for (int k = 0; k < grandChildren.getLength(); k++) {
                        Node nested = grandChildren.item(k);
                        if (nested.getNodeType() == Node.ELEMENT_NODE) {
                            Element nestedElement = (Element) nested;
                            list.add(tryParseNumber(nestedElement.getTextContent().trim()));
                        }
                    }
                    map.put(childElement.getTagName(), list);
                } else {
                    // Simple text field
                    String textContent = childElement.getTextContent().trim();
                    Object value = tryParseNumber(textContent);
                    map.put(childElement.getTagName(), value);
                }
            }
        }
    }

    private Object tryParseNumber(String text) {
        try {
            if (text.contains(".")) {
                return Double.parseDouble(text);
            } else {
                return Integer.parseInt(text);
            }
        } catch (NumberFormatException e) {
            return text; // Return as String if not a number
        }
    }

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/tableName";
        String user = "root";
        String password = "passcode";
        
        ConnectionInfo connection = new ConnectionInfo("xml","localhost","akhil","passcode","Invoice","1984");
        
        Fetcher fetcher = new RecursiveBaseXFetcher();
        List<Map<String, Object>> data = fetcher.fetchData("Invoice", connection);

        // Print the fetched data
        System.out.println("Fetched Data:");
        System.out.println("Number of records: " + data.size());

        if (!data.isEmpty()) {
            System.out.println("First Record: " + data.get(0));
        } else {
            System.out.println("No records found.");
        }
        
    }
}
