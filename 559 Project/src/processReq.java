import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/*
This class is used to process incoming client requests.
Its used by Server.java to minimize to possibility of
clients getting rejected as they attempt to connect to the server.
 */


public class processReq implements Runnable{

    protected Socket clientSocket = null;
    
    //Name of the server
    protected String serverText   = null;

    public processReq(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }


    public void run() {
        try {
        	//Gather a input and output streams
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();
            Date date=new Date(time);
            //Write a message to the client,
            output.write(("Processing Request: " + this.serverText + " On:" + date).getBytes());
            //Closing both input and output streams
            output.close();
            input.close();
            //Server side, displaying that the request has finshed and what date/time
            System.out.println("Request processed: " + date);
            System.out.println("FROM: " + clientSocket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}