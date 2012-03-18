import edu.umd.cs.guitar.model.SitarConstants;
import edu.umd.cs.guitar.replayer.SitarReplayer;
import edu.umd.cs.guitar.replayer.SitarReplayerConfiguration;
import edu.umd.cs.guitar.ripper.SitarRipper;
import edu.umd.cs.guitar.ripper.SitarRipperConfiguration;
import edu.umd.cs.guitar.ripper.SitarRunner;
import edu.umd.cs.guitar.ripper.test.aut.SWTBasicApp;
import edu.umd.cs.guitar.ripper.test.aut.SWTMenuBarApp;
import edu.umd.cs.guitar.ripper.test.aut.SWTMultiWindowDynamicApp;
import edu.umd.cs.guitar.ripper.test.aut.SWTToolbarApp;
import edu.umd.cs.guitar.ripper.test.aut.SWTTwoWindowsApp;
import org.eclipse.swt.examples.controlexample.*;


public class test {
	public static void main(String args[])
	{
		SitarRipperConfiguration config = new SitarRipperConfiguration();
		config.setMainClass(ControlExample
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				.class.getName());
		SitarRipper ripper = new SitarRipper(config);

		
		SitarConstants.WINDOW_PROPERTIES_LIST.add("data");
		SitarConstants.WINDOW_PROPERTIES_LIST.add("parent");
		SitarConstants.WINDOW_PROPERTIES_LIST.add("size");
		SitarConstants.WINDOW_PROPERTIES_LIST.add("style");
		SitarConstants.WINDOW_PROPERTIES_LIST.add("bounds");
		
		SitarConstants.WIDGET_PROPERTIES_LIST.add("data");
		SitarConstants.WIDGET_PROPERTIES_LIST.add("parent");
		SitarConstants.WIDGET_PROPERTIES_LIST.add("size");
		SitarConstants.WIDGET_PROPERTIES_LIST.add("style");
		SitarConstants.WIDGET_PROPERTIES_LIST.add("bounds");
		SitarConstants.WIDGET_PROPERTIES_LIST.add("layout");
		
		/*
		SitarReplayerConfiguration replayerConfig = new SitarReplayerConfiguration();
		replayerConfig.setMainClass(SWTMultiWindowDynamicApp.class.getName());
		replayerConfig.setGuiFile("/root/Sitar/GUITAR-Replayer-Plugin-SWT-Test/guistructures/SWTMultiWindowDynamicApp.GUI.xml");
		replayerConfig.setEfgFile("/root/Sitar/GUITAR-Replayer-Plugin-SWT-Test/efgs/SWTMultiWindowDynamicApp.EFG.xml");
		replayerConfig.setTestcase("/root/Sitar/GUITAR-Replayer-Plugin-SWT-Test/testcases/SWTMultiWindowDynamicApp/t_e2635454584_e2635454584.tst");
		SitarReplayer replayer = new SitarReplayer(replayerConfig);
		*/
		//new SitarRunner(replayer).run();

	
		new SitarRunner(ripper).run();
	}
}
