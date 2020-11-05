import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 * Created by Christoffer Grännby
 * Date: 2020-11-05
 * Time: 10:07
 * Project: Övningsuppgift 3
 * Copyright: MIT
 */
public class Reciever implements Runnable {
    final Thread activity = new Thread(this);
    MulticastSocket socket;
    JTextArea textArea;

    Reciever(MulticastSocket sock, JTextArea text) {
        socket = sock;
        textArea = text;
        activity.start();
    }

    public void run() {
        byte[] data = new byte[1024];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String message = new String(data, 0, packet.getLength());
                synchronized (activity) {
                    textArea.append(message + "\n");
                }
            }catch (SocketException s){
                break;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

