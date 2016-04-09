/*
 * This Class is the primary proxy for the network
 * It is involved in sending log files, updating log files, processing requests to and from servers.
 */


import java.io.*;
import java.net.*;
import java.util.*;

public class proxy {

	/*
	 * A ping function, that attempts to connect to a given Socket
	 * If the  connection is processed it returns true, otherwise false.
	 */
	public static boolean pingHost(String host, int port, int timeout) {	    		
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), timeout);
			return true;
		} catch (IOException e) {
			return false; // Either timeout or unreachable or failed DNS lookup.
		}
	}

	/*
	 * The check function attempts to create a server on a given port
	 * If it succeeds then a given proxy is not available, otherwise 
	 * the proxy is live and we can continue with a connection
	 * **This type of check does not send any data packets**
	 */
	public static boolean check(){
		try{
			ServerSocket server = new ServerSocket(8080);
			server.close();
		}catch(Exception e){
			return true;
		}
		return false;
	}
	public static int idCount = -1;
	public static void main(String args[]) {
		try {
			/*
			 * Ping an IP to test a connection
			 * Also create variables for various ports that the servers are located on
			 */
			Ping myping = new Ping();
			String host = "192.168.0.18";
			int remoteport = 9000;
			int remoteport1 = 9090;
			int remoteport2 = 9000;
			int count = 2;
			int count2 = 2;
			int idCount2 = -1;
			/*
			 * The local port is the Outside facing port a client will connect too
			 */
			int localport = 8090;
			int num = 1;

			/*
			 * Printing ping information
			 */
			myping.runIt();
			/*
			 * Creating the outside facing TCP socket
			 */
			ServerSocket server = new ServerSocket(localport);

			/*
			 * Infinite loop in order to continuously provide a round robin load distribution amongst the servers.
			 */
			while (true) {

				//Fault Tolerance
				//If primary proxy fails,
				if(check()){

					if(count2 % 2 == 0){
						System.out.println ("Proxy is still live on 8080.");

						idCount2++;
						server.close();
						server = new ServerSocket(localport);
					}
					/*
					 * Updating the backup proxy log to match the main proxy as long as the main proxy is active
					 */
					new logFile(server.accept(), host,remoteport);
					count2++;
				}else{
					/*
					 * First if loop is used to manage thread load
					 * Second loop is used to distribute the load between X ammount in a round robin fashion
					 */
					if(count % 2 == 0){              
						if(num == 1){
							/*
							 * Writing a unique ID to file which is later appended to a given client request
							 */
							try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("id.txt")))) {
								out.println("|2."+idCount+'\n');							
							}
							/*
							 * Incrementing counters for consistency model and updating variables for new TCP connections
							 */
							num++;
							remoteport = remoteport1;
							System.out.println("Starting proxy for " + host + ":"+ remoteport + " on port " + localport);		
						} else {
							/*
							 * Writing a unique ID to file which is later appended to a given client request
							 */
							try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("id.txt")))) {
								out.println("|2."+idCount+'\n');
							}
							/*
							 * Incrementing counters for consistency model and updating variables for new TCP connections
							 */
							num = 1;
							remoteport = remoteport2;
							System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);
						}
						idCount++;
						server.close();
						server = new ServerSocket(localport);
					}
					/*
					 * Creating a new thread that handles a new TCP connection between a proxy and server
					 */
					new ThreadProxy(server.accept(), host, remoteport);
					count++;
				}
			}
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Usage: java ProxyMultiThread " + "<host> <remoteport> <localport>");
		}
	}

}

/*
 * logFile is exclusivly used to update the log on the backup proxy while the primary is alive.
 */
class logFile extends Thread {
	private Socket sClient;
	private final String SERVER_IP;
	private final int SERVER_PORT;
	logFile(Socket sClient, String ServerUrl, int ServerPort) {
		this.SERVER_IP = ServerUrl;
		this.SERVER_PORT = ServerPort;
		this.sClient = sClient;
		this.start();
	}


	public void run() {
		try {
			final byte[] clientRequest = new byte[1024];
			final InputStream inputFromClient = sClient.getInputStream();
			final OutputStream outputToClient = sClient.getOutputStream();
			Socket server = null;

			/*
			 * Creating a Primary socket to send a request to a server
			 */
	
			try {
				server = new Socket(SERVER_IP, SERVER_PORT);
			} catch (IOException e) {
				PrintWriter out = new PrintWriter(new OutputStreamWriter(outputToClient));
				out.flush();
				throw new RuntimeException(e);
			}
			/*
			 * Various output Streams to the backup proxy, log file, server and one input stream from the server
			 */
			final OutputStream outputToServer = server.getOutputStream();
			final FileOutputStream toLog = new FileOutputStream("log.txt", true);

			/*
			 * New Thread used to manage all information transfer
			 */
			new Thread() {
				public void run() {
					int readInBytes;
					try {
						/*
						 * Ensuring the log file on the main proxy and back up proxy are the same
						 */
						while ((readInBytes = inputFromClient.read(clientRequest)) != -1) {
							if(!proxy.check()){
								break;
							}
							toLog.write(clientRequest,0,readInBytes);                      
							toLog.flush();

						}
						toLog.close();
					} catch (IOException e) {} 
					try {
						outputToServer.close();
					} catch (IOException e) {}
				}
			}
			.start();

			server.close();
			outputToServer.close();
		} catch (IOException e) {}
	}
}

