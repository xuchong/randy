package com.randy.xc.handle;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Handler;

import org.xmlpull.v1.XmlPullParser;
import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE="com.randy.xc.handle.MESSAGE";
    public static Connection connection=null;
    private Socket socket=null;
    private Socket socket_get_post=null;
    private OutputStream outputStream=null,outputStream_get_post=null;
    private InputStream inputStream_get_post=null;
    // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
    private byte buffer[] = new byte[6];

    private TextView textView=null;
    private Handler handler=null;

    private RadioButton radioButton[]=new RadioButton[6];
    private TextView textView_coordinator[]=new TextView[4];
    private Handler handler_get=null;

    private int speed=0;
    private int steplength=0;
    private int jog_index=0;
    private String[] coordinators=new String[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)this.findViewById(R.id.textStatus);

        View.OnTouchListener listener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v!=null&&v instanceof FButton)
                    ((FButton)v).onTouch(v,event);
                int index=0;
                byte value=2;
                if(v.getId() == R.id.Button01){
                    index=0;
                    value=0;
                }else if(v.getId()==R.id.Button02)
                {
                    index=0;
                    value=1;
                }else if(v.getId()==R.id.Button03)
                {
                    index=1;
                    value=0;
                }else if(v.getId()==R.id.Button04)
                {
                    index=1;
                    value=1;
                }else if(v.getId()==R.id.Button05)
                {
                    index=2;
                    value=0;
                }else if(v.getId()==R.id.Button06)
                {
                    index=2;
                    value=1;
                }else if(v.getId()==R.id.Button07)
                {
                    index=3;
                    value=0;
                }else if(v.getId()==R.id.Button08)
                {
                    index=3;
                    value=1;
                }else if(v.getId()==R.id.Button09)
                {
                    index=4;
                    value=0;
                }else if(v.getId()==R.id.Button10)
                {
                    index=4;
                    value=1;
                }else if(v.getId()==R.id.Button11)
                {
                    index=5;
                    value=0;
                }else if(v.getId()==R.id.Button12)
                {
                    index=5;
                    value=1;
                }else
                {
                    return false;
                }
                buffer[0]=buffer[1]=buffer[2]=buffer[3]=buffer[4]=buffer[5]=2;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    buffer[index]=value;
                }
                sendTcp();
                return false;
            }
        };
        FButton buttonxs,buttonxp,buttonys,buttonyp,buttonzs,buttonzp,buttonrs,buttonrp,buttonr1s,buttonr1p,buttonr2s,buttonr2p;
        buttonxs=(FButton)findViewById(R.id.Button01);
        buttonxp=(FButton)findViewById(R.id.Button02);
        buttonys=(FButton)findViewById(R.id.Button03);
        buttonyp=(FButton)findViewById(R.id.Button04);
        buttonzs=(FButton)findViewById(R.id.Button05);
        buttonzp=(FButton)findViewById(R.id.Button06);
        buttonrs=(FButton)findViewById(R.id.Button07);
        buttonrp=(FButton)findViewById(R.id.Button08);
        buttonr1s=(FButton)findViewById(R.id.Button09);
        buttonr1p=(FButton)findViewById(R.id.Button10);
        buttonr2s=(FButton)findViewById(R.id.Button11);
        buttonr2p=(FButton)findViewById(R.id.Button12);
        buttonxs.setOnTouchListener(listener);
        buttonxp.setOnTouchListener(listener);
        buttonys.setOnTouchListener(listener);
        buttonyp.setOnTouchListener(listener);
        buttonzs.setOnTouchListener(listener);
        buttonzp.setOnTouchListener(listener);
        buttonrs.setOnTouchListener(listener);
        buttonrp.setOnTouchListener(listener);
        buttonr1s.setOnTouchListener(listener);
        buttonr1p.setOnTouchListener(listener);
        buttonr2s.setOnTouchListener(listener);
        buttonr2p.setOnTouchListener(listener);

        handler=new Handler(){
            @Override
            public void handleMessage(Message message)
            {
                super.handleMessage(message);
                textView.setText(message.obj.toString());
            }
        };

        radioButton[0]=(RadioButton)findViewById(R.id.pointmv);
        radioButton[1]=(RadioButton)findViewById(R.id.linemv);
        radioButton[2]=(RadioButton)findViewById(R.id.m001);
        radioButton[3]=(RadioButton)findViewById(R.id.m01);
        radioButton[4]=(RadioButton)findViewById(R.id.m1);
        radioButton[5]=(RadioButton)findViewById(R.id.m10);

        textView_coordinator[0]=(TextView)findViewById(R.id.data_x);
        textView_coordinator[1]=(TextView)findViewById(R.id.data_y);
        textView_coordinator[2]=(TextView)findViewById(R.id.data_z);
        textView_coordinator[3]=(TextView)findViewById(R.id.data_r);

        handler_get=new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                super.handleMessage(message);
                if(jog_index==1)
                    radioButton[0].setChecked(true);
                else  if(jog_index==0)
                    radioButton[1].setChecked(true);

                if(steplength==1)
                    radioButton[2].setChecked(true);
                else if(steplength==2)
                    radioButton[3].setChecked(true);
                else if(steplength==3)
                    radioButton[4].setChecked(true);
                else if(steplength==4)
                    radioButton[5].setChecked(true);

                for(int i=0;i<4;i++)
                    textView_coordinator[i].setText(coordinators[i]);
            }
        };

        if (connection!=null)
            new Thread(runConnnect).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try {
            //stand for end :  symbol 3
            buffer[0]=buffer[1]=buffer[2]=buffer[3]=buffer[4]=buffer[5]=3;
            outputStream = socket.getOutputStream();
            outputStream.write(buffer, 0, buffer.length);
            // 发送读取的数据到服务端
            outputStream.flush();

            if (outputStream != null)
                outputStream.close();
            if (socket != null && socket.isConnected())
                socket.close();
            if(inputStream_get_post!=null)
                inputStream_get_post.close();
            if(outputStream_get_post!=null)
                outputStream_get_post.close();
            if (socket_get_post != null && socket_get_post.isConnected())
                socket_get_post.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void button_Speeds(View view)throws Exception
    {
        speed--;
        sendTcpOfMachine();
    }
    public  void button_Speedp(View view)throws Exception
    {
        speed++;
        sendTcpOfMachine();
    }

    public void radio_point(View view)throws Exception
    {
        jog_index=1;
        sendTcpOfMachine();
    }
    public void radio_line(View view)throws Exception
    {
        jog_index=0;
        sendTcpOfMachine();
    }
    public void radio_m001(View view)throws Exception
    {
        steplength=1;
        sendTcpOfMachine();
    }
    public void radio_m01(View view)throws Exception
    {
        steplength=2;
        sendTcpOfMachine();
    }
    public void radio_m1(View view)throws Exception
    {
        steplength=3;
        sendTcpOfMachine();
    }
    public void radio_m10(View view)throws Exception
    {
        steplength=4;
        sendTcpOfMachine();
    }
    public void setConnection(MenuItem  view)
    {
        Intent intent=new Intent(this,settingActivity.class);
        TextView editText=(TextView)findViewById(R.id.textStatus);
        String message=editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    protected void connectServerWithTCPSocket() {
        try {
            // 创建一个Socket对象，并指定服务端的IP及端口号： remote IP and Port
            socket = new Socket(connection.getDatagramPacket().getAddress(),9999);
            socket.setTcpNoDelay(true);
            //get coordinator information and send step_length ,speed
            socket_get_post=new Socket(connection.getDatagramPacket().getAddress(),10000);
            socket_get_post.setTcpNoDelay(true);
            new Thread(runGetCoordinator).start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            Message msg=Message.obtain();
            if(socket==null)
            {
                msg.obj="未连接，请重试";
            }
            else
            {
                msg.obj="已连接：" + connection.getName_msg();
            }
            handler.sendMessage(msg);
        }
    }
    Runnable runConnnect=new Runnable() {
        @Override
        public void run() {
            connectServerWithTCPSocket();
        }
    };

    private void sendTcp()
    {
        try {
            // 获取Socket的OutputStream对象用于发送数据。
            outputStream = socket.getOutputStream();

            outputStream.write(buffer, 0, buffer.length);

            // 发送读取的数据到服务端
            outputStream.flush();
        }catch (Exception e)
        {
            try {
                if (outputStream != null)
                    outputStream.close();
            }catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }

    private void sendTcpOfMachine()
    {
        String dataxml=null;
        try {
            //send change of machine information
            outputStream_get_post=socket_get_post.getOutputStream();
            dataxml= "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                    "<machine>" +
                    "<type>"+2+"</type>"+
                    "<jogindex>"+jog_index+"</jogindex>"+
                    "<steplength>"+steplength+"</steplength>"+
                    "<speed>"+speed+"</speed>"+
                    "</machine>";
            outputStream_get_post.write(dataxml.getBytes());
            outputStream_get_post.flush();
        }catch ( Exception e)
        {
            e.printStackTrace();
        }
    }

    Runnable runGetCoordinator=new Runnable() {
        @Override
        public void run() {
            getCoordinator();
        }
    };

    //<cmd></cmd>:1 stand for get
    //<cmd> 2:stand for send machine set information
    private void getCoordinator()
    {
        byte[] data=null;
        String dataxml;
        String xmls[];
        try {
            //get machine information:Initial
            outputStream_get_post=socket_get_post.getOutputStream();
            dataxml= "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                    "<machine>"+"<type>"+1+"</type>"+"</machine>";
            outputStream_get_post.write(dataxml.getBytes());
            while(socket_get_post!=null) {
                //wait for machine information
                inputStream_get_post = socket_get_post.getInputStream();
                data=new byte[inputStream_get_post.available()];
                inputStream_get_post.read(data);
                dataxml=new String(data).trim();
                if(dataxml.equals(""))
                    continue;
                xmls=dataxml.split("</machine>");
                for(int i=0;i<xmls.length;i++) {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(new StringReader(xmls[i]+"</machine>"));
                    int event = parser.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT) {
                        switch (event) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.START_TAG:
                                if ("jogindex".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        jog_index = Integer.parseInt(parser.getText());
                                    }
                                } else if ("steplength".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        steplength = Integer.parseInt(parser.getText());
                                    }
                                } else if ("speed".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        speed = Integer.parseInt(parser.getText());
                                    }
                                } else if ("x".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        coordinators[0] = parser.getText();
                                    }
                                }else if ("y".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        coordinators[1] = parser.getText();
                                    }
                                }else if ("z".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        coordinators[2] = parser.getText();
                                    }
                                }else if ("r".equals(parser.getName())) {
                                    event = parser.next();
                                    if (parser.getEventType() == XmlPullParser.TEXT) {
                                        coordinators[3] = parser.getText();
                                    }
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                break;
                        }
                        event = parser.next();
                    }
                    handler_get.sendMessage(Message.obtain());
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
