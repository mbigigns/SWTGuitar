package recorder;

import edu.umd.cs.guitar.ripper.SitarRecorder;
import edu.umd.cs.guitar.ripper.SitarRipperConfiguration;
import edu.umd.cs.guitar.ripper.SitarRunner;


public class test {
	public static void main(String args[])
	{
		SitarRipperConfiguration config = new SitarRipperConfiguration();
		config.setMainClass(SWTMultiWindowDynamicApp.class.getName());
		SitarRecorder recorder = new SitarRecorder(config);
		new SitarRunner(recorder).run();
	}
}