/*
 * ThreadProxy class handles all information transfer between a client and a server
 * Done by opening various input/output streams and creating a socket connection among everything
 */
class ThreadProxy extends Thread {
	private Socket sClient;
	private final String SERVER_IP;
	private final int SERVER_PORT;
	ThreadProxy(Socket sClient, String ServerUrl, int ServerPort) {
		this.SERVER_IP = ServerUrl;
		this.SERVER_PORT = ServerPort;
		this.sClient = sClient;
		this.start();
	}
	public void run() {
		try {
			final byte[] clientRequest = new byte[1024];
			byte[] serverReply = new byte[4096];
			final InputStream inputFromClient = sClient.getInputStream();
			final OutputStream outputToClient = sClient.getOutputStream();
			Socket client = null;
			Socket server = null;
			final InputStream inputFromFile = new FileInputStream("id.txt");

			/*
			 * Creating a Socket to the desired server, where a client request will be processed
			 */
			try {
				server = new Socket(SERVER_IP, SERVER_PORT);
			} catch (IOException e) {
				PrintWriter out = new PrintWriter(new OutputStreamWriter(outputToClient));
				out.flush();
				throw new RuntimeException(e);
			}
			/*
			 * Various output Streams to the backup proxy, log file, server and one input stream from the server
			 */
			final InputStream inputFromServer = server.getInputStream();
			final OutputStream outputToServer = server.getOutputStream();
			final FileOutputStream toLog = new FileOutputStream("log.txt", true);


			/*
			 * New Thread used to manage all information transfer
			 */
			new Thread() {
				public void run() {
					/*
					if(proxymain.logCounter == 2){
						String line = "LOG@";					
						try{
							BufferedReader br = new BufferedReader(new FileReader("log.txt"));
							String holder = "";
							while( (holder = br.readLine()) != null){
								holder.trim();
								if(!holder.equals("")){
									line = line + holder;

									if((holder = br.readLine()) == null){
										break;
									}
									holder.trim();
									line = line + ";";
									line = line + holder;
									line = line + "#";
									//System.out.println("LINE " + line);
								}
							}
							System.out.println("STRING: " + line);;
							br.close();*/
					/*
							byte[] logToSend = line.getBytes(Charset.forName("UTF-8"));
							int readInBytes2;
							for(int i = 0; logToSend[i] != -1;i++){
								readInBytes2 = logToSend[i];
								proxyToServer.write(logToSend,0,readInBytes2);
								proxyToServer.flush();
							}
					 */			/*
							PrintStream pw = new PrintStream(new FileOutputStream("trash.txt"));
								pw.println(line);
								pw.flush();
								pw.close();

						InputStream inputFromTrash = new FileInputStream("trash.txt");
						int readInBytes2;
						while((readInBytes2 = inputFromTrash.read(logRequest)) != -1){

								outputToServer.write(logRequest,0,readInBytes2);
								outputToServer.flush();
						}


						}catch(Exception e){
							e.printStackTrace();
						}													
					}
					  */


					/*
					 *Creating a singular input stream in order to append a UNIQUE ID to a client request 
					 */
					List<InputStream> streams = Arrays.asList(
							inputFromClient,
							inputFromFile)
							;
					InputStream story = new SequenceInputStream(Collections.enumeration(streams));


					int readInBytes;
					try {
						/*
						 * Loop used to store a input stream into a byte array
						 * Then send all information to the log file, and server
						 */
						while ((readInBytes = story.read(clientRequest)) != -1) {

							toLog.write(clientRequest,0,readInBytes);                      
							toLog.flush();


							outputToServer.write(clientRequest, 0, readInBytes);
							outputToServer.flush();

						}
						story.close();
						toLog.close();
					} catch (IOException e) {} 
					try {
						outputToServer.close();
					} catch (IOException e) {}
				}
			}
			.start();
			/*
			 * Transferring the response from the server directly to the client
			 */
			int readInBytes;
			try {
				while ((readInBytes = inputFromServer.read(serverReply)) != -1) {
					
					outputToClient.write(serverReply, 0, readInBytes);
					outputToClient.flush();

				}

			} catch (IOException e) {} 
			finally {
				try {
					if (server != null){        
						server.close();
					}
					if (client != null){               
						client.close();
					}
				} catch (Exception e) {}
			}
			sClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}