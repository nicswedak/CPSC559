import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
            
             
                //Server is running always. This is done using this while(true) loop
                  //Reading the message from the client
                    InputStream is = clientSocket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String number = br.readLine();
                    if(number != null){          
                    System.out.println("Message received from client is "+number);
     
                    //Multiplying the number by 2 and forming the return message
                    String returnMessage;     
                        //Input was not a number. Sending proper message back to client.
                        returnMessage = "IT WORKS\n";
                 

                    	//Sending the response back to the client.
                    	OutputStream os = clientSocket.getOutputStream();
                    	OutputStreamWriter osw = new OutputStreamWriter(os);
                    	BufferedWriter bw = new BufferedWriter(osw);
                    	bw.write(returnMessage);
                    	System.out.println("Message sent to the client is "+returnMessage);
                    	bw.flush();
                    
                
                        
            //Write a message to the client,
            output.write(("Processing Request: " + this.serverText + " On:" + date).getBytes());
            //Closing both input and output streams           
            output.close();
            input.close();
            //Server side, displaying that the request has finshed and what date/time
            System.out.println("Request processed: " + date);
            System.out.println("FROM: " + clientSocket.getRemoteSocketAddress().toString());
                    }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}