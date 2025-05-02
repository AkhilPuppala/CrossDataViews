import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        
        String configFilePath = "config/config.xml";

        try {
            // Load and parse the configuration XML file
            File configFile = new File(configFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document configDoc = builder.parse(configFile);
            configDoc.getDocumentElement().normalize();

            // Extract file paths from the configuration XML
            String queryFilePath = getTextContent(configDoc, "QueryFilePath");
            String viewFilePath = getTextContent(configDoc, "ViewFilePath");
            String catalogFilePath = getTextContent(configDoc, "CatalogFilePath");
            String outputFilePath = getTextContent(configDoc, "OutputFilePath");

            System.out.println("Query File Path: " + queryFilePath);
            System.out.println("View File Path: " + viewFilePath);
            System.out.println("Catalog File Path: " + catalogFilePath);
            System.out.println("Output File Path: " + outputFilePath);

            // Step 1: Transform the query XML into a view XML
            System.out.println("Transforming query XML to view XML...");
            QueryToView.transformQueryToView(queryFilePath, viewFilePath);
            System.out.println("Transformation complete. View XML saved to: " + viewFilePath);

            // Step 2: Process the view XML and generate the output JSON
            System.out.println("Processing view XML and generating output JSON...");
            ViewEngine.viewToOutput(viewFilePath, catalogFilePath, outputFilePath);
            System.out.println("Processing complete. Output JSON saved to: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("An error occurred:");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to extract text content of a tag from the XML document.
     *
     * @param doc The XML document.
     * @param tagName The name of the tag to extract.
     * @return The text content of the tag.
     */
    private static String getTextContent(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
}