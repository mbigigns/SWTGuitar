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
		Shell window = createWindow("",0,0,307,302);
        window.setSize(307, 302);
        window.setLocation(300, 300);
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
	
	public void addWidget(String classType, String ID, int x, int y, int width, int height, int style, Composite window)
	{
		Class [] classParm = {Composite.class, int.class};
		Object [] objectParm = {};
		         
		try 
		{
		  Class cl = Class.forName(classType);
		  Constructor co = cl.getConstructor(classParm);
		  Control control = (Control) co.newInstance(window, style);
	      control.setBounds(x, y, width, height);
	      control.setToolTipText(ID);
		}
		catch (Exception e) {
		  e.printStackTrace();
		}
	}
}
