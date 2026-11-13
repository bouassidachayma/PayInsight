package com.payinsight.data;

import com.payinsight.Main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PaymentDataService {

    private static final String XML_PATH = "data/payments.xml";
    private static final String XSD_PATH = "data/payments.xsd";

    // Load and validate XML
    private Document loadDocument() {

        try {

            boolean valid = Main.validateXML(XML_PATH, XSD_PATH);

            if (!valid) {
                throw new IllegalStateException(
                        "Payment XML validation failed."
                );
            }

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl",
                    true
            );

            factory.setFeature(
                    "http://xml.org/sax/features/external-general-entities",
                    false
            );

            factory.setFeature(
                    "http://xml.org/sax/features/external-parameter-entities",
                    false
            );

            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();

            try (InputStream input = XmlResourceUtil.open(XML_PATH)) {
                return builder.parse(input);
            }

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to load payment data: " + e.getMessage(),
                    e
            );
        }
    }

    // Convert XML transaction into Java object
    private Transaction convertToTransaction(Element element) {

        Transaction transaction = new Transaction();

        transaction.setId(element.getAttribute("id"));

        transaction.setCustomerId(
                getTagValue(element, "customerId")
        );

        transaction.setMerchantId(
                getTagValue(element, "merchantId")
        );

        transaction.setTerminalId(
                getTagValue(element, "terminalId")
        );

        Element amountElement =
                (Element) element.getElementsByTagName("amount").item(0);

        transaction.setAmount(
                Double.parseDouble(amountElement.getTextContent().trim())
        );

        transaction.setCurrency(
                amountElement.getAttribute("currency")
        );

        transaction.setDate(
                getTagValue(element, "date")
        );

        transaction.setStatus(
                getTagValue(element, "status")
        );

        return transaction;
    }

    // Read XML child element
    private String getTagValue(Element element, String tagName) {

        NodeList nodes = element.getElementsByTagName(tagName);

        if (nodes.getLength() == 0) {
            return "";
        }

        return nodes.item(0).getTextContent().trim();
    }

    // Retrieve transaction by ID
    public Transaction getTransaction(String transactionId) {

        Document document = loadDocument();

        NodeList transactions =
                document.getElementsByTagName("transaction");

        for (int i = 0; i < transactions.getLength(); i++) {

            Element element = (Element) transactions.item(i);

            String id = element.getAttribute("id");

            if (id.equals(transactionId)) {
                return convertToTransaction(element);
            }
        }

        return null;
    }

    // Retrieve approved transactions
    public Transaction[] getApprovedTransactions() {

        Document document = loadDocument();

        NodeList transactions =
                document.getElementsByTagName("transaction");

        List<Transaction> result = new ArrayList<>();

        for (int i = 0; i < transactions.getLength(); i++) {

            Element element = (Element) transactions.item(i);

            String status = getTagValue(element, "status");

            if ("approved".equalsIgnoreCase(status)) {
                result.add(convertToTransaction(element));
            }
        }

        return result.toArray(new Transaction[0]);
    }

    // Retrieve transactions above a specified amount
    public Transaction[] getTransactionsAboveAmount(double minimumAmount) {

        Document document = loadDocument();

        NodeList transactions =
                document.getElementsByTagName("transaction");

        List<Transaction> result = new ArrayList<>();

        for (int i = 0; i < transactions.getLength(); i++) {

            Element element = (Element) transactions.item(i);

            Element amountElement =
                    (Element) element.getElementsByTagName("amount").item(0);

            double amount = Double.parseDouble(
                    amountElement.getTextContent().trim()
            );

            if (amount > minimumAmount) {
                result.add(convertToTransaction(element));
            }
        }

        return result.toArray(new Transaction[0]);
    }

    // Count all transactions
    public int getTransactionCount() {

        Document document = loadDocument();

        NodeList transactions =
                document.getElementsByTagName("transaction");

        return transactions.getLength();
    }
}