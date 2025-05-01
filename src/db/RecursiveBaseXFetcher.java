package db;

import model.ConnectionInfo;
// import model.TableSchema;
// import org.basex.core.Context;
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
    public List<Map<String, Object>> fetchData(String dbName, ConnectionInfo connectionInfo) {
        List<Map<String, Object>> result = new ArrayList<>();
        ClientSession session = null;
        try {
            // Connect to BaseX server
            session = new ClientSession(connectionInfo.getHost(), Integer.parseInt(connectionInfo.getPort()),
                                        connectionInfo.getUser(), connectionInfo.getPassword());

            // Open the database
            // passcode
            session.execute(new Open(dbName));

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
        Map<String, List<Element>> grouped = new LinkedHashMap<>();
    
        // Group child elements by tag name
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                grouped.computeIfAbsent(childElement.getTagName(), k -> new ArrayList<>()).add(childElement);
            }
        }
    
        for (Map.Entry<String, List<Element>> entry : grouped.entrySet()) {
            String tag = entry.getKey();
            List<Element> elements = entry.getValue();
    
            if (elements.size() == 1) {
                // Only one element: either leaf or nested object
                Element single = elements.get(0);
                if (hasElementChildren(single)) {
                    Map<String, Object> nestedMap = new HashMap<>();
                    parseElement(single, nestedMap);
                    map.put(tag, nestedMap);
                } else {
                    map.put(tag, tryParseNumber(single.getTextContent().trim()));
                }
            } else {
                // Multiple elements with same tag: treat as list
                List<Object> list = new ArrayList<>();
                for (Element e : elements) {
                    if (hasElementChildren(e)) {
                        Map<String, Object> nestedMap = new HashMap<>();
                        parseElement(e, nestedMap);
                        list.add(nestedMap);
                    } else {
                        list.add(tryParseNumber(e.getTextContent().trim()));
                    }
                }
                map.put(tag, list);
            }
        }
    }
    
    private boolean hasElementChildren(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
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
        RecursiveBaseXFetcher fetcher = new RecursiveBaseXFetcher();
    
        String[] xmlRows = {
            """
            <row>
                <AId>1</AId>
                <Name>Alice</Name>
                <Age>21</Age>
                <Address>
                    <City>New York</City>
                    <Zip>10001</Zip>
                </Address>
                <Scores>
                    <Score>95</Score>
                    <Score>89</Score>
                </Scores>
            </row>
            """,
            """
            <row>
                <AId>2</AId>
                <Name>Bob</Name>
                <Age>25</Age>
                <Address>
                    <City>Los Angeles</City>
                    <Zip>90001</Zip>
                </Address>
                <Scores>
                    <Score>82</Score>
                    <Score>76</Score>
                </Scores>
            </row>
            """,
            """
            <row>
                <AId>3</AId>
                <Name>Charlie</Name>
                <Age>23</Age>
                <Address>
                    <City>Chicago</City>
                    <Zip>60601</Zip>
                </Address>
                <Scores>
                    <Score>88</Score>
                    <Score>91</Score>
                </Scores>
            </row>
            """
        };
    
        List<Map<String, Object>> results = new ArrayList<>();
        for (String xml : xmlRows) {
            results.add(fetcher.parseXmlRow(xml));
        }
        
        System.out.println(results);
        
    }
    
}
