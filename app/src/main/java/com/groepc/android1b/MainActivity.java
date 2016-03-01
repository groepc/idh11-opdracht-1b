package com.groepc.android1b;

import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText message;
    TextView viewString;
    EditText ipAddress;
    EditText port;
    Spinner tcpUdp;
    private SocketTask mSocketTask = null;


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

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mSocketTask = new SocketTask(ipAddress.getText().toString(), Integer.parseInt(port.getText().toString()), message.getText().toString(), tcpUdp.getSelectedItem().toString());
                    mSocketTask.execute();
                }
            });


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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

    public void setReturnString (String message) {
     viewString.setText(message);
    }


    public class SocketTask extends AsyncTask<Void, String, String> {


        private final String mIPAddress;
        private final Integer mPortNumber;
        private final String mMessage;
        private final String mType;

        SocketTask(String ipAddress, Integer portNumber, String message, String type) {
            mIPAddress = ipAddress;
            mPortNumber = portNumber;
            mMessage = message;
            mType = type;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String message = "";
            if (mType.equals("UDP")) {
                message = UDPClient(mIPAddress, mPortNumber, mMessage);
            } else {
                message = TCPClient(mIPAddress, mPortNumber, mMessage);
            }
            return message;

        }

        @Override
        protected void onPostExecute(final String message) {
            setReturnString(message);
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

            } catch (Exception e) {
                modifiedSentence = getString(R.string.no_connection);
            }
            return modifiedSentence;
        }

        public String UDPClient (String ipAddress, Integer portNumber, String text) {

            String modifiedSentence = null;
            try {
                BufferedReader inFromUser
                        = new BufferedReader(new InputStreamReader(System.in));

                DatagramSocket clientSocket = null;

                clientSocket = new DatagramSocket();

                InetAddress IPAddress = null;
                IPAddress = InetAddress.getByName(ipAddress);


                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];


                sendData = text.getBytes();
                DatagramPacket sendPacket
                        = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);


                clientSocket.send(sendPacket);


                DatagramPacket receivePacket
                        = new DatagramPacket(receiveData, receiveData.length);


               modifiedSentence = new String(receivePacket.getData());
            } catch (Exception e) {
                modifiedSentence = getString(R.string.no_connection);
            }
            return modifiedSentence;
        }
    }
}
