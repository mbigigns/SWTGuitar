import java.lang.reflect.Constructor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class VisualizationGenerator {
	public Shell rootWindow = readWindow();
	public void readGUI(Display display)
	{
		readComponent(rootWindow);

        while (!rootWindow.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
              }
        }
	}
	
	private Shell readWindow()
	{
		Shell window = createWindow("",0,0,302,307);
        window.setSize(302, 307);
        window.open();
        return window;
	}
	
	private void readComponent(Composite window)
	{	
		//addWidget("org.eclipse.swt.widgets.Button","",0,0,20,20,0,window);
	}
	
	public Shell createWindow(String ID, int x, int y, int width, int height)
	{
		Shell window = new Shell();
		return window;
	}
	
	public Control addWidget(String classType, String ID, int x, int y, int width, int height, int style, Composite container)
	{
		Class [] classParm = {Composite.class, int.class};
		Object [] objectParm = {};
		Control control = null;         
		try 
		{
			System.out.println(x+" "+container.getBounds().x);
			int trueX = x-DomParserExample.containerOffsets.get(container).x;
			int trueY = y-DomParserExample.containerOffsets.get(container).y;
			System.out.println(trueX+" "+trueY);
			Class cl = Class.forName(classType);
			Constructor co = cl.getConstructor(classParm);
			control = (Control) co.newInstance(container, style);
			control.setBounds(trueX, trueY, width, height);
			control.setToolTipText(Integer.toString(control.hashCode()));//ID);
		}
		catch (Exception e) {
		  e.printStackTrace();
		}
		return control;
	}
}
