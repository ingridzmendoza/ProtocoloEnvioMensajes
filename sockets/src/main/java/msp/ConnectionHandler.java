package msp;

import msp.UserManager;
import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

    public static final String CLASS_NAME = ConnectionHandler.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);


    private UserManager users;
    private Socket clientSocket = null;

    private BufferedReader input;
    private PrintWriter output;


    public ConnectionHandler(UserManager u, Socket s) {
        users = u;
        clientSocket = s;

        try {
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String buffer = null;
        while (true) {
            try {
                buffer = input.readLine();
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
            String command = buffer.trim();
            // CONNECT
            if (command.startsWith("CONNECT")) {
                String userName = command.substring(command.indexOf(' ')).trim();
                System.out.println(userName);

                boolean isConnected = users.connect(userName, clientSocket);
                if (isConnected) {
                    output.println("OK");
                } else {
                    output.println("FAIL");
                }

            }
            if (command.startsWith("DISCONNECT")) {
                String userName = command.substring(command.indexOf(' ')).trim();
                System.out.println(userName);

                boolean isDisconnected = users.disconnected(userName);
                if (isDisconnected) {
                    output.println("User " + userName + " disconnected.");
                } else {
                    output.println("User " + userName + " not connected.");
                }
            }
            //imprime los usuarios conectados
            if (command.equals("LIST")) {
                ArrayList<String> connectedUsers = users.getConnectedUsers();
                for (String user : connectedUsers) {
                    output.println(user);
                }
            }


                // SEND #<mensaje>@<usuario>
                if (command.startsWith("SEND")) {
                    String message = command.substring(command.indexOf('#') + 1,
                            command.indexOf('@'));

                    //validar tamanio de mensaje
                    if (message.length() <= 140) {
                        System.out.println(message);
                        String userName = command.substring(command.indexOf('@') + 1).trim();
                        System.out.println(userName);
                        users.send(message);
                    }
                    //recortar mensje solo primeros 140 caracteres
                    else {
                        System.out.println("Mensaje superior a 140, este se ha recortado.");
                        message = message.substring(0, 140);
                        users.send(message);
                    }
                }

            }


        }
    }

