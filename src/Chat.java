import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

/**
 * Created by Christoffer Grännby
 * Date: 2020-11-05
 * Time: 10:07
 * Project: Övningsuppgift 3
 * Copyright: MIT
 */
public class Chat extends JFrame implements ActionListener {
    String name;
    InetAddress inetAddress;
    int port;
    MulticastSocket socket = new MulticastSocket(port);
    JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);
    JTextField write = new JTextField();
    JButton close = new JButton("Close chat");
    InetSocketAddress sockGroup;
    NetworkInterface netIf = NetworkInterface.getByName("wlan4");

    public Chat (String user, String group, int portNumber) throws IOException{
        name = user;
        inetAddress = InetAddress.getByName(group);
        port = portNumber;

        sockGroup = new InetSocketAddress(inetAddress, port);
        socket.joinGroup(sockGroup, netIf);
        new Reciever(socket, textArea);
        sendMessage("Connected");
        setTitle("Chat " + name);
        textArea.setEditable(false);
        add(close, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(write, BorderLayout.SOUTH);
        close.addActionListener(this);
        write.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void sendMessage (String message) {
        byte[] data = (name + ": " + message).getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
        try {
            socket.send(packet);
            }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == write){
            sendMessage(write.getText());
            write.setText("");
       }
        else if (e.getSource() == close){
            sendMessage("Disconnected");
            try {
                socket.leaveGroup(sockGroup, netIf);
            }
            catch (IOException ie){
                ie.printStackTrace();
            }
            socket.close();
            dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException {
        String name = "Christoffer";
        if (args.length > 0)
            name = args[0];
        new Chat(name, "234.235.236.237", 12540);
    }
}
