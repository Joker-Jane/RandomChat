package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader br;
    private Scanner scanner;

    public Client(){
        try {
            init();
            read();
            write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        socket = new Socket("localhost", 1111);
        writer = new PrintWriter(socket.getOutputStream(), true);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        scanner = new Scanner(System.in);
    }

    private void read() throws IOException {
        new Thread(() -> {
            try {
                while(true){
                    System.out.println(br.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void write(){
        new Thread(() -> {
            while(true){
                String msg = scanner.nextLine();
                writer.println(msg);
            }
        }).start();
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
