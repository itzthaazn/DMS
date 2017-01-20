package tcpandudpChat;

public class DisconnectMessage extends Message {

    public DisconnectMessage(Client client) {
        super(Message.LOGOUT, "");
    }
}
