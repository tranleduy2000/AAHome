package com.example.tranleduy.aahome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tranleduy.aahome.utils.Protocol;
import com.example.tranleduy.aahome.utils.Connector;

public class GarageControlActivity extends AppCompatActivity implements Connector.ServerListener {
    private Connector connector;
    private boolean connected = false;
    private Button btnClose, btnOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage_control);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cửa nhà xe");
        getData();
        initView();
    }

    private void initView() {
        btnOpen = (Button) findViewById(R.id.btnOpenDoor);
        btnClose = (Button) findViewById(R.id.btnCloseDoor);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    String cmd = Protocol.POST + Protocol.OPEN_DOOR;
                    connector.sendMessenge(cmd);
                } else {
                    Toast.makeText(GarageControlActivity.this, "Không có kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    String cmd = Protocol.POST + Protocol.CLOSE_DOOR;
                    connector.sendMessenge(cmd);
                } else {
                    Toast.makeText(GarageControlActivity.this, "Không có kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("com/example/tranleduy/aahome/data");
        String ip = bundle.getString("ip");
        int port = bundle.getInt("port");
        connector = new Connector(ip, port);
        connector.setServerListener(this);
        connector.connect();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectStatusChange(boolean status) {
        this.connected = connected;
    }

    @Override
    public void newMessengeFromServer(String messenge) {

    }
}
