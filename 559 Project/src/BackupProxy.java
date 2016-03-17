import java.io.*;
import java.net.*;
import java.util.*;

public class BackupProxy {
    public static void main(String[] args) {
        try {
            
            String host = "192.168.0.150";
            int remoteport;
            int remoteport1 = 9090;
            int remoteport2 = 9000;
            
            //This is the entrance
            int localport = 8090;
            
            
            //Print a starting message
   
            
            while (true) {
            	
                System.out.println("Server '1' on proxy2 or Server '2' on proxy 2 ");
                Scanner in = new Scanner(System.in);

                int num = in.nextInt();
                if(num == 1){
                    remoteport = remoteport1;
                   }
                   else{
                   	remoteport = remoteport2;
                   }
                System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);
                ServerSocket server = new ServerSocket(localport);
                new ThreadProxy(server.accept(), host, remoteport);
                server.close();
            }
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java ProxyMultiThread " + "<host> <remoteport> <localport>");
        }
    }
}

// Handles a socket connection to the proxy server from the client and uses 2 threads to proxy between server and client
class BackupThreadProxy extends Thread {
    private Socket sClient;
    private final String SERVER_IP;
    private final int SERVER_PORT;
    BackupThreadProxy(Socket sClient, String ServerUrl, int ServerPort) {
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
            
            //A new thread for uploading to the server
            new Thread() {
                public void run() {
                    int readInBytes;
                    try {
                        while ((readInBytes = inputFromClient.read(clientRequest)) != -1) {
                            outputToServer.write(clientRequest, 0, readInBytes);
                            outputToServer.flush();

                            //Logic to send queries to the server
                            
                        }
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
                e.printStackTrace();
                
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
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