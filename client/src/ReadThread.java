import javax.swing.*;
import java.net.*;
import java.io.*;


public class ReadThread extends Thread {
    DefaultListModel<String> stringModel;
    public Socket socket;
    BufferedReader bufferedReader;

    public ReadThread(Socket socket, DefaultListModel<String> stringModel){
        this.socket = socket;
        this.stringModel = stringModel;
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(true){
            try {
                String input = bufferedReader.readLine();
                stringModel.addElement(input);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
