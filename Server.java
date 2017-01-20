package tcpandudpChat;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;


/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
    // Unique ID for each connection

    private static int uniqueId;
    // ArrayList to keep the list of the Client
    private static ArrayList<ClientThread> al;
    private ChatFrame cf;
    private SimpleDateFormat sdf;
    private int port, UDPort;
    private boolean keepGoing;
    private ServerSocket serverSocket;
    private DatagramSocket responseSocket;

    // UDP
    public static final String HOST_NAME = "localhost";
    public static final int HOST_PORT = 8889; // host port number
    int counter;

    /*
     *  Server constructor that receive the port to listen to for connection as parameter
     *  in console
     */
    public Server(int port) {
        this(port, null);
    }

    public Server(int port, ChatFrame cf) {
        // GUI or not
        this.cf = cf;
        // the port
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        al = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
        /* Create socket server and wait for connection requests */
        try {
            // the socket used by the server
            serverSocket = new ServerSocket(port);
            responseSocket = new DatagramSocket(9876);
            // infinite loop to wait for connections
            while (keepGoing) {
                // format message saying we are waiting
                display("Server waiting for Clients on port " + port + ".");
                // Accept connection
                Socket socket = serverSocket.accept();

                // if asked to stop
                if (!keepGoing) {
                    break;
                }
                ClientThread t = new ClientThread(socket);  // make a thread of it
                al.add(t);								// save it in the ArrayList
                t.start();
            }
            // When asked to stop
            try {
                serverSocket.close();
                responseSocket.close();
                for (int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    /*
     * For the GUI to stop the server
     */
    protected void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        } catch (Exception e) {
        }
    }

    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if (cf == null) {
            System.out.println(time);
        } else {
        }
    }

    /*
     *  to broadcast a message to all Clients
     */
    private synchronized void broadcast(String message) {
        // Add HH:mm:ss and \n to the message
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        // Display message on console or GUI
        if (cf == null) {
            System.out.print(messageLf);
        } else {
        }
        // Loop in reverse order in case of removing a Client
        // when disconnected
        for (int i = al.size(); --i >= 0;) {
            ClientThread ct = al.get(i);
            // try to write to the Client if it fails remove it from the list
            if (!ct.writeMsg(messageLf)) {
                al.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    // Allow client to logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for (int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // found it
            if (ct.id == id) {
                al.remove(i);
                return;
            }
        }
    }

    /*
     *  To run as a console application just open a console window and: 
     * > java Server
     * > java Server portNumber
     * If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        // start server on port 1500 unless a PortNumber is specified 
        int portNumber = 1500;
        switch (args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;
        }
        // Create a server object and start it
        Server server = new Server(portNumber);
        for (int i = 0; i < al.size(); ++i) {
          server.start();
        }
    }

    // UDP Server
    public void receiveResponse() throws Exception {
        // Scan all the users connected
        String list = "";
        ClientThread ct;
        for (int i = 0; i < al.size(); ++i) {
            ct = al.get(i);
            System.out.println((i + 1) + ") " + ct.username + " since " + ct.date);
            list += ct.username + ",";
        }
        System.out.println(list);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        responseSocket.receive(receivePacket);
        InetAddress IPAdd = receivePacket.getAddress();
        int udpPort = receivePacket.getPort();
        sendData = list.getBytes();
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length, IPAdd, udpPort);
        responseSocket.send(sendPacket);
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {
        // the socket where to listen/talk

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // my unique id (easier for deconnection)
        int id;
        // the Username of the Client
        String username;
        // the only type of message a will receive
        Message cm;
        // the date I connect
        String date;

        // Constructore
        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            /* Creating both Data Stream */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                // read the username
                username = (String) sInput.readObject();
                display(username + " just connected.");
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } // have to catch ClassNotFoundException
            // but I read a String, I am sure it will work
            catch (ClassNotFoundException e) {
            }
            date = new Date().toString() + "\n";
        }

        // what will run forever
        public void run() {
            for (int i = 0; i < al.size(); ++i) {
               execute();
            }
        }
        public void execute() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while (keepGoing) {
                // read a String (which is an object)
                try {
                    cm = (Message) sInput.readObject();
                } catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                // the messaage part of the Message
                String message = cm.getMessage();
                String time = sdf.format(new Date());
        

                // Switch on the type of message receive
                switch (cm.getType()) {
                    case Message.REGISTER:
                        display(username + " connected with a REGISTER message.");
                        broadcast(username + " just connected.");
                        break;
                    case Message.P_MESSAGE:
                        privateMsg(1, time + " " + username + ": " + message + "\n");
                        break;
                    case Message.B_MESSAGE:
                        broadcastMsg(time + " " + username + ": " + message + "\n");
                        break;
                    case Message.LOGOUT:
                        display(username + " disconnected with a LOGOUT message.");
                        broadcast(username + " just disconnected.");
                        keepGoing = false;
                        break;
                }
                try {
                    for(int i = 0; i< counter; i++){
                    receiveResponse();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(counter);
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
            try {
                if (responseSocket != null) {
                    responseSocket.close();
                }
            } catch (Exception e) {
            }
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(String msg) {
            // If Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // Write the message to the stream
            try {
                sOutput.writeObject(msg);
                counter = 1*al.size();
            } // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }

        // Private Message and Broadcast Message
        public synchronized void privateMsg(int index, String message) throws IndexOutOfBoundsException {
            al.get(index).writeMsg(message);
        }

        public synchronized void broadcastMsg(String message) {
            for (ClientThread client : al) {
                client.writeMsg(message);
            }
        }
    }
}
