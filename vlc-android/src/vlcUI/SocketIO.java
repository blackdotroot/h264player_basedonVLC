package vlcUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SocketIO implements Serializable {  
	private static final int TCP_SERVER_PORT = 7000;
	private static final int TCP_Camera_PORT = 9000;
    public  void sendmsg() throws Exception {  
        Socket socket = new Socket("192.168.1.105", 7000);  
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
        PrintWriter out = new PrintWriter(socket.getOutputStream());  
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
            //String msg = reader.readLine();  
        	String msg="android client";
            out.println(msg);  
            out.flush();  
            //System.out.println(in.readLine());  
        socket.close();  
    }  
    
	public String sendmsg_f(){
        try {  
            Socket s = new Socket("192.168.1.105", TCP_SERVER_PORT);//注意host改成你服务器的hostname或IP地址  
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));  
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));  
            //send output msg  
            String outMsg = "TCP connecting to " + TCP_SERVER_PORT + System.getProperty("line.separator");   
            out.write("getdirs");//发送数据  
            out.flush();  
            Log.i("TcpClient", "sent: " + outMsg);  
            //accept server response  
            String inMsg = in.readLine() + System.getProperty("line.separator");//得到服务器返回的数据  
            Log.i("TcpClient", "received: " + inMsg);  
            //close connection  
            s.close();  
            if(! inMsg.equals("error")){
            	return inMsg;
            }
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
		return null;   
     }
	
	public String sendmsg_camer(String ip,String msg){
        try {  
            Socket s = new Socket(ip, TCP_Camera_PORT);//注意host改成你服务器的hostname或IP地址  
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));  
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));  
            //send output msg  
            String outMsg = msg + System.getProperty("line.separator");   
            out.write(msg);//发送数据  
            out.flush();  
            Log.i("TcpClient", "sent: " + outMsg);  
            //accept server response  
            String inMsg = in.readLine() + System.getProperty("line.separator");//得到服务器返回的数据  
            Log.i("TcpClient", "received: " + inMsg);  
            //close connection  
            s.close();  
            if(! inMsg.equals("error")){
            	return inMsg;
            }
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
		return null;   
     }
}  