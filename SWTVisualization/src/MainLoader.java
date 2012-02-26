import org.eclipse.swt.widgets.Display;


public class MainLoader {
	public static void main(String[] args)
	{
		DomParserExample blah = new DomParserExample();
		VisualizationGenerator a = new VisualizationGenerator();        
		Display display = new Display();
		blah.runExample();
		a.readGUI(display);
        display.dispose();
	}
}
