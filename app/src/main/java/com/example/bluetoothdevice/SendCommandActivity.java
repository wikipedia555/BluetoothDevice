package com.example.bluetoothdevice;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SendCommandActivity extends AppCompatActivity {

    final String lName = "Device";
    final String lAddress = "Address";
    BluetoothSocket clientSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_command);
        // Принимаем имя
        String deviceName = getIntent().getStringExtra(lName);

        // Принимаем адрес
        String deviceAddress = getIntent().getStringExtra(lAddress);

        Log.i(lName,deviceName);
        Log.i(lAddress,deviceAddress);

        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);

        //Мы хотим использовать тот bluetooth-адаптер, который задается по умолчанию
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Пытаемся проделать эти действия
        try{
            //Устройство с данным адресом - наш Bluetooth Bee
            //Адрес опредеяется следующим образом: установите соединение
            //между ПК и модулем (пин: 1234), а затем посмотрите в настройках
            //соединения адрес модуля. Скорее всего он будет аналогичным.
            BluetoothDevice device = bluetooth.getRemoteDevice(deviceAddress);

            //Инициируем соединение с устройством
            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[] {int.class});

            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();

            //В случае появления любых ошибок, выводим в лог сообщение
        } catch (IOException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (SecurityException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (InvocationTargetException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }

        //Выводим сообщение об успешном подключении
        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();


    }


    public void sendString(String command)
    {
        try {
            //Получаем выходной поток для передачи данных
            OutputStream outStream = clientSocket.getOutputStream();

            //Пишем данные в выходной поток
            outStream.write(1);

        } catch (IOException e) {
            //Если есть ошибки, выводим их в лог
            Log.d("BLUETOOTH", e.getMessage());
        }
    }

}



