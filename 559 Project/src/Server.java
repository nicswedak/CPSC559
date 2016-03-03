
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server implements Runnable{
    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    
    //Creating a server with a port initialized
    //Constructor
    public Server(int port){
        this.serverPort = port;
    }

//Function that runs the server and error handling
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        //Attempting to open the port
        openSocket();
        //Continue to do things until the server is stopped
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server has been disconnected.") ;
                    return;
                }
                throw new RuntimeException(
                    "Issues connecting to the server", e);
            }
            new Thread(
                new processReq(
                    clientSocket, "OUR TEST SERVER")
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }
    
    //Checking if the server is stopped or not
    private synchronized boolean isStopped() {
        return this.isStopped;
    }
    
    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Server Error - Disconnecting", e);
        }
    }
    
    //Attempting to open the port
    //This is declared at the top of the program
    private void openSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port: " + serverPort, e);
        }
    }

}