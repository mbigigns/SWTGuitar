

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
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
	public static Map<Composite, Point> containerOffsets= new HashMap<Composite, Point>();
	
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
System.out.println(a.rootWindow.getBounds().x);
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

	public 	HashMap <HashMap<String, String>, ArrayList<String>> edgeMap = new HashMap <HashMap<String, String>, ArrayList<String>>();

	private void parseDoc(){

		//TODO: may need to check this for null pointer exception
		Element gui = (Element)dom.getDocumentElement().getElementsByTagName("GUI").item(0);
		getShell();
		
		//temp code for initializing the default window
		containerOffsets.put(a.rootWindow, new Point(0,0));
		parseContainer(gui,a.rootWindow,0);
		//Element events = (Element)(efg.getElementsByTagName("Attributes").item(0));
	}
	private void getShell() {
		
	}

	private Map<String,String> getAttributes(Element eventElement)
	{
		Map<String,String> eventMap = new HashMap <String, String>();
		NodeList properties = eventElement.getElementsByTagName("Property");
		for(int j=0;j<properties.getLength();j++)
		{
			Element property = (Element)(properties.item(j));
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
/*			if(eventMap.containsKey("Class"))
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
			}*/
		}
		return eventMap;
	}
	private void parseContainer(Element parentNode, Composite parent, int n)
	{
		NodeList nodeEventList = parentNode.getChildNodes();//.getElementsByTagName("Container");
		
		for(int i = 0; i < nodeEventList.getLength(); i++)
		{
			//System.out.println(nodeEventList.item(i).getNodeName());
			if(nodeEventList.item(i).getNodeName().equals("Container"))
			{
				//resets the composite container to parent unless proven otherwise
				Composite container = parent;
				for(int a=0;a<n;a++)
					System.out.print("\t");
				System.out.println(parent);
				
				NodeList childNodes = nodeEventList.item(i).getChildNodes();
				for(int j=0;j<childNodes.getLength(); j++)
				{
					Node childElement = childNodes.item(j);
					String tagName = childElement.getNodeName();
					//searches for an Attributes tag for the container
					if(tagName.equals("Attributes"))
					{
						//creates that container based on the correct attributes
						Map<String,String> containerAttributes = getAttributes((Element)childElement);
						
						if(containerAttributes.get("Class").toString().equals("org.eclipse.swt.widgets.Menu"))
						{
							parseMenu();
						}
						else if(!containerAttributes.get("Class").toString().equals("org.eclipse.swt.widgets.Shell")&&!containerAttributes.get("Class").toString().equals("org.eclipse.swt.widgets.MenuItem"))
						{
							int x = Integer.parseInt(containerAttributes.get("X")); 
							int y =	Integer.parseInt(containerAttributes.get("Y"));
							container = (Composite)a.addWidget(containerAttributes.get("Class"), 
							containerAttributes.get("ID"), 
							x, 
							y, 
							Integer.parseInt(containerAttributes.get("width")), 
							Integer.parseInt(containerAttributes.get("height")), 0, 
							parent);
							containerOffsets.put(container, new Point(x,y));
						}
					}
					if(tagName.equals("Contents"))
					{
						//System.out.println("reached contents, attributes is "+container);
						//adds the contents of the container (child widgets) to the container
						NodeList contentList = childNodes.item(j).getChildNodes();
						for(int k=0;k<contentList.getLength();k++)
						{
							String contentTag = contentList.item(k).getNodeName();
							if(contentTag.equals("Container"))
							{
								parseContainer((Element)childElement,container,n+1);
							}
							else if(contentTag.equals("Widget"))
							{
								parseWidget((Element)contentList.item(k),container);
							}
						}
					}
				}
			}
		}
	}

	private void parseMenu() {
		// TODO Auto-generated method stub
		
	}

	private void parseWidget(Element element, Composite container) {
		NodeList childNodes = element.getChildNodes();
		for(int j=0;j<childNodes.getLength(); j++)
		{
			Node childElement = childNodes.item(j);
			String tagName = childElement.getNodeName();
			//searches for an Attributes tag for the container
			if(tagName.equals("Attributes"))
			{
				//watch for cast error
				Map<String,String> containerAttributes = getAttributes((Element)childElement);
				
				if(!containerAttributes.get("Class").toString().equals("org.eclipse.swt.widgets.MenuItem"))
				{
					System.out.println("Adding widget to "+container+" with "+containerAttributes+" "+container.hashCode());
					a.addWidget(containerAttributes.get("Class"), 
					containerAttributes.get("ID"), Integer.parseInt(containerAttributes.get("X")), 
					Integer.parseInt(containerAttributes.get("Y")), 
					Integer.parseInt(containerAttributes.get("width")), 
					Integer.parseInt(containerAttributes.get("height")), 0, 
					container);
				}
			}
		}
	}

}
