package fryzjer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientHandler extends Thread {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    Schedule schedule;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private final int  ERROR_OK = 0;

    public ClientHandler(Socket socket, Schedule schedule) {
        try {
            this.socket = socket;
            this.schedule = schedule;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        try {
            sendFreeVisits(this);
            while (socket.isConnected()) {
                String messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);
                String[] message = messageFromClient.split("\\|");
                String[] information = message[1].split(" ");
                if (information[0].equals("RESERVE")) {
                    try{
                        if(Integer.parseInt(information[1])>17 || Integer.parseInt(information[1])<10){
                            sendResponse(this,"Our working hours are 10am-6pm");
                            continue;
                        }
                        int result = schedule.setUsernameForVisit(message[0], Integer.parseInt(information[1]));
                        if(result==ERROR_OK){
                            sendResponse(this,"Visit reserved");
                            broadcastVisits();
                        }else {
                            sendResponse(this,"Visit is already booked! Please choose another one");
                        }
                    } catch (NumberFormatException e){
                        sendResponse(this,"Invalid input");
                    }
                }
                else if (information[0].equals("REMOVE")) {
                    try{
                        if(Integer.parseInt(information[1])>17 || Integer.parseInt(information[1])<10){
                            sendResponse(this,"Our working hours are 10am-6pm");
                            continue;
                        }
                        int result = schedule.cancelVisit(message[0], Integer.parseInt(information[1]));
                        if(result==ERROR_OK){
                            sendResponse(this,"Visit Canceled");
                            broadcastVisits();
                        }else {
                            sendResponse(this,"It's not your visit");
                        }
                    } catch (NumberFormatException e){
                        sendResponse(this,"Invalid input");
                    }
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendFreeVisits(ClientHandler clientHandler) throws IOException {
        List<Visit> freeVisits = schedule.getFreeVisits();
        StringBuilder response = new StringBuilder();
        for (Visit visit : freeVisits) {
            response.append(visit.getHour()).append(":00\n");
        }

        sendResponse(clientHandler,response.toString());
    }

    public void broadcastVisits()  {
        for(ClientHandler clientHandler : clientHandlers){
            try {
                sendFreeVisits(clientHandler);
            } catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void sendResponse(ClientHandler clientHandler,String response) throws IOException {
        clientHandler.bufferedWriter.write(response);
        clientHandler.bufferedWriter.newLine();
        clientHandler.bufferedWriter.flush();
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        System.err.println(clientUsername + " opuścił server");
        clientHandlers.remove(this);
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null && !socket.isConnected()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
