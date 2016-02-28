package com.groepc.android1b;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText message;
    TextView viewString;
    EditText ipAddress;
    EditText port;
    Spinner tcpUdp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.button);
        message = (EditText) findViewById(R.id.message);
        viewString = (TextView) findViewById(R.id.textView3);
        ipAddress = (EditText) findViewById(R.id.editText);
        port = (EditText) findViewById(R.id.editText2);
        tcpUdp = (Spinner) findViewById(R.id.spinner);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String text = tcpUdp.getSelectedItem().toString();

                    if (text.equals("UDP")) {
                    viewString.setText("Connentie met UDP");
                    } else {
                        viewString.setText(TCPClient(ipAddress.getText().toString(), Integer.parseInt(port.getText().toString()), message.getText().toString()));
                    }
                }
            });
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    public String TCPClient (String ipAddress, Integer portNumber, String text) {
        BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));

        String modifiedSentence = null;
        try (Socket clientSocket = new Socket(ipAddress, portNumber)) {
            DataOutputStream outToServer
                    = new DataOutputStream(clientSocket.getOutputStream());

            BufferedReader inFromServer
                    = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            outToServer.writeBytes(text+ '\n');

            modifiedSentence = inFromServer.readLine();

            clientSocket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return modifiedSentence;
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
