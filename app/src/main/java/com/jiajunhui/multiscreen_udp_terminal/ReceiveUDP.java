package com.jiajunhui.multiscreen_udp_terminal;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.jiajunhui.multiscreen_udp_terminal.bean.BasePackage;
import com.jiajunhui.multiscreen_udp_terminal.utils.GsonTools;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by Taurus on 2016/11/10.
 */

public class ReceiveUDP {

    public static final int PORT = 9999;
    private static DatagramSocket listenSocket;
    private static DatagramSocket responseSocket;

    private static InetAddress deviceInetAddress;
    private static boolean hasConnected;
    private static DatagramSocket sendSocket;

    public static void listenUDP(OnUDPListener OnUDPListener){
        try{
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            listenSocket = new DatagramSocket(PORT);
            System.out.println("Server started, Listen port: " + PORT);
            while (true) {
                listenSocket.receive(packet);
                //receive data
                String receiveData = new String(packet.getData(), 0, packet.getLength());
                deviceInetAddress = packet.getAddress();
                BasePackage basePackage = getPackage(receiveData);
                if(basePackage!=null && EventContants.EVENT_CODE_DEVICE_AUTH == basePackage.getEventCode()){
                    responseSendUDP(getAuthStr());
                    if(OnUDPListener!=null){
                        //call back to UI
                        OnUDPListener.onReceive(basePackage,packet.getSocketAddress());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static BasePackage getPackage(String content){
        return GsonTools.changeGsonToBean(content,BasePackage.class);
    }

    private static String getAuthStr(){
        BasePackage basePackage = new BasePackage();
        basePackage.setEventCode(EventContants.EVENT_CODE_DEVICE_AUTH);
        basePackage.setDeviceInfo(Build.MODEL);
        basePackage.setEventMessage("AUTH");
        return GsonTools.createGsonString(basePackage);
    }

    public static void responseSendUDP(String outMessage){
        try{
            if(deviceInetAddress==null)
                return;
            // Send packet thread
            byte[] buf;
            int packetPort = 8888;
            if (TextUtils.isEmpty(outMessage))
                return;
            buf = outMessage.getBytes();
            Log.d("UDP_Send","Send " + outMessage + " to " + deviceInetAddress);
            // Send packet to hostAddress:9999, server that listen
            // 9999 would reply this packet
            DatagramPacket out = new DatagramPacket(buf,
                    buf.length, deviceInetAddress,packetPort);
            responseSocket = new DatagramSocket(null);
            responseSocket.send(out);
            responseSocket.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("UDP_Send","---Exception---");
        }
    }

    public static void sendUDP(String outMessage){
        try{
            if(deviceInetAddress==null)
                return;
            // Send packet thread
            byte[] buf;
            int packetPort = 8888;
            String address = new String(deviceInetAddress.getHostAddress());
            SocketAddress inetAddress = new InetSocketAddress(address,packetPort);
            if (TextUtils.isEmpty(outMessage))
                return;
            buf = outMessage.getBytes();
            Log.d("UDP_Send","Send " + outMessage + " to " + inetAddress);
            // Send packet to hostAddress:9999, server that listen
            // 9999 would reply this packet
            DatagramPacket out = new DatagramPacket(buf,
                    buf.length, inetAddress);
            sendSocket = new DatagramSocket(null);
            sendSocket.setReuseAddress(true);
            sendSocket.send(out);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("UDP_Send","---Exception---");
        }
    }

    public static void stopListener(){
        hasConnected = false;
        if(listenSocket !=null){
            listenSocket.close();
        }
        if(responseSocket!=null){
            responseSocket.close();
        }
        if(sendSocket!=null){
            sendSocket.close();
        }
    }

    public interface OnUDPListener{
        void onReceive(BasePackage basePackage, SocketAddress socketAddress);
    }
}
