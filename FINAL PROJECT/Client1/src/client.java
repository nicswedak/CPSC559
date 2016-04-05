/*
 * A Client class that opens TCP sockets to a known proxy
 * In the case that the main proxy is down, the client routes requests to the backup proxy
 * The client then sends the given user input through the socket
 * and waits on a response
 * Once a response is obtained, the proxy will parse the response and output specfic information to the user
 */

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Scanner;
import java.net.Socket;


public class client {

	private static Socket socket;	

	/*
	 * A ping function, that attempts to connect to a given Socket
	 * If the  connection is processed it returns true, otherwise false.
	 */
	public static boolean pingHost(String host, int port, int timeout) {	    		
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), timeout);
			return true;
		} catch (IOException e) {
			return false; 
			// Either timeout or unreachable or failed DNS lookup
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
			return false;
		}
		return true;
	}


	/*
	 * The running function handles all required processes to open sockets
	 * Send data packets and process retrieved data packets.
	 */
	public static void running(String str) throws IOException {


		String serverHostname = new String ("192.168.0.150");
		System.out.println ("Attemping to connect to host " + serverHostname + " on port 8080.");

		int port = 8080;
		int portBackup = 8090;
		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;


		/*
		 * Checking the state of the main proxy
		 * If it is dead route information to the back up proxy
		 * otherwise continue on the main proxy
		 */
		if(check()){
			port = portBackup;
			System.out.println ("Connection to 8080 failed.");
			System.out.println ("Attemping to connect to host " + serverHostname + " on port 8090.");
		}
		/*
		 *  Create a TCP connection with the client and proxy
		 */	
		socket = new Socket(serverHostname, port);
		/*
		 * Open necessary output streams to send client information to a given proxy
		 */		

		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		/*
		 *Writing the user client request to a string and sending entire string to the proxy
		 */
		String sendMessage = str + "\n";
		bw.write(sendMessage);
		bw.flush();
		System.out.println("Message sent to the server : "+sendMessage);

		/*
		 * Open necessary input streams using a given socket to process all responses from the proxy       
		 */
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String message = br.readLine();

		/*
		 *Trim user input, and parse the returned string in order to process the return message 
		 */
		message.trim();
		String delims1 = "#";
		String delims2 = ";";
		String []tokens = message.split(delims1);

		System.out.println("Message received from the server is:" + message );
		if(tokens[0].equals("Successfully added the prof")){

			System.out.println(tokens[0]);

		}else if(tokens[0].equals("For future itterations")){
			System.out.println(tokens[0]);
		}
		else if(message.equals("*")){
			System.out.println("The professor does not exist in the database");
		}else{

			for(int i = 0;!tokens[i].equals("*");i++){
				String []allMsgs = tokens[i].split(delims2);
				if(i == 0){
					System.out.println("Professor Name: "+ allMsgs[1] + "," + allMsgs[2] + '\n');
				}

				System.out.println("\tClass: " + allMsgs[4] + allMsgs[5] + " Rating: " + allMsgs[6]);
				System.out.println("\tComment: " + allMsgs[3] + '\n');
			}
		}


		/*
		 * Stub of code used to identify a IP address 
		 */
		InetAddress address = InetAddress.getByName(serverHostname);
		echoSocket = new Socket(address, port);
		out = new PrintWriter(echoSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		out.flush();



		/*       
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
            //e.printStackTrace();
            System.exit(1);
        }

		 */     	
		/*
		 * 		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		 * 
		 * 			
			DBConnect connection = new DBConnect();
	   		SQLQueries queries = new SQLQueries();
			boolean loggedOut = false;
			do  {

				System.out.println("What would you like to do?");
				System.out.println("1: for a list of all users.");
				System.out.println("2: to search by a professors last name.");
				System.out.println("3: to add a new professor");
				System.out.println("4: to remove an existing professor");
				System.out.println("5: to add a rating for an existing professor");
				System.out.println("Logout to stop the program.");

				String input = userInput.nextLine();
				if(input.equals("1")){

					try {
						queries.viewUsers(connection.SQLConnection(), "ratemyprof");
					} catch (SQLException e) {
						e.printStackTrace();
					}

					System.out.println("WORKED");
				} else if (input.equals("2")) {



					try {
						System.out.println("Enter the last name of the professor you wish to search");
						String myInput = userInput.nextLine();
						queries.viewRatings(connection.SQLConnection(), "ratemyprof", myInput);
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else if (input.equals("3")) {

					try {
						System.out.println("Enter the professors first name");
						String newFName = userInput.nextLine();
						System.out.println("Enter the professors last name");
						String newLName = userInput.nextLine();
						System.out.println("Enter the professors school");
						String newSchool = userInput.nextLine();

						queries.addProf(connection.SQLConnection(), "ratemyprof", newFName, newLName, newSchool);
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else if (input.equals("4")) {

					try {
						System.out.println("Enter the professors first name");
						String oldFName = userInput.nextLine();
						System.out.println("Enter the professors last name");
						String oldLName = userInput.nextLine();
						queries.removeProf(connection.SQLConnection(), "ratemyprof", oldFName, oldLName);

					} catch (SQLException e) {
						//Want to print this instead of the actual error
						System.out.println("Cannot remove that prof as they have valid ratings");
					}
				} else if (input.equals("5")) {

					try {

						System.out.println("Enter the professors first name");
						String oldFName = userInput.nextLine();
						System.out.println("Enter the professors last name");
						String oldLName = userInput.nextLine();
						System.out.println("Enter any comments you have for the professor");
						String comments = userInput.nextLine();
						System.out.println("Enter the 4 letter class abbrevation for the class the professor taught");
						String className = userInput.nextLine();
						System.out.println("Enter the class number for the class the professors taught");
						int classNumber = userInput.nextInt();
						System.out.println("Enter the professors rating");
						int rating = userInput.nextInt();
						userInput.nextLine();

						queries.addRating(connection.SQLConnection(), "ratemyprof", oldFName, oldLName, rating, className, classNumber, comments);

					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else if (input.equals("Logout")) {
					loggedOut = true;
				}
			} while(!loggedOut);
		stdIn.close();
		 */
		/*
		 * Closing any open streams
		 */
		out.close();
		in.close();
		echoSocket.close();
	}
}