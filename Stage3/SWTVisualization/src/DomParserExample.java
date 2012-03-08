

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

import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomParserExample {

	//No generics
	List myEmpls;
	static Document dom;
	static Document dom2;
	
	public static void main(String[] args)
	{
		runExample();
  
	}
	

	public static void runExample() {

		//parse the xml file and get the dom object
		parseXmlFile();

		//get each employee element and create a Employee object
		parseDoc();
		//generateXML();

		//Iterate through the list and print the data
		//printData();

	}


	private static void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse("GUITAR-Default.GUI");
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

	private static void parseDoc(){

		HashMap <String, String> eventMap = new HashMap<String, String>();

		Element GUI = dom.getDocumentElement();

		//Element events = (Element)(efg.getElementsByTagName("Attributes").item(0));

		NodeList attributeList = GUI.getElementsByTagName("Attributes");

		for(int i = 0; i < attributeList.getLength(); i++)
		{
			Element attributes = (Element)(attributeList.item(i));
			NodeList properties = attributes.getElementsByTagName("Property");
			
			eventMap = new HashMap<String, String>();
			
			for(int j=0;j<properties.getLength();j++)
			{
				Element property = (Element)(properties.item(j));
				NodeList nlList = property.getElementsByTagName("Name").item(0).getChildNodes();
		        Node nValue = (Node) nlList.item(0);
				String propertyName = property.getElementsByTagName("Name").item(0).getChildNodes().item(0).getNodeValue();
				//System.out.println(property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				
				if(propertyName.equals("ID"))
				{
					eventMap.put("ID", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("Class"))
				{
					System.out.println(property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
					eventMap.put("Class", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("bounds"))
				{
					//Rectangle {6, 48, 250, 250}
					String numbers = property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
					numbers = numbers.substring(numbers.indexOf("{") + 1, numbers.length() - 1);
					numbers = numbers.replace(" ", "");
					String[] boundList = numbers.split(",");
					
					String x = boundList[0];
					String y = boundList[1];
					String width = boundList[2];
					String height = boundList[3];
					//System.out.println(numbers);
					//System.out.println(x + y + width + height);
					//System.out.println(x + " " + " "+  y + " "+ width +" " + height);
					eventMap.put("X", x);
					eventMap.put("Y", y);
					eventMap.put("width", width);
					eventMap.put("height", height);
				}
				else if(propertyName.equals("layout"))
				{
					eventMap.put("layout", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("data"))
				{	
					//System.out.println(property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
					if(property.getElementsByTagName("Value").item(0).getFirstChild() == null)
						eventMap.put("data", "");
					else
						eventMap.put("data", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("text"))
				{
					if(property.getElementsByTagName("Value").item(0).getFirstChild() != null)
						eventMap.put("text", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("Rootwindow"))
				{
					//System.out.println("HERE");
					eventMap.put("Rootwindow", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("style"))
				{
					//System.out.println(property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
					eventMap.put("style", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
		
				
			}

			
			VisualizationGenerator.addWidget(eventMap);
		}
		VisualizationGenerator.Show();
	}

}
