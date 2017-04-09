package project.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button btn1,btn2,btn3,btn4,btn5;
    ListView listView;
    BluetoothAdapter bluetoothAdapter;//=BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice bluetoothDevice;
    Set<BluetoothDevice> myset;
    TextView textView,errr,listHead;
    ArrayAdapter<String> ad;
    ArrayList<String> list=new ArrayList<>();
//    ArrayList<String> availList=new ArrayList<>();
//    ArrayList<String> pairedList=new ArrayList<>();
    ArrayList<BluetoothDevice> btList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1= (Button) findViewById(R.id.button);
        btn2= (Button) findViewById(R.id.button2);
        btn3= (Button) findViewById(R.id.button3);
        btn4= (Button) findViewById(R.id.button4);
        btn5= (Button) findViewById(R.id.button5);
        textView= (TextView) findViewById(R.id.textView);
        listView= (ListView) findViewById(R.id.listView);
        errr= (TextView) findViewById(R.id.textView6);
        listHead= (TextView) findViewById(R.id.textView7);
        listHead.setVisibility(View.INVISIBLE);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(i);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.disable();
                listHead.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.INVISIBLE);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivity(i);
            }
        });


        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myset=bluetoothAdapter.getBondedDevices();
                list.clear();
                ad=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);
                listView.setAdapter(ad);
                for(BluetoothDevice bt:myset){
//                    list.add(bt.getName()+"\n"+bt.getAddress());
                    ad.add(bt.getName()+"\n"+bt.getAddress());
                }
//                ad=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);
//                listView.setAdapter(ad);
                listHead.setText("Paired Devices");
                listHead.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
            }

        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mydisc=BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
                startActivityForResult(new Intent(mydisc),1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            //if(bluetoothDevice==null)
            Toast.makeText(MainActivity.this, "Start Discovery", Toast.LENGTH_SHORT).show();

            bluetoothAdapter.startDiscovery();
            textView.setText("hello");
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            this.registerReceiver(broadcastReceiver,filter);
        }
    }

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String intentAction=intent.getAction();
            if(intentAction.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                Toast.makeText(MainActivity.this, "discovery started", Toast.LENGTH_SHORT).show();
                list.clear();
                ad=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);
                listView.setAdapter(ad);
                listView.setVisibility(View.VISIBLE);
                listHead.setText("Available Devices");
               //availList.clear();
            }
            if(intentAction.equals(BluetoothDevice.ACTION_FOUND)){
                Toast.makeText(MainActivity.this, "Action found", Toast.LENGTH_SHORT).show();

                bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    String device=intent.getStringExtra(BluetoothDevice.EXTRA_NAME)+"\n"+bluetoothDevice.getAddress();
                String device=bluetoothDevice.getName()+"\n"+bluetoothDevice.getAddress();
                errr.setText(device);
                ad.add(device);

//                    ad=new ArrayAdapter<String>(MainActivity.this,android.R.layout.activity_list_item,list);
//                    listView.setAdapter(ad);  //error....
//                    availList.add(device);        //error...
//                    ad.add(device);

            }
            if(intentAction.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                bluetoothAdapter.cancelDiscovery();
                Toast.makeText(MainActivity.this, "discovery fininshed", Toast.LENGTH_SHORT).show();
            }
            if(intentAction.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                Toast.makeText(MainActivity.this, "state changed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
