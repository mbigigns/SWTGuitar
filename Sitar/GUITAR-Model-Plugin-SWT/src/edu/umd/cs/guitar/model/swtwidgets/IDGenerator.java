package edu.umd.cs.guitar.model.swtwidgets;

/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.SitarConstants;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;


/**
 * Default ID generator for JFC application
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class IDGenerator implements GIDGenerator {
	public static Map<ComponentType,String> idMap = new HashMap<ComponentType,String>();

	static ObjectFactory factory = new ObjectFactory();
	static final int prime = 31;

	private List<String> properties;

	public IDGenerator() {
		this.properties = SitarConstants.ID_PROPERTIES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.model.GIDGenerator#genID(edu.umd.cs.guitar.model.data
	 * .GUIStructure)
	 */
	@Override
	public void generateID(GUIStructure gs) {
		for (GUIType gui : gs.getGUI()) {
			generateGUIID(gui);
		}
	}

	/**
	 * @param gui
	 */
	private void generateGUIID(GUIType gui) {
		ContainerType container = gui.getContainer();

		if (container == null)
			return;

		long windowHashCode = getWindowHashCode(gui);

		generateComponentID(container, windowHashCode);
	}

	/**
	 * @param gui
	 * @return
	 */
	private long getWindowHashCode(GUIType gui) {

		GUITypeWrapper wGUI = new GUITypeWrapper(gui);
		String title = wGUI.getTitle();
		long hashcode = title.hashCode();

		hashcode = (hashcode * 2) & 0xffffffffL;
		return hashcode;
	}

	private void generateComponentID(ComponentType component,
			long parentHashCode) {

		AttributesType attributes = component.getAttributes();

		long hashcode = 1;

		if (attributes != null) {

			long localHashCode = getLocalHashcode(component);
			hashcode = parentHashCode * prime + localHashCode;
			hashcode = (hashcode * 2) & 0xffffffffL;

			String sID = GUITARConstants.COMPONENT_ID_PREFIX + hashcode;
			idMap.put(component, sID);
			
			PropertyType property = factory.createPropertyType();
			property.setName(GUITARConstants.ID_TAG_NAME);
			property.getValue().add(sID);

			attributes.getProperty().add(0, property);
		} else {
			hashcode = parentHashCode;
		}

		if (component instanceof ContainerType) {
			ContainerType container = (ContainerType) component;
			List<ComponentType> children = container.getContents()
					.getWidgetOrContainer();

			boolean isAddIndex = !hasUniqueChildren(component);

			for (ComponentType child : children) {

				long propagatedHashCode = hashcode;
				if (isAddIndex) {
					propagatedHashCode = prime * propagatedHashCode
							+ ((Integer) children.indexOf(child)).hashCode();
				}
				
				generateComponentID(child, propagatedHashCode);
			}
		}
	}

	/**
	 * @param component
	 * @return
	 */
	private boolean hasUniqueChildren(ComponentType component) {
		if (!(component instanceof ContainerType))
			return true;
		
		List<Long> examinedHashCode = new ArrayList<Long>();
		
		ContainerType container = (ContainerType) component;
		for(ComponentType child: container.getContents().getWidgetOrContainer()){
			long hashcode = getLocalHashcode(child);
			if(examinedHashCode.contains(hashcode)){
				return false;
			}else{
				examinedHashCode.add(hashcode);
			}
		}
		return true;
	}

	/**
	 * @param component
	 * @return
	 */
	private long getLocalHashcode(ComponentType component) {
		// ComponentTypeWrapper wComponent = new
		// ComponentTypeWrapper(component);
		//
		// String sClass = wComponent
		// .getFirstValueByName(GUITARConstants.CLASS_TAG_NAME);
		// if (IGNORED_ID_CLASSES.contains(sClass)) {
		// return 0;
		// }
		//
		// preprocessID(component);

		final int prime = 31;

		long hashcode = 1;

		AttributesType attributes = component.getAttributes();
		if (attributes == null)
			return hashcode;

		List<PropertyType> lProperty = attributes.getProperty();
		if (lProperty == null)
			return hashcode;

		for (PropertyType property : lProperty) {
			if (properties.contains(property.getName())) {

				String name = property.getName();
				hashcode = (prime * hashcode + (name == null ? 0 : name
						.hashCode()));

				List<String> valueList = property.getValue();
				if (valueList != null)
					for (String value : valueList) {
						hashcode = (prime * hashcode + (value == null ? 0
								: (value.hashCode())));

					}

			}
		}

		hashcode = (hashcode * 2) & 0xffffffffL;

		return hashcode;

	}
}
