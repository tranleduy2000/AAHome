package com.example.tranleduy.aahome.utils;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tranleduy.aahome.adapter.CommandAdapter;
import com.example.tranleduy.aahome.items.MessengeItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by duong on 2/14/2016.
 */
public class Connector implements Serializable {
    public BluetoothSocket socket = null;
    public ServerListener serverListener = null;
    public BufferedWriter out;
    public BufferedReader in;

    public Connector(BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public void connect() {
        new ConnectToServerTask().execute();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serverListener != null) serverListener.connectStatusChange(false);
    }

    public void sendMessenge(String messenge, CommandAdapter messengeListAdapter) {
        if (socket.isConnected()) {
            try {
                out.write(messenge);
                out.newLine();
                out.flush();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                String currentDateandTime = sdf.format(new Date());

                messengeListAdapter.addMessengeItem(new MessengeItem(currentDateandTime, MessengeItem.TYPE_OUT, messenge));
                Log.e("SEND", messenge);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (serverListener != null) serverListener.connectStatusChange(false);
        }
    }

    public void sendMessenge(String messenge, CommandAdapter messengeListAdapter,
                             boolean debug, boolean isConnected) {
        if (!debug) {
            sendMessenge(messenge, messengeListAdapter);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String currentDateandTime = sdf.format(new Date());
        messengeListAdapter.addMessengeItem(new MessengeItem(currentDateandTime, MessengeItem.TYPE_OUT, messenge));
        if (!isConnected) return;
        if (socket.isConnected()) {
            try {
                out.write(messenge);
                out.newLine();
                out.flush();
                Log.e("SEND", messenge);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (serverListener != null) serverListener.connectStatusChange(false);
        }
    }

    public void sendMessenge(String messenge) {
        if (socket.isConnected()) {
            try {
                out.write(messenge);
                out.newLine();
                out.flush();
                Log.e("SEND", messenge);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (serverListener != null) serverListener.connectStatusChange(false);
        }
    }

    public void setServerListener(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public interface ServerListener {
        void connectStatusChange(boolean status);

        void newMessengeFromServer(String messenge);
    }

    public class ConnectToServerTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (socket.isConnected()) {
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean connnected) {
            if (serverListener != null) serverListener.connectStatusChange(connnected);
            if (connnected) {
                new ServerListenner().execute();
            }
        }
    }

    public class ServerListenner extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String messenge;
            try {
                messenge = in.readLine();
                while (messenge != null) {
                    publishProgress(messenge);
                    messenge = in.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
                in.close();
                out.close();
//                serverListener.connectStatusChange(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (serverListener != null) serverListener.newMessengeFromServer(values[0]);
        }
    }
}
