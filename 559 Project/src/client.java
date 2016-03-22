import java.io.*;
import java.net.*;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;
import java.net.Socket;


public class client {
	
	private static Socket socket;	
	
	public static boolean pingHost(String host, int port, int timeout) {	    		
		try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // Either timeout or unreachable or failed DNS lookup.
	    }
	}
	
    public static void main(String[] args) throws IOException {

        String serverHostname = new String ("192.168.0.150");

        System.out.println ("Attemping to connect to host " + serverHostname + " on port 8080.");
        
        int port = 8080;
        int portBackup = 8090;
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        try {
        	//Fault Tolerance
        	//If primary proxy fails, switch to the backup
        	if(!pingHost(serverHostname,port,1000)){
        		port = portBackup;
        		 System.out.println ("Connection to 8080 failed.");
        		 System.out.println ("Attemping to connect to host " + serverHostname + " on port 8090.");
        	}
      
        	socket = new Socket(serverHostname, port);
        	
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            Scanner userInput = new Scanner(System.in);
            
            System.out.println("Whatchu want");
            String str = userInput.nextLine();

            String sendMessage = str + "\n";
            bw.write(sendMessage);
            bw.flush();
            System.out.println("Message sent to the server : "+sendMessage);
        	
            
            //Get the return message from the server            
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            System.out.println("Message received from the server : " +message);
            
                    
        	InetAddress address = InetAddress.getByName(serverHostname);
            echoSocket = new Socket(address, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        	
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
            System.exit(1);
        }


        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
	
/*
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
/
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

*/

	out.close();
	in.close();
	stdIn.close();
	echoSocket.close();
    }
}