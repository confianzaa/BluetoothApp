package project.mybluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ChatScreen extends AppCompatActivity {
    public static BluetoothDevice device;
    ProgressBar prog;
    TextView connectionText;
    EditText message;
    Button send;
    ListView chats;
    ArrayList<String> msgs = new ArrayList<>();
    ArrayAdapter ad;
    BluetoothSocket socket;
    InputStream istream;
    OutputStream ostream;
    Thread receiveThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        if(device==null){
            Toast.makeText(this, "Please select the device again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChatScreen.this,Main2Activity.class);
            startActivity(intent);
        }

        prog = (ProgressBar) findViewById(R.id.progressBar);
        connectionText = (TextView) findViewById(R.id.connectionText);
        (message = (EditText) findViewById(R.id.message)).setVisibility(View.INVISIBLE);
        (send = (Button) findViewById(R.id.send)).setVisibility(View.INVISIBLE);
        (chats = (ListView) findViewById(R.id.chats)).setVisibility(View.INVISIBLE);
        ad = new ArrayAdapter(ChatScreen.this,android.R.layout.simple_dropdown_item_1line,msgs);
        chats.setAdapter(ad);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String msg = message.getText().toString();
                ad.add(msg);
                message.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ostream.write(msg.getBytes());
                            // use handler to set message empty
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });



        try {
            startConnection();
            startDataTransfer();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    private void startConnection() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        socket = device.createRfcommSocketToServiceRecord(uuid);
        socket.connect();
        istream = socket.getInputStream();
        ostream = socket.getOutputStream();
        prog.setVisibility(View.GONE);
        connectionText.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        send.setVisibility(View.VISIBLE);
        chats.setVisibility(View.VISIBLE);
    }

    private void startDataTransfer() {
        //receiving data...
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        int bytecount = istream.available();
                        if(bytecount>0){
                            byte[] raw = new byte[bytecount];
                            istream.read(raw);
                            final String text = new String(raw,"UTF-8");
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    ad.add(text);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        receiveThread.start();
    }


}
