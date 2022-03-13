package server;

public class Pair {
    ServerThread client1;
    ServerThread client2;

    public Pair(ServerThread client1, ServerThread client2){
        this.client1 = client1;
        this.client2 = client2;
        client1.setPair(this);
        client2.setPair(this);
    }

    protected ServerThread getOther(ServerThread client){
        return client == client1 ? client2 : client1;
    }

    protected void sendMsg(ServerThread client, String str){
        getOther(client).out(str + "\n");
    }

    protected void disconnect(ServerThread client){
        getOther(client).removePair();
    }
}
