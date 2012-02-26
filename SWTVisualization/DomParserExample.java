

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomParserExample {

	//No generics
	List myEmpls;
	Document dom;
	Document dom2;


	public DomParserExample(){
		//create a list to hold the employee objects
		myEmpls = new ArrayList();
	}

	public void runExample() {

		//parse the xml file and get the dom object
		parseXmlFile();

		//get each employee element and create a Employee object
		parseDoc();
		generateXML();

		//Iterate through the list and print the data
		//printData();

	}


	private void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse("Demo.EFG");
			dom2 = db.newDocument();

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}



	public 	ArrayList<HashMap<String, String>> eventList = new ArrayList <HashMap <String, String>>();
	public 	HashMap <HashMap<String, String>, ArrayList<String>> edgeMap = new HashMap <HashMap<String, String>, ArrayList<String>>();
	public String[][] adjMatrix;

	private void parseDoc(){

		HashMap <String, String> eventMap;
		ArrayList<String> edgeList;

		Element efg = dom.getDocumentElement();

		Element events = (Element)(efg.getElementsByTagName("Events").item(0));

		NodeList nodeEventList = events.getElementsByTagName("Event");

		for(int i = 0; i < nodeEventList.getLength(); i++)
		{
			eventMap = new HashMap <String, String>();
			Element eventElement = (Element)(nodeEventList.item(i));
			//System.out.println(eventElement.getElementsByTagName("EventId").item(0).getFirstChild().getNodeValue());
			eventMap.put("EventId", eventElement.getElementsByTagName("EventId").item(0).getFirstChild().getNodeValue());
			eventMap.put("WidgetId", eventElement.getElementsByTagName("WidgetId").item(0).getFirstChild().getNodeValue());
			eventMap.put("Type", eventElement.getElementsByTagName("Type").item(0).getFirstChild().getNodeValue());
			eventMap.put("Initial", eventElement.getElementsByTagName("Initial").item(0).getFirstChild().getNodeValue());
			eventMap.put("Action", eventElement.getElementsByTagName("Action").item(0).getFirstChild().getNodeValue());

			eventList.add(eventMap);
		}


		Element eventGraph = (Element)(efg.getElementsByTagName("EventGraph").item(0));
		NodeList nodeRowList = eventGraph.getElementsByTagName("Row");
		adjMatrix = new String[eventList.size()] [eventList.size()];
		
		for(int i = 0; i < nodeRowList.getLength(); i++)
		{
			edgeList = new ArrayList <String>();
			Element rowElement = (Element)(nodeRowList.item(i));
			

			for(int j = 0; j < rowElement.getElementsByTagName("E").getLength(); j++)
			{
				//System.out.println(rowElement.getElementsByTagName("E").item(j).getFirstChild().getNodeValue());
				edgeList.add(rowElement.getElementsByTagName("E").item(j).getFirstChild().getNodeValue());
				adjMatrix[i][j] = rowElement.getElementsByTagName("E").item(j).getFirstChild().getNodeValue();
			}
			edgeMap.put(eventList.get(i), edgeList);

		}
	}



	private void generateXML(){

		//create the root element 

		/*for(int i = 0; i < adjMatrix.length; i++)
		{
			for(int j = 0; j < adjMatrix[0].length; j++)
				System.out.println(adjMatrix[i][j]);
			System.out.println("");
		}
		*/
		Element graph = dom2.createElement("graph");
		dom2.appendChild(graph);
		graph.setAttribute("id", "g");
		graph.setAttribute("edgedefault", "undirected");
		
		for(int i = 0; i < eventList.size(); i++)
		{
			Element childNode = dom2.createElement("node");
			childNode.setAttribute("id", eventList.get(i).get("EventId"));
		
			
			graph.appendChild(childNode);
			
		}
		
		
		for(int i = 0; i < eventList.size(); i++)
		{
			for(int j = 0; j < eventList.size(); j++)
			{
			
				Element childEdge = dom2.createElement("edge");
				childEdge.setAttribute("source", eventList.get(i).get("EventId"));
				//System.out.println(adjMatrix[i][j]);
				if(adjMatrix[i][j].equals("1") || adjMatrix[i][j].equals("2"))
				{	
					
					childEdge.setAttribute("target", eventList.get(j).get("EventId"));
					graph.appendChild(childEdge);
				}
			}	
		
		}
		
		//System.out.println(dom2.getChildNodes().item(0).get);

		
	    try
	    {
	      // Set up the output transformer
	      TransformerFactory transfac = TransformerFactory.newInstance();
	      Transformer trans = transfac.newTransformer();
	      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	      trans.setOutputProperty(OutputKeys.INDENT, "yes");

	      // Print the DOM node

	      StringWriter sw = new StringWriter();
	      StreamResult result = new StreamResult(sw);
	      DOMSource source = new DOMSource(dom2);
	      trans.transform(source, result);
	      String xmlString = sw.toString();

	      System.out.println(xmlString);
	    }
	    catch (TransformerException e)
	    {
	      e.printStackTrace();
	    }
	

	}




	public static void main(String[] args){
		//create an instance
		DomParserExample dpe = new DomParserExample();

		//call run example
		dpe.runExample();
	}

}
