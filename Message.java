package tcpandudpChat;

import java.io.Serializable;



public class Message implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    // The different types of message sent by the Client
    // REGISTER - Connect notification
    // B_MESSAGE - Broadcast message
    // P_MESSAGE - Private message
    // LOGOUT - Disconnect Notification
    static final int REGISTER = 0, B_MESSAGE = 1, P_MESSAGE = 2, LOGOUT = 3;
    private int type;
    private String message;
    private Client client;

    // constructor
    Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    Message(String message) {
        this.message = message;
    }
    
    Message(String message, Client client) {
        this.message = message;
        this.client = client;
    }

    // getters

    int getType() {
        return type;
    }

    String getMessage() {
        return message;
    }
}
