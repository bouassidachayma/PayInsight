package com.payinsight;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Main {

    public static void main(String[] args) {

        String xmlPath = "data/payments.xml";
        String xsdPath = "data/payments.xsd";

        System.out.println("===== XML/XSD VALIDATION =====");

        boolean valid = validateXML(xmlPath, xsdPath);

        if (valid) {
            System.out.println("\n===== XPATH QUERIES =====");
            executeXPathQueries(xmlPath);
        }
    }

    // FUNCTIONALITY 1: XML/XSD validation
    public static boolean validateXML(String xmlPath, String xsdPath) {

        try {
            SchemaFactory factory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Schema schema = factory.newSchema(new File(xsdPath));

            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(new File(xmlPath)));

            System.out.println("XML validation successful!");
            System.out.println("payments.xml respects payments.xsd.");

            return true;

        } catch (Exception e) {
            System.out.println("XML validation failed!");
            System.out.println(e.getMessage());

            return false;
        }
    }

    // FUNCTIONALITY 2: XPath queries
    public static void executeXPathQueries(String xmlPath) {

        try {
            // Load the XML document
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = dbFactory.newDocumentBuilder();

            Document document = builder.parse(new File(xmlPath));

            // Create the XPath processor
            XPathFactory xpathFactory = XPathFactory.newInstance();

            XPath xpath = xpathFactory.newXPath();

            // Query 1: Find all merchant names
            executeQuery(
                    xpath,
                    document,
                    "/paymentSystem/merchants/merchant/name",
                    "Merchant names"
            );

            // Query 2: Find IDs of active terminals
            executeQuery(
                    xpath,
                    document,
                    "/paymentSystem/terminals/terminal[status='active']/@id",
                    "Active terminal IDs"
            );

            // Query 3: Find approved transaction IDs
            executeQuery(
                    xpath,
                    document,
                    "/paymentSystem/transactions/transaction[status='approved']/@id",
                    "Approved transaction IDs"
            );

            // Query 4: Find transactions above 100 TND
            executeQuery(
                    xpath,
                    document,
                    "/paymentSystem/transactions/transaction[amount > 100]/@id",
                    "Transactions above 100 TND"
            );

            // Query 5: Find declined transaction IDs
            executeQuery(
                    xpath,
                    document,
                    "/paymentSystem/transactions/transaction[status='declined']/@id",
                    "Declined transaction IDs"
            );

            // Query 6: Find the description of error code 05
            executeQuery(
                    xpath,
                    document,
                    "/paymentSystem/errorCodes/error[@code='05']/description",
                    "Description of error code 05"
            );

        } catch (Exception e) {
            System.out.println("XPath error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Execute and display one XPath query
    public static void executeQuery(
            XPath xpath,
            Document document,
            String expression,
            String title) throws Exception {

        System.out.println("\n" + title + ":");

        XPathExpression compiledExpression =
                xpath.compile(expression);

        NodeList nodes = (NodeList) compiledExpression.evaluate(
                document,
                XPathConstants.NODESET
        );

        if (nodes.getLength() == 0) {
            System.out.println("No results found.");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println("- " + nodes.item(i).getTextContent());
        }
    }
}