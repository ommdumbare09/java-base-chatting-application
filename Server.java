import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable {
    private final int port;
    private final ChatServerGUI gui;
    private final Map<String, ServerHandler> clients;

    public Server(int port, ChatServerGUI gui) {
        this.port = port;
        this.gui = gui;
        this.clients = new HashMap<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            InetAddress serverAddress = InetAddress.getLocalHost();
            gui.appendMessage("Server is running on " + serverAddress.getHostAddress() + ":" + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    gui.appendMessage("Client connected: " + clientSocket.getInetAddress());

                    ServerHandler handler = new ServerHandler(clientSocket, gui, this);
                    new Thread(handler).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClient(String username, ServerHandler handler) {
        clients.put(username, handler);
    }

    public void removeClient(String username) {
        clients.remove(username);
    }

    public void broadcastMessage(String sender, String message) {
        for (ServerHandler handler : clients.values()) {
            handler.sendMessage(sender + ": " + message);
        }
    }

    public static void main(String[] args) throws IOException {
        int portNumber = 12345; 
        ChatServerGUI gui = new ChatServerGUI();
        Server server = new Server(portNumber, gui);
        new Thread(server).start();
    }
}
