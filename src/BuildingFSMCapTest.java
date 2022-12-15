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
import building.*;
import myfileio.MyFileIO;

@TestMethodOrder(OrderAnnotation.class)
class BuildingFSMCapTest {
	private ElevatorSimController c;
	private Building b;
	private MyFileIO fio = new MyFileIO();
	private static boolean DEBUG = false;
	private static String os = null;
	private static String javaHome = null;
	private ElevatorLogCompare cmpLog = new ElevatorLogCompare();

	private void updateSimConfigCSV(String fname) {
		File fh = fio.getFileHandle("ElevatorSimConfig.csv");
		String line = "";
		ArrayList<String> fileData = new ArrayList<>();
		try {
			BufferedReader br = fio.openBufferedReader(fh);
			while ( (line = br.readLine())!=null) {
				if (line.matches("passCSV.*")) 
					fileData.add("passCSV,"+fname);
				else
					fileData.add(line);
			}
			fio.closeFile(br);
			BufferedWriter bw = fio.openBufferedWriter(fh);
			for (String l : fileData)
				bw.write(l+"\n");
			fio.closeFile(bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyTestFile(String fname) {
		File ifh = fio.getFileHandle("test_data/"+fname);
		File ofh = fio.getFileHandle(fname);
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		try {
			Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		updateSimConfigCSV(fname);
	}
	
	private void deleteTestCSV(String fname) {
		MyFileIO fio = new MyFileIO();
		File ifh = fio.getFileHandle(fname);
		ifh.delete();
		ifh = fio.getFileHandle(fname.replaceAll(".csv", "PassData.csv"));
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
//				moveLogCmpFiles(fh.getName().replaceAll(".cmp", ""));
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

    private void moveLogCmpFiles(String base) {
    	File ifh = new File(base+".log");
    	File ofh = new File("JUnitTestLogs/"+base+".log");
		Path src = Paths.get(ifh.getPath());
		Path dest = Paths.get(ofh.getPath());
		try {
			Files.move(src,dest,StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    	ifh = new File(base+".cmp");
    	ofh = new File("JUnitTestLogs/"+base+".cmp");
		src = Paths.get(ifh.getPath());
		dest = Paths.get(ofh.getPath());
		try {
			Files.move(src,dest,StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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
		ifh = new File("JUnitTestLogs");
		if (!ifh.exists()) {
			ifh.mkdir();
			System.out.println("Created file: JUnitTestLogs");
		}
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

	private void runFSMTest (String test,String cmd) {
		deleteTestCSV(test+".csv");
		assertTrue(cmpLog.executeCompare(cmd.split("\\s+")));
		moveLogCmpFiles(test);
	}
	
	@Test
	@Order(1)
	//@Disabled
	void testCapTest1() {
		String test = "CapTest1";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 109;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}

	@Test
	@Order(2)
	//@Disabled
	void testCapTest2() {
		String test = "CapTest2";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 129;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}

	@Test
	@Order(3)
	//@Disabled
	void testCapTest3() {
		String test = "CapTest3";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 134;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}

	@Test
	@Order(4)
	//@Disabled
	void testCapTest4() {
		String test = "CapTest4";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 154;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}

	@Test
	@Order(5)
	//@Disabled
	void testCapTest5() {
		String test = "CapTest5";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 67;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}

	@Test
	@Order(6)
	//@Disabled
	void testCapTest6() {
		String test = "CapTest6";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 178;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}

	@Test
	@Order(7)
	//@Disabled
	void testCapTest7() {
		String test = "CapTest7";
		System.out.println("\n\nExecuting Test: "+test+".csv");
		copyTestFile(test+".csv");
		c = new ElevatorSimController(null);
		b = c.getBuilding();
		b.enableLogging();
	    int i;
		for (i = 0; i < 184;i++) c.stepSim();
		b.closeLogs(i);
		String cmd = "java -jar ./cmpElevator.jar "+test+".log";
	    runFSMTest(test,cmd);
	}
}
