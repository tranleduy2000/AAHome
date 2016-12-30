package com.example.tranleduy.aahome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tranleduy.aahome.data.Preferences;
import com.example.tranleduy.aahome.items.ModeItem;
import com.example.tranleduy.aahome.utils.Variable;

public class ModeActivity extends AppCompatActivity {
    private EditText editId, editName, editDes, editCommand;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mode);
        editId = (EditText) findViewById(R.id.edit_device_id);
        editName = (EditText) findViewById(R.id.edit_device_name);
        editDes = (EditText) findViewById(R.id.edit_device_des);
        editCommand = (EditText) findViewById(R.id.edit_device_command);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String name = editName.getText().toString();
                String des = editDes.getText().toString();
                String com = editCommand.getText().toString();
                if (id + "" == "") {
                    Toast.makeText(ModeActivity.this, "ID Not empty", Toast.LENGTH_SHORT).show();
                } else {
                    int _id = Integer.parseInt(id);
                    ModeItem modeItem = new ModeItem(name, com, des, _id);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Preferences.MODE, modeItem);
                    Intent intent = getIntent();
                    intent.putExtra("com/example/tranleduy/aahome/data", bundle);
                    setResult(Variable.ACTIVITY_ADD_MODE, intent);
                    ModeActivity.this.finish();
                }
            }
        });
    }
}
