package fryzjer;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.exit;

public class Client {
    private String username;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
         } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    private void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String message = scanner.nextLine();
                bufferedWriter.write(username + "|" + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void listenForMessage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                try {
                    while (socket.isConnected()) {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        });
        thread.start();
    }

    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer){
        try{
            if(reader != null){
                reader.close();
            }
            if (writer != null){
                writer.close();
            }
            if(socket != null && !socket.isConnected()){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 2137);
        Client client = new Client(socket, username);
        System.out.println("To reserve visit tipe RESERVE HOUR (instead of HOUR tipe number).\nTo Remove visit tipe REMOVE HOUR (instead of HOUR tipe number).");
        client.listenForMessage();
        client.sendMessage();
    }
}
