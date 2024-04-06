package fryzjer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    ServerSocket serverSocket;
    public Schedule schedule = new Schedule();
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Połączenie z nowym klientem");
                ClientHandler clientHandler = new ClientHandler(socket,schedule);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas akceptowania połączenia: " + e.getMessage());
        }
    }

    public void closeServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args)  {
        try {
            ServerSocket serverSocket = new ServerSocket(2137);
            Server server = new Server(serverSocket);
            server.start();
        } catch (Exception e){
            System.out.println("Server down");
        }
    }
}