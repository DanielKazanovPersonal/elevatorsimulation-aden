import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import myfileio.MyFileIO;
import building.*;

@TestMethodOrder(OrderAnnotation.class)
class ExecFullElevatorTests {
	private ElevatorSimController c;
	private Building b;
	private MyFileIO fio = new MyFileIO();
	private static boolean DEBUG = false;
	private static String os = null;
	private static String javaHome = null;
	private ElevatorLogCompare cmpLog = new ElevatorLogCompare();

	private void setupSimConfigCSV(String fname) {
		File ofh = new File("ElevatorSimConfig.csv");
		File ifh = new File(fname);
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		try {
			Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void renameLogFile(String fname) {
		File ofh = new File(fname);
		File ifh = new File("FullElevatorTest.log");
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		try {
			Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ifh.delete();
	}

	private void renamePassDataFile(String fname) {
		File ofh = new File(fname);
		File ifh = new File("FullElevatorTestPassData.csv");
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		try {
			Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ifh.delete();
	}
	
//	private boolean processCmpElevatorOutput(Process proc, ArrayList<String> results) {
//		String line = "";
//		boolean pass = true;
//		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//		try {
//			while ((line = br.readLine())!=null) {
//				results.add(line);
//				System.out.println(line);
//				if (line.contains("FAILED")) pass = false;
//			}
//			br.close();		
//		} catch (IOException e) {
//			e.printStackTrace();			
//		}
//		return pass;
//	}
//	
//	private void printManualCmpElevatorInstructions(File fh) {
//		System.out.println("ERROR: cmpElevator failed to run - you will need to run manually.");
//		System.out.println("       1) cd to your project directory in the terminal.");
//		System.out.println("       2) java -jar cmpElevator.jar "+fh.getName().replaceAll(".cmp", ".log"));	
//	}
//	
//	private boolean processCmpElevatorError(Process proc, ArrayList<String> results, File fh) {
//		String line = "";
//		boolean pass = true;
//		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//		try {
//			while ((line = br.readLine())!=null) {
//				results.add(line);
//				System.out.println(line);
//				pass = false;
//			}
//			br.close();		
//			printManualCmpElevatorInstructions(fh);
//		} catch (IOException e) {
//			e.printStackTrace();			
//		}
//		return pass;
//	}
//	
//	private boolean executeCmpElevator(File fh,String cmd) {
//		boolean pass = true;
//		ArrayList<String> cmpResults = new ArrayList<String>();
//		if (javaHome == null) {
//			printManualCmpElevatorInstructions(fh);
//			fail();
//		}
//		cmd = javaHome+"/"+cmd;
//		String[] execCmpElevator = cmd.split("\\s+");
//		try {
//			Process proc = new ProcessBuilder(execCmpElevator).start();
//			proc.waitFor();
//			pass = pass && processCmpElevatorOutput(proc,cmpResults);
//			if (cmpResults.isEmpty()) 
//				pass = pass && processCmpElevatorError(proc,cmpResults,fh);
//			
//			if (!cmpResults.isEmpty()) {
//				BufferedWriter bw = fio.openBufferedWriter(fh);
//				for (int i = 0; i < cmpResults.size() ; i++) {
//					bw.write(cmpResults.get(i)+"\n");
//				}
//				bw.close();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	    return(pass);	
//	}

	
    private static String getOperatingSystem() {
    	os = System.getProperty("os.name");
    	return os;
    }

    private static void getJavaHome() {
    	File fh = null;
    	javaHome = System.getProperty("java.home").replaceAll("jre","bin");
		if (DEBUG) System.out.println("JavaHome: "+javaHome);
		fh = new File(javaHome);
		if (!fh.exists()) 
			javaHome = null;
    }

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Running on: "+getOperatingSystem());
		getJavaHome();
		File ifh = new File("ElevatorSimConfig.csv");
		File ofh = new File("ElevatorSimConfig.save");
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		File ifh = new File("ElevatorSimConfig.save");
		File ofh = new File("ElevatorSimConfig.csv");
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
		ifh.delete();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	@Order(1)
	//@Disabled
	void testElevatorTestCfg1() {
		String test = "FullElevatorTest";
		String config = "ElevatorSimConfig1.csv";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		setupSimConfigCSV(config);
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 13397;i++) c.stepSim();
		b.closeLogs(i);
		renameLogFile(test+"Cfg1.log");
		renamePassDataFile(test+"Cfg1PassData.csv");
		assertTrue(true);		
	}

	@Test
	@Order(2)
	//@Disabled
	void testElevatorTestCfg2() {
		String test = "FullElevatorTest";
		String config = "ElevatorSimConfig2.csv";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		setupSimConfigCSV(config);
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 13429;i++) c.stepSim();
		b.closeLogs(i);
		renameLogFile(test+"Cfg2.log");
		renamePassDataFile(test+"Cfg2PassData.csv");
		assertTrue(true);
	}

	@Test
	@Order(3)
	//@Disabled
	void testElevatorTestCfg3() {
		String test = "FullElevatorTest";
		String config = "ElevatorSimConfig3.csv";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		setupSimConfigCSV(config);
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 13371;i++) c.stepSim();
		b.closeLogs(i);
		renameLogFile(test+"Cfg3.log");
		renamePassDataFile(test+"Cfg3PassData.csv");
		assertTrue(true);
	}

}
