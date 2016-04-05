import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.*;

/*
This class is used to process incoming client requests.
Its used by Server.java to minimize to possibility of
clients getting rejected as they attempt to connect to the server.
 */


public class processReq implements Runnable{

	public static boolean check(){
		try{
			ServerSocket server = new ServerSocket(8070);
			server.close();
		}catch(Exception e){
			return true;
		}
		return false;
	}

	protected Socket clientSocket = null;

	/*
	 * Name of the server
	 */
	protected String serverText   = null;

	public processReq(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText   = serverText;
	}
	
	public void run() {
		try {
			/*
			 * Opening output stream from the client Socket
			 * Also creating a time stamp
			 */
			OutputStream output = clientSocket.getOutputStream();
			long time = System.currentTimeMillis();
			Date date=new Date(time);
			String query = "";

			/*
			 * Reading the message that the client (or proxy) has sent to the server
			 * Saving it into a string
			 */
			InputStream is = clientSocket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String clientIn = br.readLine();
			/*
			 * Checking if the input is null, if it is dont do anything
			 * Otherwise go inside the if loop
			 */
			if(clientIn != null){          
				//System.out.println("Message received from client is "+ clientIn);
				/*
				 * Trim and parse the client message to further diagnose what needs to be done
				 */
				clientIn.trim();
				String parse = "@";
				String []dec = clientIn.split(parse);

				/*
				 * If the proxy has passed a log, then update the server with all IDS that are missing
				 */
				if(dec[0].equals("LOG")){
					/*
					 * To Update the server:
					 * Parse all requests 
					 * If the request has a set then compare its ID to all IDS on the server
					 * If the ID DOES NOT Exist then add it do the Database
					 * If the ID DOES exist, then check the next request in the list of itmes
					 */
					String parse2 = "#";
					String parse3 = ";";
					String []requests = dec[1].split(parse2);
					boolean cond = false;

					for(int i=0; i < requests.length;i++){	
						String []parseRequest = requests[i].split(parse3);

						if(parseRequest[0].equals("SET")){

							try{
								FileReader fromText = new FileReader("database.txt");
								BufferedReader fromFR = new BufferedReader(fromText);
								String test = "";
								while((test  = fromFR.readLine()) != null || cond == true){
									test.trim();
									String []tokens2 = test.split(parse3);
									if(parseRequest[7].equals(tokens2[7])){
										cond = true;
									}
								}				
								fromText.close();
								fromFR.close();							 

							}catch(Exception e){}
							if(!cond){
								try(PrintStream pw = new PrintStream(new FileOutputStream("database.txt",true))){
									pw.println(requests[i]);
									pw.flush();
									pw.close();}
							}
							cond = false;
						}
					}
					/*
					 * Otherwise check if we got a SET/GET/DEL
					 */
				}else{
					clientIn.trim();
					String delims = ";";
					String []tokens = clientIn.split(delims);
					/*
					 * If the request has a set, then just add the entire msg to the database
					 */
					if(tokens[0].equals("SET")){
						try(PrintStream pw = new PrintStream(new FileOutputStream("database.txt",true))){
							pw.print(clientIn);
							pw.flush();
							pw.close();
						}
						query = "Successfully added the prof#";

						/*
						 * If the request has a get, then search the entire database for matching
						 * first and last name, then return the string to the proxy
						 */
					}else if(tokens[0].equals("GET")){

						String test = "";

						try{
							FileReader fromText = new FileReader("database.txt");
							BufferedReader fromFR = new BufferedReader(fromText);

							while((test  = fromFR.readLine()) != null){
								test.trim();
								String []tokens2 = test.split(delims);
								if(tokens[1].equals(tokens2[1])){
									if(tokens[2].equals(tokens2[2])){
										query = query + test + "#"; 
									}
								}
							}
							fromText.close();
							fromFR.close();

						}catch(Exception e){}
						/*
						 * IF the request is a delete, then search the database for the first and last name
						 * Then remove all instance from the database that share that first and last name
						 */
					}else if(tokens[0].equals("DEL")){
						/*System.out.println("WE ARE HERE");
						String test = "";
						String newFile = "";
						String delims2 = ";";
						boolean found = false;
						try{
							File inputFile = new File("database.txt");
							File tempFile = new File("tempFile.txt");
							
							FileReader fromText = new FileReader(inputFile);
							BufferedReader fromFR = new BufferedReader(fromText);
							BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
							
							while((test  = fromFR.readLine()) != null){
								test.trim();
								String []tokens3 = test.split(delims2);
								if(tokens[1].equals(tokens3[1])){
									if(tokens[2].equals(tokens3[2]))continue;
							}
								newFile = newFile + test;
							
							}
			
							try(PrintStream pw = new PrintStream(new FileOutputStream("database.txt",true))){
								pw.print(clientIn);
								pw.flush();
								pw.close();
							}
							
							
							writer.close();			
							fromText.close();
							fromFR.close();

							

								query = "Professor Removed";	
									}catch(Exception e){}
								*/
						query = "For future itterations#";
												/*
												 * Add the unqiue ID to the database	
												 */
					}else{
						try(PrintStream pw = new PrintStream(new FileOutputStream("database.txt",true))){
							pw.println(";" + tokens[0]);
							pw.flush();
							pw.close();
						}
					}
				}
			
			/*
			 * Setting up all required output streams to convey the response back to the proxy and client
			 */
			OutputStream os = clientSocket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			query = query + "*";
			bw.write(query);
			System.out.println("Message sent to the client is "+ query);
			bw.flush();
			
			/*
			 * Close all streams
			 */
			output.close();
			is.close();
			os.close();
			osw.close();
			bw.close();
			}
			isr.close();
			br.close();
			/*
			 * Display the time stamp for when the request was processed,
			 * Also what IP created the request
			 */
			System.out.println("Request processed: " + date);
			System.out.println("FROM: " + clientSocket.getRemoteSocketAddress().toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}