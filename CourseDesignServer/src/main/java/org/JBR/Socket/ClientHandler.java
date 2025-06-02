package org.JBR.Socket;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            //* Receive the Client request */
            Object request = in.readObject();
            
            //* Process the request */
            Object response = processRequest(request);
            
            //* Send the response back to the Client */
            out.writeObject(response);
            out.flush();
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Object processRequest(Object request) {
   
        return "Processed: " + request.toString();
    }
}