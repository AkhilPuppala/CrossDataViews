package model;

import java.util.Map;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.*;

import org.w3c.dom.*;

public class Catalog {
    public Map<String, DatabaseInfo> databases = new HashMap<>(); // dbName -> connection and list of tables


    public String getDbNameForTable(String tableName) {
        for (Map.Entry<String, DatabaseInfo> entry : databases.entrySet()) {
            if (entry.getValue().tables.contains(tableName)) {
                return entry.getKey();
            }
        }
        return "";
    }
    
    private static String text(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).getTextContent() != null) {
            return nl.item(0).getTextContent().trim();
        }
        return "";
    }

    public Catalog(String fileName) {
        // 1) Parse the XML
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder        db  = dbf.newDocumentBuilder();
            Document               doc = db.parse(new File(fileName));
            doc.getDocumentElement().normalize();
    
            // 2) Find each <DataBase> entry
            NodeList dbList = doc.getDocumentElement()
                                 .getElementsByTagName("DataBase");

            for (int i = 0; i < dbList.getLength(); i++) {
                Element dbElem = (Element) dbList.item(i);
    
                // 3) Read the <name> for map key and dbName field
                String name = text(dbElem, "name");
    
                // 4) Read the nested <Connection> info
                Element connElem = (Element) dbElem
                    .getElementsByTagName("Connection")
                    .item(0);
                String type     = text(connElem, "type");
                String host     = text(connElem, "host");
                String port     = text(connElem, "port");
                String user     = text(connElem, "username");
                String password = text(connElem, "password");
    
    
                // 6) Gather all <Table> entries into a Set<Table>
                HashSet<String> tables = new HashSet<>();
                NodeList tableNodes = dbElem
                    .getElementsByTagName("Table");
                for (int j = 0; j < tableNodes.getLength(); j++) {
                    String tbl = tableNodes
                        .item(j)
                        .getTextContent()
                        .trim();
                    if (!tbl.isEmpty()) {
                        tables.add(tbl);
                    }
                }
                
                ConnectionInfo connection = new ConnectionInfo(
                    type,   // type
                    host,   // host
                    user,   // user
                    password,// password
                    name,   // dbName (same as <name>)
                    port   // port
                );

                DatabaseInfo info = new DatabaseInfo(
                        connection,
                        tables
                    );
    
                databases.put(name, info);
            }
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("Failed to parse catalog XML", e);
        }

    }

}
