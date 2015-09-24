package com.randy.xc.handle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE="com.randy.xc.handle.MESSAGE";
    public static Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void button_Xs(View view)
    {

    }
    public void button_Xp(View view)
    {

    }
    public void button_Ys(View view)
    {

    }
    public void button_Yp(View view)
    {

    }
    public void button_Zs(View view)
    {

    }
    public void button_Zp(View view)
    {

    }
    public void button_Rs(View view)
    {

    }
    public void button_Rp(View view)
    {

    }
    public void button_R1s(View view)
    {

    }
    public void button_R1p(View view)
    {

    }
    public void button_R2s(View view)
    {

    }
    public void button_R2p(View view)
    {

    }


    public void setConnection(MenuItem  view)
    {
        Intent intent=new Intent(this,settingActivity.class);
        TextView editText=(TextView)findViewById(R.id.textStatus);
        String message=editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
