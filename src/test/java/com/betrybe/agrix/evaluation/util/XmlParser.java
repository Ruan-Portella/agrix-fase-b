package com.betrybe.agrix.evaluation.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {
  private DocumentBuilder builder;
  private XPath xpath;

  public XmlParser() throws ParserConfigurationException {
    this.builder = builder();
    this.xpath = xpathBuilder();
  }

  /**
   * Este método realiza o parse um arquivo XML que contém a resposta 
   * de uma execução do cálculo de Coverage do JaCoCo.
   * 
   * <p> A função retorna tanto a quantidade não coberta por testes do código 
   * como a porcentagem. </p>
   * 
   * @param file o arquivo de cobertura de teste.
   * @return um mapa, contendo o percentual de cobertura dos testes.
   */
  public Map<String, Object> parseToMap(File file)
      throws SAXException, ParserConfigurationException, IOException, XPathExpressionException {

    Map<String, Object> output = new HashMap<String, Object>();

    NodeList report = getDocumentFromFile(file);
    Node node = report.item(0);
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;

      NodeList reportCounter =
          (NodeList) xpath.evaluate("counter", element, XPathConstants.NODESET);

      List<Map<String, Object>> counter = new ArrayList<Map<String, Object>>();
      for (int i = 0; i < reportCounter.getLength(); i++) {
        Node counterNode = reportCounter.item(i);
        Element counterElement = (Element) counterNode;

        counter.add(buildCoverageResponse(counterElement));
      }

      output.put("counters", counter);

    }
    return output;
  }

  private Map<String, Object> buildCoverageResponse(Element counterElement) {
    Map<String, Object> counterMap = new HashMap<String, Object>();

    String missedLines = counterElement.getAttribute("missed");
    String coveredLines = counterElement.getAttribute("covered");

    Double percentage = (Double.parseDouble(coveredLines)
        / (Double.parseDouble(coveredLines) + Double.parseDouble(missedLines))) * 100;

    counterMap.put("type", counterElement.getAttribute("type"));
    counterMap.put("missed", missedLines);
    counterMap.put("covered", coveredLines);
    counterMap.put("percentage", Double.toString(Math.floor(percentage)));

    return counterMap;
  }

  private DocumentBuilder builder() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

    return factory.newDocumentBuilder();
  }

  private XPath xpathBuilder() {
    return XPathFactory.newInstance().newXPath();
  }

  private NodeList getDocumentFromFile(File file) throws SAXException, IOException {
    Document jacocoReport = builder.parse(file);

    jacocoReport.getDocumentElement().normalize();

    return jacocoReport.getElementsByTagName("report");
  }
}