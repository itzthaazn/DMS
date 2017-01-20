package tcpandudpChat;

public class RegisterMessage extends Message {

    public RegisterMessage(Client client) {
        super(Message.REGISTER, "");
    }

}
