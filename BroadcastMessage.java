package tcpandudpChat;


public class BroadcastMessage extends Message {

    public BroadcastMessage(String message) {
        super(Message.B_MESSAGE, message);
    }

}
