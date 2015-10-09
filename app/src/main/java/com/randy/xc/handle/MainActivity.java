package com.randy.xc.handle;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE="com.randy.xc.handle.MESSAGE";
    public static Connection connection=null;
    private Socket socket=null;
    private OutputStream outputStream=null;
    // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
    private byte buffer[] = new byte[6];

    private TextView textView=null;
    private Handler handler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)this.findViewById(R.id.textStatus);
        handler=new Handler(){
            @Override
            public void handleMessage(Message message)
            {
                super.handleMessage(message);
                textView.setText(message.obj.toString());
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
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void button_Xs(View view) throws Exception
    {
        buffer[0]=0;
        buffer[1]=buffer[2]=buffer[3]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Xp(View view) throws  Exception
    {
        buffer[0]=1;
        buffer[1]=buffer[2]=buffer[3]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Ys(View view)throws  Exception
    {
        buffer[1]=0;
        buffer[0]=buffer[2]=buffer[3]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Yp(View view)throws Exception
    {
        buffer[1]=1;
        buffer[0]=buffer[2]=buffer[3]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Zs(View view)throws Exception
    {
        buffer[2]=0;
        buffer[0]=buffer[1]=buffer[3]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Zp(View view)throws Exception
    {
        buffer[2]=1;
        buffer[0]=buffer[1]=buffer[3]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Rs(View view)throws Exception
    {
        buffer[3]=0;
        buffer[0]=buffer[2]=buffer[1]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_Rp(View view)throws Exception
    {
        buffer[3]=1;
        buffer[0]=buffer[2]=buffer[1]=buffer[4]=buffer[5]=2;
        sendTcp();
    }
    public void button_R1s(View view)throws Exception
    {
        buffer[4]=0;
        buffer[0]=buffer[2]=buffer[3]=buffer[1]=buffer[5]=2;
        sendTcp();
    }
    public void button_R1p(View view)throws Exception
    {
        buffer[4]=1;
        buffer[0]=buffer[2]=buffer[3]=buffer[1]=buffer[5]=2;
        sendTcp();
    }
    public void button_R2s(View view)throws Exception
    {
        buffer[5]=0;
        buffer[0]=buffer[2]=buffer[3]=buffer[4]=buffer[1]=2;
        sendTcp();
    }
    public void button_R2p(View view)throws Exception
    {
        buffer[5]=1;
        buffer[0]=buffer[2]=buffer[3]=buffer[4]=buffer[1]=2;
        sendTcp();
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


            /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
			//String socketData = "[2143213;21343fjks;213]";
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//					socket.getOutputStream()));
//			writer.write(socketData.replace("\n", " ") + "\n");
//			writer.flush();
            /************************************************/

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

    private void sendTcp() throws Exception
    {
        try {
            // 获取Socket的OutputStream对象用于发送数据。
            outputStream = socket.getOutputStream();

            outputStream.write(buffer, 0, buffer.length);

            // 发送读取的数据到服务端
            outputStream.flush();
        }catch (Exception e)
        {
            if(outputStream!=null)
                outputStream.close();
        }
    }
}
