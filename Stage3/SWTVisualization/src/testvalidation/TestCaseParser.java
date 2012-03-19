package testvalidation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.*;
import org.eclipse.swt.widgets.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class TestCaseParser {

	/**
	 * @param file - String name of testcase file (eg: test01.tst)
	 * @return ArrayList<String> containing the eventIDs of the subsequent test events
	 */
	public static ArrayList<String> parseTst(String file){
		Document dom = getDom(file);
		
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
			System.err.println("Error in .tst file");
			e.printStackTrace();
		}
		
		return steps;
	}
	
	
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
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return dom;
	}
	
}