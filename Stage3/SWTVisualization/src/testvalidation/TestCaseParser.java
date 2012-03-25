package testvalidation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.*;
import org.eclipse.swt.widgets.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class TestCaseParser {

	//parses the test case file and returns the events as an arraylist
	/**
	 * @param file - String name of testcase file (eg: test01.tst)
	 * @return ArrayList<String> containing the eventIDs of the subsequent test events
	 */
	public static ArrayList<String> parseTst(String file){
		Document dom = getDom(file);
		
		if(dom == null) { 
			System.out.println("Error in .tst file");
			//e.printStackTrace();
			System.exit(0);
		}
		
		ArrayList<String> steps = new ArrayList<String>();

		NodeList stepsXml = dom.getDocumentElement().getElementsByTagName("Step");

		try {
			for(int i = 0; i < stepsXml.getLength(); i++)
			{
				Element el = (Element)stepsXml.item(i);
				NodeList events = el.getElementsByTagName("EventId");
				Node event = events.item(0);
				steps.add(event.getTextContent());
			}
		} catch (Exception e) {
			System.out.println("Error in .tst file");
			//e.printStackTrace();
			System.exit(0);
		}
		
		return steps;
	}
	
	//returns the document object of the test case file
	/**
	 * @param file - String name of testcase file (eg: test01.tst)
	 * @return Document object initialized with dom components
	 */
	private static Document getDom(String file) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(file);

		}catch(ParserConfigurationException pce) {
			System.out.println("Error reading .tst file");
			//pce.printStackTrace();
			System.exit(0);
		}catch(SAXException se) {
			System.out.println("Error reading .tst file");
			//se.printStackTrace();
			System.exit(0);
		}catch(IOException ioe) {
			System.out.println("Error reading .tst file");
			//ioe.printStackTrace();
			System.exit(0);
		}
		
		return dom;
	}
	
}
