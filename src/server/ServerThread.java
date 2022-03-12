package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerThread extends Thread{
    private Server server;
    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private BufferedReader reader;
    private Pair pair;

    public ServerThread(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        output = socket.getOutputStream();
        input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        out("Connection established\n");
        server.addToQueue(this);
        while(!socket.isClosed()){
            loop();
        }
    }

    private void loop() throws IOException {
        in();
    }

    private void in() throws IOException {
        String msg = reader.readLine();
        if(msg == null){
            close();
            return;
        }
        if(pair != null){
            pair.sendMsg(this, format(msg));
            System.out.println(format(msg));
        }else{
            out("Searching for your mate, please wait\n");
        }
    }

    protected void out(String str){
        try {
            output.write(str.getBytes());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        socket.close();
        server.disconnect(this);
    }

    public String getUsername(){
        return Integer.toString(socket.getPort());
    }

    protected void setPair(Pair pair){
        this.pair = pair;
        out("Your mate is " + pair.getOther(this).getUsername() + "\n");
    }

    private String format(String raw){
        return getUsername() + ": " + raw;
    }
}
