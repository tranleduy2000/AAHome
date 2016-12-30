package com.example.tranleduy.aahome;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.tranleduy.aahome.utils.Variable;

import java.io.IOException;

/**
 * Created by Duy on 19/7/2016
 */
public class MainActivity extends AbstractTheme {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void msg(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Variable.MO_ACTIVITY_CHON_THIET_BI) {
            String address = data.getStringExtra("data");
            msg(address);
            new ConnectBT().execute(address);
        }
    }

    class ConnectBT extends AsyncTask<String, Void, BluetoothSocket> {
        private Boolean connectSuccess = true;
        private BluetoothSocket bluetoothSocket;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new ProgressDialog(MainActivity.this);
//            dialog.setTitle("Đang kết nối");
//            dialog.setMessage("Vui lòng chờ ...");
//            dialog.show();
            btnConnect.setText("Đang kết nối ...");
        }

        @Override
        protected BluetoothSocket doInBackground(String... params) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(params[0]);
            try {
                bluetoothSocket =
                        bluetoothDevice.createInsecureRfcommSocketToServiceRecord(Variable.myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                bluetoothSocket.connect();
                return bluetoothSocket;
            } catch (IOException e) {
                e.printStackTrace();
                connectSuccess = false;
                return null;
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(BluetoothSocket socket) {
            super.onPostExecute(socket);
            if (socket == null) {
                msg("Kết nối thất bại, không phải giao thức SPP!");
//                txtDevice.setText("Chưa kết nối");
            } else {
                msg("Đã kết nối");
//                txtDevice.setText(address);
//                btnConnect.setText("Ngắt kết nối");
//                txtStatus.setText("Đã kết nối");
//                isConnect = true;
//                updateData(address);
            }
//            dialog.dismiss();

        }
    }
}
