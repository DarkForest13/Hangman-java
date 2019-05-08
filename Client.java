package src;

import java.net.ConnectException;
import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    private static String hiddenWord = "";
    private static String missedWord = "";
    private static int missedCount = 0;
    private static int isWin = 0;
    private static int isLose = 0;
    private static int HP = 7;
    private String man[] = {     "|=====\n" +
            "|    O\n" +
            "|   /|\\\n" +
            "|   /\\",

            "|=====\n" +
                    "|    O\n" +
                    "|   /|\\\n" +
                    "|   /\n",

            "|=====\n" +
                    "|    O\n" +
                    "|   /|\\\n" +
                    "|   \n",

            "|=====\n" +
                    "|    O\n" +
                    "|   /|\n" +
                    "|   \n",

            "|=====\n" +
                    "|    O\n" +
                    "|   /\n" +
                    "|   \n",

            "|=====\n" +
                    "|    O\n" +
                    "|   \n" +
                    "|   \n",

            "|=====\n" +
                    "|    \n" +
                    "|   \n" +
                    "|   \n",

            "|\n" +
                    "|    \n" +
                    "|   \n" +
                    "|   \n",
    };


    private static boolean isContinue = true;

    private static Scanner sc = new Scanner (System.in);

    public static void main(String[] args){
        Client gameClient = new Client();
        gameClient.run();
    }

    private void run() {
        int timeout = 0;
        while (isContinue) {
            try {
                //get the localhost IP address
                InetAddress localhost = InetAddress.getLocalHost();

                //init socket with localhost and port
                int port = 3742;
                //String ip = "172.17.0.2"; //docker ip
                Socket socket = new Socket(localhost, port);

                //sending request new port
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Client request new port");

                //get new port
                ois = new ObjectInputStream(socket.getInputStream());
                String newPort = (String) ois.readObject();
                System.out.println("new Port: " + newPort);

                //switch to new port
                socket = new Socket(localhost, Integer.parseInt(newPort));
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                System.out.println("Client - Hangman game is starting...");
                System.out.println("Connecting to "+ localhost + " Port : " + newPort);

                System.out.print("\nDo you want to play? (y/n) : ");
                isContinue = toContinue();
                if (isContinue) {
                    System.out.println("\n ============================= \n");
                    while (true) {
                        cls();              //to clear the entire screen
                        getStatus();
                        System.out.println(man[HP-missedCount]);
                        if (isWin == 1 || isLose == 1) break;
                        System.out.println("You have " + (HP - missedCount) + " chance left \nThere is "+hiddenWord.length() + " alphabet ");
                        System.out.println("Word : " + hiddenWord);
                        System.out.println("Missed : " + missedWord);
                        sendObject(inputGuess());
                        System.out.println("\n =============================\n");
                    }//the game end

                    if (isWin == 1){
                        System.out.println( "You WIN!!\n" );
                        System.out.println( "The word is " + hiddenWord );
                    }
                    else {
                        String answer = getAnswer();
                        System.out.println("You DIED!!\n");
                        System.out.println("The word is " + answer);
                    }
                }
                System.out.println("\nDisconnecting ...\n");
                oos.writeObject("EXIT");
                ois.close();
                oos.close();
            }
            catch(Exception e){
                timeout++;
                System.out.println("Connection failed : reconnecting "+(11-timeout));
                if(timeout == 10) {
                    System.out.println("Connection timeout");
                    System.exit(1);
                }
            }
        }
    }

    private static void getStatus(){
        try {
            oos.writeObject("STATUS");
            String input = (String) ois.readObject();
            String []detail = input.split("#");
            hiddenWord = detail[0];
            missedWord = detail[1];
            missedCount = Integer.parseInt(detail[2]);
            isWin = Integer.parseInt(detail[3]);
            isLose = Integer.parseInt(detail[4]);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getAnswer(){
        try {
            oos.writeObject("ANSWER");
            return (String) ois.readObject();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "";
    }

    private boolean toContinue() {
        String userInput;
        while (true) {
            try {
                userInput = sc.nextLine();
            } catch (Exception ex) {
                break;
            }
            userInput = userInput.trim().toLowerCase();
            if (userInput.equalsIgnoreCase("y")) {
                return true;
            } else if (userInput.equalsIgnoreCase("n")) {
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }
        return false;
    }

    private String inputGuess () {
        String guess;
        while (true) {
            System.out.print("Guess : ");
            guess = sc.nextLine().trim().toLowerCase();
            if (guess.length() > 1) {
                System.out.println("Invalid input.");
            }
            else if (guess.equalsIgnoreCase("")) {
                System.out.println("Invalid input.");
            }
            else break;
        }
        return guess;
    }

    private void sendObject(String request){
        try {
            oos.writeObject(request);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private void cls(){
        System.out.println("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n ");
    }
}
