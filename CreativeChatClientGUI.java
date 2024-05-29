import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CreativeChatClientGUI {
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
                new CreativeChatClientGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public CreativeChatClientGUI() throws IOException {
        getUsername();

        frame = new JFrame("Chat Client - " + username);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(new Color(34, 40, 49));
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(new Color(45, 52, 61));
        textField.setForeground(new Color(255, 255, 255));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(78, 205, 196));
        sendButton.setForeground(new Color(34, 40, 49));
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBackground(new Color(34, 40, 49));

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
        frame.getContentPane().setBackground(new Color(34, 40, 49));

        frame.setVisible(true);

        startClient();
    }

    private void getUsername() {
        while (true) {
            username = JOptionPane.showInputDialog("Enter your username:");
            if (username == null) {
                System.exit(0);
            }
            if (!username.trim().isEmpty()) {
                break;
            }
            JOptionPane.showMessageDialog(null, "Username cannot be empty.");
        }
    }

    private void startClient() throws IOException {
        String serverAddress = "10.5.76.250"; 
        int portNumber = 12345; 

        socket = new Socket(serverAddress, portNumber);
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println(username);

        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while (true) {
                    String serverMessage = in.readLine();
                    if (serverMessage == null) {
                        break;
                    }
                    SwingUtilities.invokeLater(() -> textArea.append(serverMessage + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.trim().isEmpty()) {
            textField.setText("");
            if (out != null) {
                out.println(message);
            }
        }
    }
}
