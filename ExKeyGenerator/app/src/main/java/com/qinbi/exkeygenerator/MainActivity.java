package com.qinbi.exkeygenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button UpdateButton=findViewById(R.id.update_button);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExCookieGenerator ecg=new ExCookieGenerator();
                ecg.start();
                try {
                    ecg.join(7000);
                }catch(InterruptedException ie){
                    ie.printStackTrace();
                }
                if(ecg.getStatus()==ExCookieGenerator.FAIL || ecg.getStatus()==ExCookieGenerator.NOT_FINISH){
                    Toast networkError =Toast.makeText(MainActivity.this,"Network Error",Toast.LENGTH_LONG);
                    networkError.show();
                }
                else if(ecg.getStatus()==ExCookieGenerator.SUCCESS) {
                    Map<String, String> cookies = ecg.getCookies();
                    EditText cookieContents = findViewById(R.id.member_id);
                    cookieContents.setText(cookies.get("ipb_member_id"));
                    cookieContents = findViewById(R.id.pass_hash);
                    cookieContents.setText(cookies.get("ipb_pass_hash"));
                    cookieContents = findViewById(R.id.igenous);
                    cookieContents.setText(cookies.get("igenous"));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
