package com.example.tranleduy.aahome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.tranleduy.aahome.data.Preferences;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Preferences preferences;
    private Button btnLogin;
    private EditText editUser, editPass;
    private CheckBox cbAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = new Preferences(getApplicationContext());
        btnLogin = (Button) findViewById(R.id.btn_login);
        editPass = (EditText) findViewById(R.id.edit_password);
        editUser = (EditText) findViewById(R.id.edit_username);

        editPass.setText(preferences.getString(Preferences.PASSWORD));
        editUser.setText(preferences.getString(Preferences.USER_NAME));

        cbAutoLogin = (CheckBox) findViewById(R.id.ckb_autologin);
        cbAutoLogin.setChecked(preferences.getBoolean(Preferences.AUTO_LOGIN));

        btnLogin.setOnClickListener(this);
        cbAutoLogin.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(LoginActivity.this, "on start", Toast.LENGTH_SHORT).show();
        preferences = new Preferences(getApplicationContext());
        if (preferences.getBoolean(Preferences.AUTO_LOGIN)) {
            String username = preferences.getString(Preferences.USER_NAME);
            String password = preferences.getString(Preferences.PASSWORD);
            if (username.equalsIgnoreCase(Preferences.getUSER()) && password.equals(Preferences.getPASS())) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                View container = findViewById(R.id.ll_login);
                Snackbar.make(container, "Tự động đăng nhập thất bại", Snackbar.LENGTH_LONG)
                        .setAction("Đóng", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(LoginActivity.this, "on destroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            String username = editUser.getText().toString().trim();
            String password = editPass.getText().toString().trim();
            if (username.equalsIgnoreCase(Preferences.getUSER()) && password.equals(Preferences.getPASS())) {
                preferences.putString(Preferences.USER_NAME, username);
                preferences.putString(Preferences.PASSWORD, password);
                Intent intent = new Intent(LoginActivity.this, AbstractTheme.class);
                startActivity(intent);
                this.finish();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setMessage("Sai tên đang nhập hoặc mật khẩu");
                alertDialog.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.create().show();
            }
        } else if (v == cbAutoLogin) {
            preferences.putBoolean(Preferences.AUTO_LOGIN, cbAutoLogin.isChecked());
        }
    }
}
