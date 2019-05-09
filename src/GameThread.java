package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadRespond implements Runnable{

    private ServerSocket server;
    private int port;

    public MultiThreadRespond(int port){
        this.port = port;
        try{
            server = new ServerSocket(port);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        int HP = 7;
        ObjectOutputStream objectOutput;
        ObjectInputStream objectInput;

        String moviePool[] = {"Martian" , "Moonlight" , "Greenbook" , "Lalaland" , "Avatar" , "Roma" , "Dunkirk" , "Arrival" , "Spectre" , "LifeOfPi" , "Inception" , "Coco" , "Terminator" , "Kickass"};
        int rand = (int) (Math.random() * moviePool.length);
        String word = moviePool[rand].toLowerCase();

        char hiddenWord[] = new char[word.length()];
        char missedWord[] = new char[7];
        int missedCount = 0;
        int hiddenLeft = 1;
        int isWin = 0;
        int isLose = 0;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                hiddenWord[i] = ' ';
            } else {
                hiddenWord[i] = '*';
            }
        }

        try{
            Socket socket = server.accept();
            objectInput = new ObjectInputStream(socket.getInputStream());
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                String action = (String) objectInput.readObject();
                if (hiddenLeft == 0) {
                    isWin = 1;
                }
                if (missedCount == HP){
                    isLose = 1;
                }
                if (action.equalsIgnoreCase("status")) {
                    System.out.println("[Thread] Command Received : " + action);
                    String output = new String(hiddenWord) + "#" + new String(missedWord) + "#" + missedCount + "#" + isWin + "#" + isLose;
                    System.out.println(output);
                    objectOutput.writeObject(output);
                }
                else if (action.length() == 1) {
                    System.out.println("[Thread] Guess Received : " + action);
                    char userGuess = action.charAt(0);

                    boolean letterFound = false;
                    for (int i = 0; i < word.length(); i++) {
                        if (userGuess == word.charAt(i)) {
                            hiddenWord[i] = word.charAt(i);
                            letterFound = true;
                        }
                    }
                    if (!letterFound) {
                        missedWord[missedCount] = userGuess;
                        missedCount++;

                    }

                    hiddenLeft = word.length();
                    for (int i = 0; i < word.length(); i++) {
                        if ('*' != hiddenWord[i])
                            hiddenLeft--;
                    }
                }
                else if (action.equalsIgnoreCase("answer")) {
                    System.out.println("[Thread] Command Received : " + action);
                    objectOutput.writeObject(word);
                    System.out.println(word);
                }
                else if (action.equalsIgnoreCase("exit")){
                    System.out.println("[Thread] Command Received : " + action);
                    objectInput.close();
                    objectOutput.close();
                    socket.close();
                    break;
                }
            }
        } catch(IOException | ClassNotFoundException err){
            err.printStackTrace();
        }
    }
}
