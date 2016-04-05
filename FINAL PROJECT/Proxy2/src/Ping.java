/*
 * Ping sweeper is  used to test a IP address and return a result
 * That includes, time required to send/receive data packets.
 * Success rate for sending a item
 * And the given IP/Port combo that it attempted to try
 */
import java.io.*;
import java.util.*;

public class Ping{
	public Ping() throws IOException{
	}
	
	public void runIt() throws IOException{
		Ping PING = new Ping();
		List<String> com = new ArrayList<String>();
		com.add("ping");
		com.add("-4");
		com.add("-n");
		com.add("1");
		com.add("192.168.0.150");
		
		String i = null;
		String Error = "";
		
		ProcessBuilder PB = new ProcessBuilder(com);
		Process PROCESS = PB.start();
		
		BufferedReader results = new BufferedReader(new InputStreamReader(PROCESS.getInputStream()));
		System.out.println("Result: \n");
		
		while((i = results.readLine()) != null){
			System.out.println(i);
		}
	}
	
}

