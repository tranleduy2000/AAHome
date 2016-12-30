package com.example.tranleduy.aahome.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.example.tranleduy.aahome.adapter.CommandAdapter;
import com.example.tranleduy.aahome.items.MessengeItem;

/**
 * Created by duong on 2/14/2016.
 */
public class MySocket {
    public String ipAddress;
    public int port;
    public Socket socket;
    public ServerListener serverListener;
    public BufferedWriter out;
    public BufferedReader in;

    public MySocket(String ip, int port) {
        this.ipAddress = ip;
        this.port = port;
    }

    public void connect() {
        new ConnectToServerTask().execute();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {

        }
        serverListener.connectStatusChange(false);
    }

    public void sendMessenge(String messenge, CommandAdapter messengeListAdapter) {
        if (socket.isConnected()) {
            try {
                out.write(messenge);
                out.newLine();
                out.flush();
                messengeListAdapter.addMessengeItem(new MessengeItem(MessengeItem.TYPE_OUT, messenge));
                Log.e("SEND", messenge);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            serverListener.connectStatusChange(false);
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
            serverListener.connectStatusChange(false);
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
                socket = new Socket(ipAddress, port);
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
            serverListener.connectStatusChange(connnected);
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
            serverListener.newMessengeFromServer(values[0]);
        }
    }
}
