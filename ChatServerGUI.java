import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChatServerGUI {
    private JFrame frame;
    private JTextArea textArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatServerGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public ChatServerGUI() throws IOException {
        frame = new JFrame("Chat Server");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);

        frame.setVisible(true);

        startServer();
    }

    private void startServer() throws IOException {
        int portNumber = 12345;
        Server server = new Server(portNumber, this);
        new Thread(server).start();
    }

    public void appendMessage(String message) {
        textArea.append(message + "\n");
    }
}
