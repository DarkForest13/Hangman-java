package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {

    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 3742;

    public static void main(String args[]) throws ClassNotFoundException {
        //create the socket server object

        Thread thread = null;
        try {
            server = new ServerSocket(port);

            while(true){
                System.out.println("[Server] Waiting for the client request");
                Socket socket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("[Server] Server acppected : " + message);
                Random rand = new Random();
                int newPort = rand.nextInt(9000)+1000;
                MultiThreadRespond mr = new MultiThreadRespond(newPort);
                thread = new Thread(mr);
                thread.start();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(""+newPort);

                ois.close();
                oos.close();
                socket.close();

            }
        } catch (Exception ex) {
            try {
                server.close();
            } catch (Exception e) {
                System.err.println("ERROR closing socket: " + e.getMessage());
            }
        }
    }

}