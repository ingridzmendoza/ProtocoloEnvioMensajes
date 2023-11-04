package msp;

import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

public class UserManager {

    public static final String CLASS_NAME = UserManager.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    //para poder mandarlo llamar en connection handler
    public ArrayList<String> getConnectedUsers() {
        return connectedUsers;
    }


    private HashMap<String, Socket> connections;

    public UserManager() {
        super();
        connections = new  HashMap<String, Socket>();
    }

    //arraylist auxiliar para los usuarios conectados
    ArrayList<String> connectedUsers = new ArrayList<>();


    //connect method
    public boolean connect(String user, Socket socket) {
        boolean result = true;

        Socket s = connections.put(user, socket);
        if (s != null) {
            result = false;
        } else {
            //agregar usuarios conectados
            connectedUsers.add(user);
        }
        return result;
    }


    //disconnect method
    public boolean disconnected(String user) {
        boolean result = false;

        Socket s = connections.remove(user);
        if (s != null) {
            result = true;
            try {
                //cerrar el socket
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public Socket get(String user) {

        Socket s = connections.get(user);

        return s;
    }

    public void send(String message) {

        Collection<Socket> conexiones = connections.values();

        for( Socket s: conexiones) {
            try {
                PrintWriter output = new PrintWriter(s.getOutputStream(), true);
                output.println(message);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
        }

    }


}
