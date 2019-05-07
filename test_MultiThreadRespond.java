package src;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class test_MultiThreadRespond implements Runnable{
    
    private ServerSocket server;
    private int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    String poll[] = {"eiei","coconut","orange","pink","background"};
    String word;
    String secret = "";
    int health = 6;
    String status = "";

    public test_MultiThreadRespond(int port){
        this.port = port;
        try{
           server = new ServerSocket(port);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while(true){
            try{
                Socket socket = server.accept();
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                while(true){
                    String action = (String)ois.readObject();
                    if(secret.equalsIgnoreCase(word)){
                        status = "win";
                    }
                    if(health == 0){
                        status = "lose";
                    }
                    if(action.length() == 1){
                        char ans[] = action.toCharArray();
                        if(checkans(ans[0]).equalsIgnoreCase("") ){
                        }
                    }
                    if (action.equals("ANSWER")) {
                        System.out.println("[Server] Command Received : " + action);
                        oos.writeObject(word);
                        System.out.println(word);
                    }
                    if(action.equalsIgnoreCase("exit")) {
                        System.out.println("[Server] Command Received : " + action);
                        ois.close();
                        oos.close();
                        socket.close();
                        break;
                    }
                }

                //terminate the server if client sends exit request

            }catch(Exception e){

            }

        }
    }


    //game logic
    String checkans(char guess){
        String correction;
        for(int i = 0 ; i < word.length(); i++){
            if(guess == (word.charAt(i))){
                String tmp = secret.substring(0, i)+guess+secret.substring(i+1);
                secret = tmp;

                return "correct";
            }
        }
        health--;
        return "incorrect";

    }

    boolean isWin(){
        if(this.secret.equals(this.word)){
            return true;
        }
        else return false;
    }

    public String getAns(){
        return word;
    }

    public String getSecret(){
        return secret;
    }

    public int getHP(){
        return health;
    }

    public void currentHP (){
    }

}
