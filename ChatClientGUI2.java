import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientGUI2 {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private Socket socket;
    private PrintWriter out;
    private String username;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatClientGUI2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public ChatClientGUI2() throws IOException {
        // Get the username from the user
        username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty. Exiting.");
            System.exit(0);
        }

        frame = new JFrame("Chat Client - " + username);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        startClient();
    }

    private void startClient() throws IOException {
        String serverAddress = "localhost"; // Change this to the server's IP address
        int portNumber = 12345; // Change this to the server's port

        socket = new Socket(serverAddress, portNumber);
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send the username to the server
        out.println(username);

        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while (true) {
                    String serverMessage = in.readLine();
                    if (serverMessage == null) {
                        break;
                    }
                    textArea.append(serverMessage + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = textField.getText();
        textField.setText("");

        // Send the message to the server
        if (out != null) {
            out.println(message);
        }
    }
}
