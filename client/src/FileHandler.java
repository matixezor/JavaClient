import javax.swing.*;
import java.net.*;
import java.io.*;


public class FileHandler{
    DefaultListModel<String> fileModel;
    public Socket socket;
    BufferedReader bufferedReader;

    public FileHandler(Socket socket, DefaultListModel<String> fileModel){
        this.socket = socket;
        this.fileModel = fileModel;
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readFiles(){
        while(true){
            try {
                String input = bufferedReader.readLine();
                if(input.equals("done")) {
                    return;
                }
                fileModel.addElement(input);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void readFile (String fileName) throws Exception{
        try{
            FileWriter writer = new FileWriter("/home/students/s452622/IdeaProjects/client/download/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            while(true){
                String input = bufferedReader.readLine();
                if (input.equals("error"))
                    throw new Exception("Server error");
                if (input.equals("done")) {
                    bufferedWriter.close();
                    writer.close();
                    return;
                }
                bufferedWriter.write(input + "\n");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
