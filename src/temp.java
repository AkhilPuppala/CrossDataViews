
public static void loadConfig(String filePath) throws Exception {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder        dBuilder = dbFactory.newDocumentBuilder();
    Document               doc      = dBuilder.parse(new File(filePath));
    doc.getDocumentElement().normalize();

    Element root = doc.getDocumentElement();
    NodeList dbList = root.getElementsByTagName("DataBase");

    for (int i = 0; i < dbList.getLength(); i++) {
        Element dbElem = (Element) dbList.item(i);
        String  name   = dbElem.getElementsByTagName("name")
                            .item(0)
                            .getTextContent()
                            .trim();

        Element conn = (Element) dbElem.getElementsByTagName("Connection").item(0);
        String type  = conn.getElementsByTagName("type").item(0).getTextContent().trim();
        String table = dbElem.getElementsByTagName("Table").item(0).getTextContent().trim();

        if ("mysql".equalsIgnoreCase(type)) {
            mysqlDbName    = name;
            mysqlUrl       = conn.getElementsByTagName("host").item(0).getTextContent().trim();
            mysqlUser      = conn.getElementsByTagName("username").item(0).getTextContent().trim();
            mysqlPassword  = conn.getElementsByTagName("password").item(0).getTextContent().trim();
            mysqlTableName = table;
        }
        else if ("basex".equalsIgnoreCase(type)) {
            basexDbName    = name;
            basexHost      = conn.getElementsByTagName("host").item(0).getTextContent().trim();
            basexPort      = Integer.parseInt(conn.getElementsByTagName("port").item(0).getTextContent().trim());
            basexUser      = conn.getElementsByTagName("username").item(0).getTextContent().trim();
            basexPassword  = conn.getElementsByTagName("password").item(0).getTextContent().trim();
            basexTableName = table;
        }
    }
}
