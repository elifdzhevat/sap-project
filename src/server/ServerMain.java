package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            ExecutorService pool = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected.");
                pool.execute(new ServerWorker(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
}
