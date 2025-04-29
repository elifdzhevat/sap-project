package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {
    private static Socket socket;
    private static BufferedReader reader;
    private static PrintWriter writer;
    private static String currentUsername;

    public static void connect() throws IOException {
        socket = new Socket("localhost", 5000); // Свързваме се със сървъра
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void sendMessage(String message) {
        writer.println(message);
    }

    public static String readMessage() throws IOException {
        return reader.readLine();
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void main(String[] args) {
        try {
            connect();
            new LoginFrame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
