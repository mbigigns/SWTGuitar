

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
	static VisualizationGenerator a;
	
	public static void main(String[] args)
	{
		Display display = new Display();
		DomParserExample blah = new DomParserExample();
		a = new VisualizationGenerator();    
		blah.runExample();
		a.readGUI(display);
        display.dispose();
	}
	
	public DomParserExample(){
		//create a list to hold the employee objects
		myEmpls = new ArrayList();
	}

	public void runExample() {

		//parse the xml file and get the dom object
		parseXmlFile();

		//get each employee element and create a Employee object
		parseDoc();
		//generateXML();

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
			dom = db.parse("Demo.GUI");
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

	private void parseDoc(){

		HashMap <String, String> eventMap;

		Element efg = dom.getDocumentElement();

		//Element events = (Element)(efg.getElementsByTagName("Attributes").item(0));

		NodeList nodeEventList = efg.getElementsByTagName("Attributes");

		for(int i = 0; i < nodeEventList.getLength(); i++)
		{
			eventMap = new HashMap <String, String>();
			Element eventElement = (Element)(nodeEventList.item(i));
			NodeList properties = eventElement.getElementsByTagName("Property");
			for(int j=0;j<properties.getLength();j++)
			{
				Element property = (Element)(properties.item(j));
				NodeList nlList = property.getElementsByTagName("Name").item(0).getChildNodes();
		        Node nValue = (Node) nlList.item(0);
				String propertyName = property.getElementsByTagName("Name").item(0).getChildNodes().item(0).getNodeValue();

				if(propertyName.equals("ID"))
				{
					eventMap.put("ID", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("Class"))
				{
					eventMap.put("Class", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("X"))
				{
					eventMap.put("X", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("Y"))
				{
					eventMap.put("Y", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("height"))
				{
					eventMap.put("height", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("width"))
				{
					eventMap.put("width", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				else if(propertyName.equals("text"))
				{
					//eventMap.put("text", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
				}
				if(eventMap.containsKey("Class"))
				{
					if(eventMap.get("Class").toString().equals("org.eclipse.swt.widgets.Shell")||eventMap.get("Class").toString().equals("org.eclipse.swt.widgets.Group"))
					{
						
					}
					else if(eventMap.containsKey("X")&&eventMap.containsKey("Y")&&eventMap.containsKey("height")&&eventMap.containsKey("width"))
					{
						System.out.println(eventMap);
						a.addWidget(eventMap.get("Class"), 
								eventMap.get("ID"), Integer.parseInt(eventMap.get("X")), 
								Integer.parseInt(eventMap.get("Y")), Integer.parseInt(eventMap.get("width")), 
								Integer.parseInt(eventMap.get("height")), 0, 
								a.rootWindow);
					}
				}
			}
			eventList.add(eventMap);
		}
	}

}
