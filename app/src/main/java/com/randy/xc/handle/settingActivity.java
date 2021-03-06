package com.randy.xc.handle;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.randy.xc.refreshlibrary.PullToRefreshView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class settingActivity extends AppCompatActivity {

    private Thread send;
    private Thread get;
    private Handler handler;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private List<Connection> connectionList=new ArrayList<Connection>();

    public static final int REFRESH_DELAY = 4000;

    private PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        String message=intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_setting);

        get= new Thread(getRun);
        get.start();
        //broadcast
        send=new Thread(sendRun);
        send.start();

        handler=new Handler() {
            @Override
            public void handleMessage(Message message1)
            {
                super.handleMessage(message1);
                TableLayout tableLayout=(TableLayout)findViewById(R.id.display_message);
                for(int i=1;i<tableLayout.getChildCount();i++)
                {
                    tableLayout.removeView(tableLayout.getChildAt(i));
                }

                for(int i=0;i<connectionList.size();i++) {
                    TableRow tableRow = new TableRow(settingActivity.this);

                    TextView editText = new TextView(settingActivity.this.getApplicationContext());
                    editText.setText(connectionList.get(i).getName_msg());
                    editText.setTextColor(Color.rgb(0, 0, 0));
                    editText.setLayoutParams(new TableRow.LayoutParams(
                            0,
                            ActionBar.LayoutParams.WRAP_CONTENT, 3.5f));

                    TextView editTextIP = new TextView(settingActivity.this);
                    editTextIP.setText(connectionList.get(i).getDatagramPacket().getSocketAddress().toString());
                    editTextIP.setLayoutParams(new TableRow.LayoutParams(
                            0,
                            ActionBar.LayoutParams.WRAP_CONTENT, 5.5f));

                    RadioButton radioButton = new RadioButton(settingActivity.this);
                    radioButton.setLayoutParams(new TableRow.LayoutParams(
                            0,
                            ActionBar.LayoutParams.WRAP_CONTENT, 1.5f));
                    radioButton.setSelected(false);
                    radioButton.setId(i);
                    radioButton.setOnClickListener(new RadioButton.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           for(int j=0;j<connectionList.size();j++)
                                                           {
                                                               connectionList.get(j).setIsSelected(false);
                                                           }
                                                           v.setSelected(true);
                                                           connectionList.get(v.getId()).setIsSelected(true);
                                                       }
                                                   }

                    );

                    tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    tableRow.addView(editText);
                    tableRow.addView(editTextIP);
                    tableRow.addView(radioButton);
                    tableLayout.addView(tableRow);
                }
            }
        };

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });
    }
    @Override
    protected  void onPause()
    {
        super.onPause();
        for(int i=0;i<connectionList.size();i++)
        {
            if(connectionList.get(i).getIsSelected())
            {
                MainActivity.connection=connectionList.get(i);
                break;
            }
        }
    }
    @Override
    protected void onStop()
    {
        if(datagramSocket!=null&&!datagramSocket.isClosed())
            datagramSocket.close();
        super.onStop();
        if(send!=null&&send.isAlive())
            send.interrupt();
        if(get!=null&&get.isAlive())
            get.interrupt();
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
        if(send!=null&&send.isAlive()) {
            send.interrupt();
        }
        send=new Thread(sendRun);
        send.start();
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
        String substr=null;
        try {
            // 建立Socket连接
            datagramSocket = new DatagramSocket(destPortInt);
            datagramSocket.setBroadcast(true);
            datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (true) {

                    // 准备接收数据
                    datagramSocket.receive(datagramPacket);
                    String strMsg=new String(datagramPacket.getData(),0,datagramPacket.getData().length,"UTF-8").trim();
                    if(strMsg==null)
                        continue;
                    if(strMsg.length()<10)
                        substr=strMsg.substring(0);
                    else
                        substr=strMsg.substring(0,9);
                    if(substr.matches("Machine[0-9][0-9]"))
                    {
                        strMsg=strMsg.substring(9);
                        if(isExistMachine(strMsg))
                            continue;
                        Connection connection=new Connection(strMsg,datagramPacket,false);
                        connectionList.add(connection);
                        handler.sendMessage(handler.obtainMessage());
                    }
                }
            } catch (Exception e) {//IOException
                e.printStackTrace();
            }
            datagramSocket.close();
        } catch (SocketException e) {
            if(datagramSocket!=null&&!datagramSocket.isClosed())
                datagramSocket.close();
            e.printStackTrace();
        }
    }

    /*
    ture:exist
    false:not exist
     */
    private boolean isExistMachine(String name)
    {
        if(connectionList.size()<1)
            return false;
        for(int i=0;i<connectionList.size();i++)
        {
            if(connectionList.get(i).getName_msg().equals(name))
                return  true;
        }
        return false;
    }
}
