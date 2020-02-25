package com.example.bluetoothdevice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth;
    int REQUEST_ENABLE_BT = 1;
    Button searchDevice;
    ArrayList<HashMap<String,String>> listOfDevice;
    HashMap<String,String> map;
    SimpleAdapter adapter;
    final String lName = "Device";
    final String lAddress = "Address";
    public static final int REQUEST_CODE_LOC = 1;
    ProgressDialog dialog;


    private static final int REQ_ENABLE_BT  = 10;
    public static final int BT_BOUNDED      = 21;
    public static final int BT_SEARCH       = 22;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView deviceList = (ListView) findViewById(R.id.deviceList);
        searchDevice = findViewById(R.id.searchButton);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetooth = BluetoothAdapter.getDefaultAdapter();



        blConnection a = new blConnection();

        if(a.checkBluetooth(bluetooth) == true)
        {
            Log.i("TEST", "DA");
            if(!a.Enablebluetooth(bluetooth))
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        else
        {
            Log.i("TEST", "NET"); //device without bluetooth

        }

        while(!a.Enablebluetooth(bluetooth))
        {

        }

        listOfDevice = new ArrayList<>();



        Set<BluetoothDevice> pairedDevices= bluetooth.getBondedDevices();
        // Если список спаренных устройств не пуст
        if(pairedDevices.size()>0){
        // проходимся в цикле по этому списку
            for(BluetoothDevice device: pairedDevices){
        // Добавляем имена и адреса в mArrayAdapter, чтобы показать
        // через ListView
                //mArrayAdapter.add(device.getName()+"\n"+ + device.getAddress());

                Log.i("Device",device.getName());
                Log.i("Address", device.getAddress());
                map = new HashMap<>();
                map.put(lName, device.getName());
                map.put(lAddress, device.getAddress());
                listOfDevice.add(map);
            }

            adapter = new SimpleAdapter(this, listOfDevice, android.R.layout.simple_list_item_2,
                    new String[]{lName, lAddress},
                    new int[]{android.R.id.text1, android.R.id.text2});
            deviceList.setAdapter(adapter);


            //adapter.notifyDataSetChanged();

            searchDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    listOfDevice.clear();
                    adapter.notifyDataSetChanged();

                    enableSearch();

                }
            });



        }

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Item Click", listOfDevice.get(i).get(lName));
                Log.i("Item Click", listOfDevice.get(i).get(lAddress));

            }
        });




    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    //btnEnableSearch.setText(R.string.stop_search);
                    //pbProgress.setVisibility(View.VISIBLE);
                    //setListAdapter(BT_SEARCH);
                    //dialog.show(context, "Loading", "Please wait...", true);
                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setTitle("Search");
                    dialog.setMessage("Please wait ...");
                    dialog.setCancelable(false);
                    dialog.show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                   // btnEnableSearch.setText(R.string.start_search);
                    //pbProgress.setVisibility(View.GONE);
                    //dialog.dismiss();
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {

                        map = new HashMap<>();

                        Log.i("Device",device.getAddress());
                        map.put(lAddress, device.getAddress());
                        if(device.getName() != null)
                        {
                            map.put(lName, device.getName());
                            Log.i("device",device.getName());
                        }
                        else
                        {
                            map.put(lName, "Unknown device");
                        }

                        listOfDevice.add(map);
                        adapter.notifyDataSetChanged();

                    }
                    break;
            }
        }
    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode == RESULT_OK)
            {
                Log.i("Bluetooth", "Enable by user");
            }
            else
            {
                if(resultCode == RESULT_CANCELED)
                {
                    Log.i("Bluetooth", "Canceled by User");
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    /**
     * Запрос на разрешение данных о местоположении (для Marshmallow 6.0)
     */

    private void accessLocationPermission() {
        int accessCoarseLocation = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation   = this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            this.requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
        }
    }

    private void enableSearch() {
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        } else {
            accessLocationPermission();
            bluetooth.startDiscovery();
        }
    }

}


class blConnection extends Activity {


    public boolean checkBluetooth(BluetoothAdapter bl)
        {
            if(bl==null)
            {
                return false; // bluetooth no ok
            }
            else
            {
                return true; //Bluetooth ok
            }
        }

        public boolean Enablebluetooth (final BluetoothAdapter bl)
        {
            if(bl.isEnabled())
            {
                Log.i("Bluetooth", "Enable");
                return true;
            }
            else
            {
                return false;
            }
        }


}
