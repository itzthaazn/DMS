package tcpandudpChat;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * The Client class that can be run both as a console or a GUI
 */
public class Client {

    // for I/O
    private ObjectInputStream sInput, lInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;
    private ChatFrame cf;

    // Server, Port and Username
    public String server, username;
    private int port;

    private String[] listData;
    private String[] list = new String[30];
    private byte[] receiveData;

    /*
     *  Constructor called by console mode
     *  Server: Server address
     *  Port: Port number
     *  Username: Username
     */
    Client(String server, int port, String username) {
        // which calls the common constructor with the GUI set to null
        this(server, port, username, null);
    }

    /*
     * Constructor call when used from a GUI
     * in console mode the ClienGUI parameter is null
     */
    Client(String server, int port, String username, ChatFrame cf) {
        this.server = server;
        this.port = port;
        this.username = username;
        // Save if we are in GUI mode or not
        this.cf = cf;
    }

    /*
     * To start the dialog
     */
    public boolean start() {
        // Try to connect to the server
        try {
            socket = new Socket(server, port);
        } // if it failed not much I can so
        catch (Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // Creates the Thread to listen from the server 
        new ListenFromServer().start();
        // Send username to the server this is the only message that will
        // be sent as a String. All other messages will be Message objects
        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // If succeeded inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console or the GUI
     */
    private void display(String msg) {
        if (cf == null) {
            System.out.println(msg);      // println in console mode
        } else {
            cf.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
        }
    }

    /*
     * To send a message to the server
     */
    void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if (sInput != null) {
                sInput.close();
            }
        } catch (Exception e) {
        } // not much else I can do
        try {
            if (sOutput != null) {
                sOutput.close();
            }
        } catch (Exception e) {
        } // not much else I can do
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        } // not much else I can do

        // inform the GUI
        if (cf != null) {
            cf.connectionFailed();
        }

    }

    /*
     * To start the Client in console mode use one of the following command
     * > java Client
     * > java Client username
     * > java Client username portNumber
     * > java Client username portNumber serverAddress
     * at the console prompt
     * If the portNumber is not specified 1500 is used
     * If the serverAddress is not specified "localHost" is used
     * If the username is not specified "Anonymous" is used
     * > java Client 
     * is equivalent to
     * > java Client Anonymous 1500 localhost 
     * are eqquivalent
     * 
     * In console mode, if an error occurs the program simply stops
     * when a GUI id used, the GUI is informed of the disconnection
     */
    public static void main(String[] args) {
        // Default input values
        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonymous";

        // Depending of the number of arguments provided we fall through
        switch (args.length) {
            // > javac Client username portNumber serverAddr
            case 3:
                serverAddress = args[2];
            // > javac Client username portNumber
            case 2:
                try {
                    portNumber = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            // > javac Client username
            case 1:
                userName = args[0];
            // > java Client
            case 0:
                break;
            // invalid number of arguments
            default:
                System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
                return;
        }
        // Create the Client object
        Client client = new Client(serverAddress, portNumber, userName);
        // Test the connection to the Server
        if (!client.start()) {
            return;
        }

        // Wait for messages from user
        Scanner scan = new Scanner(System.in);
        // Loop forever for message from the user
        while (true) {
            System.out.print("> ");
            // Read message from user
            String msg = scan.nextLine();
            // Logout if message is LOGOUT
            if (msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new DisconnectMessage(client) {
                });
                // Break to do the disconnect
                break;
            } else {
                client.sendMessage(new Message(Message.B_MESSAGE, msg) {
                });
            }
        }
        client.disconnect();
    }

    // UDP client
    public String[] sendRequest(String sentence) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, socket.getInetAddress(), 9876);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String sentList = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + sentList);
        StringTokenizer tokenizer = new StringTokenizer(sentList, ",");
        Arrays.fill(list, null);
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            list[i] = tokenizer.nextToken();
            i++;
        }
        clientSocket.close();
        return list;
    }

    /*
     * A Thread class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ListenFromServer extends Thread {

        public void run() {
            while (true) {
                try {
                    String msg = (String) sInput.readObject();
                    // Console
                    if (cf == null) {
                        System.out.println(msg);
                        System.out.print("> ");
                    } else {
                        // Frame GUI
                        cf.append(msg);
                        listData = sendRequest("request");
                        cf.updateList(listData);
                    }
                } catch (IOException e) {
                    display("Server has close the connection: " + e);
                    if (cf != null) {
                        cf.connectionFailed();
                    }
                    break;
                } // can't happen with a String object but need the catch anyhow
                catch (ClassNotFoundException e2) {
                } catch (Exception ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
