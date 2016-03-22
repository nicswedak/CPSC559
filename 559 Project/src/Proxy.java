import java.io.*;
import java.net.*;
import java.util.*;

public class Proxy {
    public static void main(String args[]) {
        try {
        	
        	Ping myping = new Ping();
            String host = "192.168.0.150";
            int remoteport = 9000;
            int remoteport1 = 9090;
            int remoteport2 = 9000;
            int count = 2;
            int idCount = -1;
            //This is the entrance
            int localport = 8080;
            int num = 1;
            
            //Print a starting message
           
            	myping.runIt();
            	   ServerSocket server = new ServerSocket(localport);
                
                while (true) {
                  	
                	
                    //System.out.println("Count is: " + count);
                    if(count % 2 == 0){              
                    //System.out.println("Server '1' (9090) or Server '2' (9000) - DONE ON MAIN PROXY ");
                    //Scanner in = new Scanner(System.in);
                     
                    //int num = in.nextInt();
                    if(num == 1){
                    	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)))) {
                    	    out.println("1."+idCount);

                    	}
                    	num++;
                        remoteport = remoteport1;
                        System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);
                        idCount++;
                       }
                       else{
                    	   	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)))) {
                        	    out.println("1."+idCount);
                        	    

                        	}
                    	   	num = 1;
                       	remoteport = remoteport2;
                        System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);
                        idCount++;
                       }
                    server.close();
                    server = new ServerSocket(localport);
                    }
                    

                new ThreadProxy(server.accept(), host, remoteport);
                count++;
            
            }
                
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java ProxyMultiThread " + "<host> <remoteport> <localport>");
        }
    }
}

// Handles a socket connection to the proxy server from the client and uses 2 threads to proxy between server and client
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
            
            
            
            //Connects a socket to the server
            try {
                server = new Socket(SERVER_IP, SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outputToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            
            //A new thread to manage streams from server to client
            final InputStream inputFromServer = server.getInputStream();
            final OutputStream outputToServer = server.getOutputStream();
            
            final FileOutputStream toLog = new FileOutputStream("log.txt", true);
            
  
            //A new thread for uploading to the server
            new Thread() {
                public void run() {
                    int readInBytes;
                    try {
                    	//Loop to send all incoming information from a client to a designated server
                        while ((readInBytes = inputFromClient.read(clientRequest)) != -1) {
                        	                        	
                        	//Writing user transmission to a log file
                        	   toLog.write(clientRequest,0,readInBytes);
                
                        
                        	   toLog.flush();
                        	   
                            outputToServer.write(clientRequest, 0, readInBytes);
                            outputToServer.flush();
                            
                        }
                        toLog.close();
                        
                    } catch (IOException e) {
                    	
                    } 
                    try {
                        outputToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Actually starts the thread
            .start();
            
            //The current thread manages streams from server to client
            int readInBytes;
            try {
                while ((readInBytes = inputFromServer.read(serverReply)) != -1) {
                    outputToClient.write(serverReply, 0, readInBytes);
                    outputToClient.flush();

                    //Logic to send back the query request from server to client
                    
                }
                
            } catch (IOException e) {
               // e.printStackTrace();
                
            } finally {
                try {
                    if (server != null){
                    	//System.err.println("Error 1");          
                        server.close();
                }
                    if (client != null){
                    //System.err.println("Error 2");                  
                        client.close();
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            outputToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}