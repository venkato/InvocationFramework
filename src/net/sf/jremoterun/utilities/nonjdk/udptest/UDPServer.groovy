package net.sf.jremoterun.utilities.nonjdk.udptest

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class UDPServer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void startClient(String host, int port, String sentence) {
//        BufferedReader inFromUser =
//                new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
//        clientSocket.setBroadcast(true);
        InetAddress ipAddress = InetAddress.getByName(host);
        byte[] sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        clientSocket.send(sendPacket);
        clientSocket.close();
        log.info("packet sent")
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        log.info("FROM SERVER: ${modifiedSentence}");
        clientSocket.close();
    }

    static void startServer(int port) {
        log.info "waiting connection .."
        DatagramSocket serverSocket = new DatagramSocket(port);
//        serverSocket.setBroadcast(true)
        byte[] receiveData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            log.info "RECEIVED: ${sentence}"
            InetAddress ipReplyAddress = receivePacket.getAddress();
            int port2 = receivePacket.getPort();
            log.info "reply address : ${ipReplyAddress}, reply port = ${port2}"
            String capitalizedSentence = "reply on : ${sentence.toUpperCase()}";
            byte[] sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, ipReplyAddress, port2);
            serverSocket.send(sendPacket);
        }
    }



    static void startServerMulticust(int port, String adderess) {
        MulticastSocket socket = new MulticastSocket(port);
        log.info "waiting connection .."
        InetAddress ipAddress = InetAddress.getByName(adderess);
        socket.joinGroup(ipAddress)
//        serverSocket.setBroadcast(true)
        byte[] receiveData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            log.info "RECEIVED: ${sentence}"
            InetAddress ipReplyAddress = receivePacket.getAddress();
            int port2 = receivePacket.getPort();
            log.info "reply address : ${ipReplyAddress}, reply port = ${port2}"
        }
    }

}
