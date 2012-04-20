package recorder;

import java.util.concurrent.Semaphore;

import edu.umd.cs.guitar.ripper.SitarRecorder;
import edu.umd.cs.guitar.swt.launcher.SitarRipperConfiguration;
import edu.umd.cs.guitar.swt.launcher.SitarRunner;


public class test {
	public static void main(String args[])
	{
		SitarRipperConfiguration config = new SitarRipperConfiguration();
		config.setMainClass(SWTMultiWindowDynamicApp.class.getName());
		Semaphore semaphore = new Semaphore(1);
		SitarRecorder recorder = new SitarRecorder(config,semaphore);
		recorder.setExit();
		new SitarRunner(recorder).run();
		System.out.println("YOOO");
		recorder.getMonitor().cleanUp();
	}
}
