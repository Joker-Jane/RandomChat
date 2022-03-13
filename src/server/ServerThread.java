package server;

import java.io.*;
import java.net.Socket;

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
        reader = new BufferedReader(new InputStreamReader(input));
        out("Connection established, your id is " + getUsername());
        out("Searching for your mate, please wait");
        server.searchPair(this);
        while(!socket.isClosed()){
            loop();
        }
    }

    private void loop(){
        in();
    }

    private void in(){
        try {
            String msg = reader.readLine();
            if(msg == null){
                close();
                return;
            }
            if(pair != null){
                pair.sendMsg(this, format(msg));
                System.out.println(format(msg));
            }else{
                out("Searching for your mate, please wait");
            }
        } catch (IOException e) {
            close();
        }
    }

    protected void out(String str){
        try {
            str += "\n";
            output.write(str.getBytes());
            output.flush();
        } catch (IOException e) {
            close();
        }
    }

    private void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.disconnect(this);
        if(pair != null){
            pair.disconnect(this);
        }
    }

    public String getUsername(){
        return Integer.toString(socket.getPort());
    }

    protected void setPair(Pair pair){
        this.pair = pair;
        out("Your mate is " + pair.getOther(this).getUsername());
    }

    protected void removePair(){
        out("Your mate has disconnected, searching for your new mate");
        pair = null;
        server.searchPair(this);
    }

    private String format(String raw){
        return getUsername() + ": " + raw;
    }
}
