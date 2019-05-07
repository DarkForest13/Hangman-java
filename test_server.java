package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * This class implements java Socket server
 * @author pankaj
 *
 */
public class test_server {

    private static ServerSocket server;
    private static int port = 9876;

    public static void main(String args[]) throws ClassNotFoundException {

        int useCount=0;
        Thread thread = null;
        try {
            server = new ServerSocket(port);

            while(true){
                if(useCount == 0) {
                    System.out.println("Waiting for the client request");
                    useCount++;
                }
                else System.out.println("Waiting for the other client request");
                Socket socket = server.accept();
                System.out.println("Connected");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message From user: " + message);
                Random rand = new Random();
                int newPort = rand.nextInt(9000)+1000;
                test_MultiThreadRespond mr = new test_MultiThreadRespond(newPort);
                thread = new Thread(mr);
                thread.start();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(""+newPort);


                //close resources
                ois.close();
                oos.close();
                socket.close();
            }
        } catch (IOException ex) {
            try {
                server.close();
            } catch (IOException e) {
                System.err.println("ERROR closing socket: " + e.getMessage());
            }
        }
    }

}
