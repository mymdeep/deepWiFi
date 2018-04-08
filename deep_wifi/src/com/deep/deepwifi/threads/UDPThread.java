package com.deep.deepwifi.threads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.Callable;

import com.deep.deepwifi.L;

/**
 * Created by wangfei on 2018/3/21.
 */

public class UDPThread implements Callable<Integer> {
    private String target_ip = "";

    private static final String C = "deep";
    public static final short NBUDPP = 137;

    public UDPThread(String target_ip) {
        this.target_ip = target_ip;

    }


    public synchronized void run() {


    }


    @Override
    public Integer call() throws Exception {

        if (target_ip == null || target_ip.equals("")) return 1;
        DatagramSocket socket = null;
        InetAddress address = null;
        DatagramPacket packet = null;
        try {
            address = InetAddress.getByName(target_ip);
            packet = new DatagramPacket(C.getBytes(), C.getBytes().length, address, NBUDPP);
            socket = new DatagramSocket();
            socket.setSoTimeout(200);
            socket.send(packet);
            socket.close();
        } catch (SocketException se) {
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return 1;
    }
}
