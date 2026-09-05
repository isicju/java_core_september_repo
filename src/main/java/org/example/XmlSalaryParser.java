package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlSalaryParser implements SalaryParser {
    @Override
    public List<SalaryRecord> parse(Path path) throws IOException {
        List<SalaryRecord> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path.toFile());
            doc.getDocumentElement().normalize();

            NodeList recordNodes = doc.getElementsByTagName("record");

            for (int i = 0; i < recordNodes.getLength(); i++) {
                Node node = recordNodes.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element recordElement = (Element) node;

                try {
                    String name = getElementText(recordElement, "user_name");
                    String salaryText = getElementText(recordElement, "salary");
                    String dateText = getElementText(recordElement, "date");

                    if (name.trim().isEmpty() || !name.trim().matches("\\w+")) {
                        System.err.println("Invalid record format! Invalid name in record " + (i + 1));
                        continue;
                    }

                    int salary = Integer.parseInt(salaryText.trim());
                    LocalDate date = LocalDate.parse(dateText.trim(), formatter);

                    if (salary < 0) {
                        System.err.println("Invalid record format! Negative salary in record " + (i + 1));
                        continue;
                    }

                    results.add(new SalaryRecord(name.trim(), salary, date));
                } catch (Exception e) {
                    System.err.println("Invalid record format! Record " + (i + 1) + " " + e.getMessage());
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            System.err.println("Failed to parse XML file: " + e.getMessage());
        }

        return results;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) {
            return null;
        }
        Node node = nodeList.item(0);
        return node.getTextContent();
    }
}
