package edu.buffalo.cse.cse486586.simplemessenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity_SimpleMessenger extends Activity {
	
	String TAG="androidPA1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity__simple_messenger);
		TelephonyManager tel =  (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		//String buil = android.os.Build.MODEL;

	    final String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        //final String realPort = String.valueOf((Integer.parseInt(portStr) * 2));
		//String id = tel.getDeviceId();
		//Log.v(TAG,"test");
		//Log.w(portStr,"portnum");
		//Log.w(number,"num");
		//Log.w(buil,"Model");
		//Log.v(number,"num");
		//Log.v(id,"id");
		final EditText editText = (EditText) findViewById(R.id.editText1);
		
		try {
			ServerSocket serverSocket = new ServerSocket(10000);//its try to open the port num of 10000
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);	//creating ServerTask
			//new ServerTask().doInBackground(serverSocket);
		} catch(Exception ex){
			
		}
		
		
		
		
		
				
		editText.setOnKeyListener(new OnKeyListener(){
		//	@Override    //do i supposed to have override here?
			public boolean onKey(View v, int keyCode, KeyEvent event){
				if((event.getAction()==KeyEvent.ACTION_DOWN)&&keyCode==KeyEvent.KEYCODE_ENTER){
				//Action_Down is same as keyPressed
				String msg =editText.getText().toString()+"\n";
				editText.setText("");
				try{
					ClientTask client = new ClientTask();
					//client.execute(msg,realPort);
					
					String realPort="";
					
					if(portStr.equals("5554"))
						realPort="11112";
					else
						if(portStr.equals("5556"))
						realPort="11108";
					
					final String realPort1=realPort;
					
					client.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg,realPort1);	//creating ClientTask with msg
					client.setPort(realPort1);
			
				}catch(Exception ex){
				}				
				return true;
				}
				return false;
			}
		});
		}


		private class ServerTask extends AsyncTask<ServerSocket, String, Void>{
	//private static class ServerTask{
		
	/*
		private static void main(String[] args) throws IOException{
		ServerSocket listener = new ServerSocket(10000);
		while(true){
		  Socket  ssocket = listener.accept();	//accepting the request if the request port num is 10000?
		//    Log.v(TAG,"Accepted!!!");
			//Log.d(ssocket,"Ssocket");
		  /*
		  try{
			BufferedReader in = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
			String msg = in.readLine();
			Log.v("androidPA1",msg);
			ssocket.close();
			
		}finally{	
			ssocket.close();
		}
		}
		*/
		@Override
		protected Void doInBackground(ServerSocket... sockets){
			// doInBackground on Server task keeps running and always be ready to receive msgs
			String msg = null;
			ServerSocket serverSocket = sockets[0]; //what is sockets[0] inside? its 100000
			Socket ssocket;
			//Log.v(TAG,"listening....");
			
			try{
			while(true){
		    ssocket = serverSocket.accept();	//accepting the request if the request port num is 10000?
		    //Log.v(TAG,"Accepted!!!");
			//Log.d(ssocket,"Ssocket");
			BufferedReader in = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
			msg = in.readLine();
			Log.v("androidPA1",msg);
			publishProgress(msg);// what is the publishPogress do? passing the msg from background to onProgressUpdate
			ssocket.close();
			}
			} catch(IOException ex){
				
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(String... string){
			Log.v("androidPA1","receving msg");
			//It runs when publishProgress receive something.
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.setText(string[0]);	//Displays text here??
			
		}
		
	}
	
private class ClientTask extends AsyncTask<String, Void, Void>{
	//Client side, I think I need to get the actual text from editText and use the PrintWriter to write the info in the socket then send the
	// socket to server

	
private String PortStr;

protected void setPort (String str) {
		PortStr = str;
		//Log.w(PortStr,"Port");
	}


@Override
	protected Void doInBackground(String... msgs){
	//I think I need to keep idling and wait for msg.
	//msgs0 represent normal msg and msgs1 represent portNumber
	//Log.w(msgs[0],"msg0");
	//Log.w(msgs[1],"msg1");

	
	
	final int portNum = Integer.parseInt(PortStr);
	
	Socket csocket=null;
	try {
		csocket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),portNum);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//I think I need to send the socket or prepare to send the socket here..
	PrintWriter out= null;
	try {
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(csocket.getOutputStream())),true);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //This is my guess but I think its wrong since I dont get any msg from edittext...

	out.print(msgs[0]);//code given I guess writing msg in the socket
	out.flush(); // code given 

	//if the key got press then we send the socket to progressUpdate 	
	//publishProgress(out);	//sending the actual msgs here
	
	try {
		csocket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	
	return null;
}


}



public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_activity__simple_messenger, menu);
		return true;
	}

}



	
	



