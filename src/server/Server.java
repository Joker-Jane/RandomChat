package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Server{
    private ServerSocket serverSocket;
    private Deque<ServerThread> deque = new LinkedList<>();
    private List<ServerThread> clients = new ArrayList<>();

    public Server(){
        init();
    }

    private void init(){
        try {
            serverSocket = new ServerSocket(1111);
            System.out.println("Server connected");
            while (true){
                connect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws IOException {
        Socket socket = serverSocket.accept();
        ServerThread client = establishConnection(socket);
    }

    private ServerThread establishConnection(Socket socket){
        ServerThread client = new ServerThread(this, socket);
        System.out.println("A connection has established: " + client.getUsername());
        clients.add(client);
        client.start();
        return client;
    }

    protected void addToQueue(ServerThread client){
        deque.push(client);
        if(deque.size() >= 2){
            makePair(deque.poll(), deque.poll());
        }
    }

    protected void makePair(ServerThread client1, ServerThread client2){
        Pair pair = new Pair(client1, client2);
        System.out.println("A pair has been made: " + client1.getUsername() + " " + client2.getUsername());
    }

    protected void disconnect(ServerThread client){
        System.out.println("A connection has lost: " + client.getUsername());
        clients.remove(client);
    }

    protected void distributeMsg(ServerThread client, String msg){
        for (ServerThread thread : clients){
            if(thread != client){
                thread.out(msg);
            }
        }
    }

    private void close() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args){
        Server server = new Server();
    }
}
