package com.payinsight;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class Main {

    public static void main(String[] args) {

        String xmlPath = "data/payments.xml";
        String xsdPath = "data/payments.xsd";

        validateXML(xmlPath, xsdPath);
    }

    public static void validateXML(String xmlPath, String xsdPath) {

        try {
            // Create the schema factory
            SchemaFactory factory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // Load the XSD schema
            Schema schema = factory.newSchema(new File(xsdPath));

            // Create the validator
            Validator validator = schema.newValidator();

            // Validate the XML document
            validator.validate(new javax.xml.transform.stream.StreamSource(
                    new File(xmlPath)));

            System.out.println("XML validation successful!");
            System.out.println("payments.xml is valid according to payments.xsd.");

        } catch (SAXException e) {
            System.out.println("XML validation failed!");
            System.out.println("Schema validation error: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}