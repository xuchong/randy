package com.randy.xc.handle;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class settingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        String message=intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_setting);

        //broadcast
        new Thread(sendRun).start();

        new Thread(getRun).start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh(MenuItem menu)
    {
        new Thread(sendRun).start();
    }

    Runnable sendRun = new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            sendUDP();
        }
    };


    static String destAddressStr = "255.255.255.255";
    static int destPortInt = 9998;
    static int TTLTime = 4;

    ///broadcast
    private void sendUDP()
    {
        MulticastSocket multiSocket=null;
        try {

            InetAddress destAddress = InetAddress.getByName(destAddressStr);

            //if (!destAddress.isMulticastAddress()) {//检测该地址是否是多播地址

            //throw new Exception("地址不是多播地址");

            //}

            int destPort = destPortInt;

            int TTL = TTLTime;

            multiSocket = new MulticastSocket();

            //multiSocket.setTimeToLive(TTL);

            byte[] sendMSG = "udpsend".getBytes();

            DatagramPacket dp = new DatagramPacket(sendMSG, sendMSG.length, destAddress, destPort);

            multiSocket.send(dp);

            multiSocket.close();
        }catch (Exception e)
        {
            if(multiSocket!=null)
                multiSocket.close();
        }
    }
    Runnable getRun = new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            getUDPptp();
        }
    };
    private void getUDPptp()
    {
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[100];
        try {
            // 建立Socket连接
            DatagramSocket datagramSocket = new DatagramSocket(destPortInt);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (true) {
                    // 准备接收数据
                    datagramSocket.receive(datagramPacket);
                    String strMsg=new String(datagramPacket.getData()).trim();
                    if(strMsg!=null&&strMsg.equals("udpptp"))
                    {
                        TableLayout tableLayout=(TableLayout)findViewById(R.id.display_message);
                        TableRow tableRow=new TableRow(this);

                        EditText editText=new EditText(this.getApplicationContext());
                        editText.setText(datagramSocket.getInetAddress().getHostAddress());
                        TableRow.LayoutParams params = new TableRow.LayoutParams(
                                ActionBar.LayoutParams.WRAP_CONTENT,
                                ActionBar.LayoutParams.WRAP_CONTENT, 1.0f);
                        editText.setLayoutParams(params);

                        EditText editTextIP=new EditText(this);
                        editTextIP.setText(datagramSocket.getInetAddress().getAddress().toString());
                        editTextIP.setLayoutParams(params);

                        RadioButton radioButton=new RadioButton(this);
                        radioButton.setLayoutParams(params);

                        tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        tableRow.addView(editText);
                        tableRow.addView(editTextIP);
                        tableRow.addView(radioButton);
                        tableLayout.addView(tableRow);

                    }
                }
            } catch (IOException e) {//IOException
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
