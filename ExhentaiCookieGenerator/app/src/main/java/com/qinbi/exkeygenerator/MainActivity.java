package com.qinbi.exkeygenerator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String originalPandaUrl = "https://panda.gxtel.com";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button UpdateButton = findViewById(R.id.update_button);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExCookieGenerator ecg = new ExCookieGenerator(getPandaUrl());
                ecg.start();
                try {
                    ecg.join(7000L);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                if (ecg.getStatus() == ExCookieGenerator.FAIL || ecg.getStatus() == ExCookieGenerator.NOT_FINISH) {
                    Toast networkError = Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_LONG);
                    networkError.show();
                } else if (ecg.getStatus() == ExCookieGenerator.URL_INCORRECT) {
                    Toast urlIncorrect = Toast.makeText(MainActivity.this, R.string.panda_url_incorrect, Toast.LENGTH_LONG);
                    urlIncorrect.show();
                } else if (ecg.getStatus() == ExCookieGenerator.SUCCESS) {
                    Map<String, String> cookies = ecg.getCookies();
                    EditText cookieContents = findViewById(R.id.member_id);
                    cookieContents.setText(cookies.get("ipb_member_id"));
                    cookieContents = findViewById(R.id.pass_hash);
                    cookieContents.setText(cookies.get("ipb_pass_hash"));
                    cookieContents = findViewById(R.id.igenous);
                    cookieContents.setText(cookies.get("igenous"));
                    cookieContents = findViewById(R.id.url);
                    cookieContents.setText(cookies.get("URL"));
                    cookieContents = findViewById(R.id.exkey);
                    cookieContents.setText(cookies.get("raw_key"));
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

        switch(id){
            case R.id.action_settings:
                setPandaUrl();
                return true;
            case R.id.About:
                Intent goToDeveloper = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/volatileAntiEntropy/EXhentaiCookieGenerator"));
                startActivity(goToDeveloper);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String setPandaUrl() {
        final EditText url = new EditText(this);
        url.setGravity(Gravity.LEFT);
        url.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        url.setSingleLine(true);
        url.setText(getPandaUrl());
        AlertDialog.Builder urlRequestDialogBuilder = new AlertDialog.Builder(this);
        urlRequestDialogBuilder.setTitle(R.string.url).setView(url);
        urlRequestDialogBuilder.setNegativeButton(R.string.no, null);
        urlRequestDialogBuilder.setNeutralButton(R.string.neutral, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("PandaURL", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PandaURL", originalPandaUrl);
                editor.apply();
                Toast msg = Toast.makeText(MainActivity.this, R.string.toastMessage, Toast.LENGTH_LONG);
                msg.show();
            }
        });
        urlRequestDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isURLInputValidate = true;
                try {
                    new URL(url.getText().toString());
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                    isURLInputValidate = false;
                }
                if (isURLInputValidate) {
                    SharedPreferences sharedPreferences = getSharedPreferences("PandaURL", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("PandaURL", url.getText().toString());
                    editor.apply();
                    Toast msg = Toast.makeText(MainActivity.this, R.string.toastMessage, Toast.LENGTH_LONG);
                    msg.show();
                } else {
                    Toast msg = Toast.makeText(MainActivity.this, R.string.url_invalid, Toast.LENGTH_LONG);
                    msg.show();
                }
            }
        }).show();
        return url.getText().toString();
    }

    public String getPandaUrl() {
        return getSharedPreferences("PandaURL", Context.MODE_PRIVATE).getString("PandaURL", originalPandaUrl);
    }
}
