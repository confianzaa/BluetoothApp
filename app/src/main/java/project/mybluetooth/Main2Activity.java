package project.mybluetooth;

    import android.Manifest;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.le.BluetoothLeScanner;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.pm.PackageManager;
    import android.os.Build;
    import android.os.CountDownTimer;
    import android.os.SystemClock;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.content.ContextCompat;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.text.Html;
    import android.text.method.LinkMovementMethod;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.CompoundButton;
    import android.widget.ListView;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.widget.ToggleButton;
    import java.util.ArrayList;
    import java.util.Set;

public class Main2Activity extends AppCompatActivity {
    ListView paired,available;
    ToggleButton bluetooth;
    TextView visibilty,paireddev,availdev;
    BluetoothAdapter adapter;
    BroadcastReceiver find;
    ProgressBar prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        visibilty=(TextView) findViewById(R.id.visibility);
        paireddev=(TextView) findViewById(R.id.textView4);
        availdev= (TextView) findViewById(R.id.textView5);
        paired= (ListView) findViewById(R.id.paireddiv);
        available= (ListView) findViewById(R.id.availdiv);
        bluetooth = (ToggleButton) findViewById(R.id.toggleButton);
        prog = (ProgressBar) findViewById(R.id.progress);

        adapter=BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){
            Toast.makeText(Main2Activity.this, "Bluetooth not supported by your device", Toast.LENGTH_SHORT).show();
        }

        adapter.disable();
        paireddev.setVisibility(View.INVISIBLE);
        availdev.setVisibility(View.INVISIBLE);
        visibilty.setVisibility(View.INVISIBLE);
        paired.setVisibility(View.INVISIBLE);
        available.setVisibility(View.INVISIBLE);
        prog.setVisibility(View.INVISIBLE);

        /*if(adapter.isEnabled()){
            bluetooth.toggle();
            Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,0);
        }*/

        bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
//                    adapter.enable();
                    Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i,0);
                }
                else{
                    adapter.disable();
                    paired.setVisibility(View.INVISIBLE);
                    paireddev.setVisibility(View.INVISIBLE);
                    availdev.setVisibility(View.INVISIBLE);
                    available.setVisibility(View.INVISIBLE);
                    visibilty.setVisibility(View.INVISIBLE);
                    prog.setVisibility(View.INVISIBLE);
                }
            }
        });
        visibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(i,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0) {
            /*SWITCHING ON BLUETOOTH*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Only ask for these permissions on runtime when running Android 6.0 or higher
                switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    case PackageManager.PERMISSION_DENIED:
                        ((TextView) new AlertDialog.Builder(this)
                        .setTitle("Runtime Permissions up ahead")
                        .setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the" +
                                " runtime permissions popup.</p> <p>For more info see <a " +
                                "href=\"http://developer.android.com/about/versions/marshmallow/android‐6.0‐changes.html#behavior‐hardware‐id\">here</a>.</p>"))
                        .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (ContextCompat.checkSelfPermission(getBaseContext(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Main2Activity.this,
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            1);
                                }
                            }
                        })
                        .show()
                        .findViewById(android.R.id.message))
                        .setMovementMethod(LinkMovementMethod.getInstance()); // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                        break;
                    case PackageManager.PERMISSION_GRANTED:
                        break;
                }
            }

            visibilty.setVisibility(View.VISIBLE);
            availdev.setVisibility(View.VISIBLE);
            paireddev.setVisibility(View.VISIBLE);
            paireddev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> pairedList = new ArrayList<>();
                    Set<BluetoothDevice> s = adapter.getBondedDevices();
                    for (BluetoothDevice bd : s) {
                        pairedList.add(bd.getName()+"\n"+bd.getBondState());
                    }
                    ArrayAdapter ad = new ArrayAdapter(Main2Activity.this, android.R.layout.simple_dropdown_item_1line, pairedList);
                    paired.setAdapter(ad);
                    paired.setVisibility(View.VISIBLE);
                }
            });

        }

        if(requestCode==1){

            final ArrayList<BluetoothDevice> avail = new ArrayList<>();
            find = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        prog.setVisibility(View.VISIBLE);
                    }
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(dev.getName()!=null)
                            avail.add(dev);
                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        prog.setVisibility(View.INVISIBLE);
                        adapter.cancelDiscovery();
                        if(avail.size()>0){
                            ArrayAdapter ad = new ArrayAdapter(Main2Activity.this, android.R.layout.simple_dropdown_item_1line, avail);
                            available.setAdapter(ad);
                        }
                        unregisterReceiver(this);
                    }
                }
            };

            final IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

            availdev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //make discoverable
                    adapter.startDiscovery();
                    available.setVisibility(View.VISIBLE);
                    registerReceiver(find, filter);
                }
            });
            available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(Main2Activity.this,"Device selected "+avail.get(position).getName(),Toast.LENGTH_LONG).show();
                    ChatScreen.device = avail.get(position);

                }
            });

            visibilty.setText("Your device is now visible for 2:00 minutes");
            new CountDownTimer(120000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    visibilty.setText("Your device is now visible for "+(millisUntilFinished/1000)+" seconds");
                }
                @Override
                public void onFinish() {
                    unregisterReceiver(find);
                    visibilty.setText("Tap to make your device visible to all other devices");
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(Main2Activity.this, "Paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(Main2Activity.this, "Resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cancelDiscovery();
        unregisterReceiver(find);
        Toast.makeText(getApplicationContext(), "destroyed", Toast.LENGTH_SHORT).show();
    }
}