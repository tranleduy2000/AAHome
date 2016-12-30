package com.example.tranleduy.aahome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tranleduy.aahome.data.Preferences;
import com.example.tranleduy.aahome.items.DeviceItem;
import com.example.tranleduy.aahome.utils.Variable;

public class DeviceActivity extends AppCompatActivity {
    private EditText editId, editName, editDes, editCommand;
    private Button btnAdd;
    private ImageView imageView;
    private RecyclerView rcImg;
    private CustomAdapter customAdapter;
    private int imgId = R.drawable.ic_air_conditioner;

    private ActionBar actionBar;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        editId = (EditText) findViewById(R.id.edit_device_id);
        editName = (EditText) findViewById(R.id.edit_device_name);
        editDes = (EditText) findViewById(R.id.edit_device_des);
        editCommand = (EditText) findViewById(R.id.edit_device_command);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        rcImg = (RecyclerView) findViewById(R.id.rc_img);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcImg.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapter(Variable.IMG_DEVICE, this);
        rcImg.setAdapter(customAdapter);

        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("com/example/tranleduy/aahome/data");
        if (bundle != null) {
            type = bundle.getInt("type");
            if (type == 1) {//change info
                actionBar.setTitle("Chỉnh sửa thông tin thiết bị");

                final DeviceItem deviceItem = (DeviceItem) bundle.getSerializable("item");
                editId.setText(deviceItem.getId() + "");
                editId.setEnabled(false);
                editName.setText(deviceItem.getName());
                editCommand.setText(deviceItem.getCommand());
                editDes.setText(deviceItem.getDes());
                imgId = deviceItem.getImg();
                imageView.setImageResource(imgId);
                btnAdd.setText("Lưu");

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editName.getText().toString();
                        String des = editDes.getText().toString();
                        String com = editCommand.getText().toString();

                        deviceItem.setName(name);
                        deviceItem.setDes(des);
                        deviceItem.setCommand(com);
                        deviceItem.setImg(imgId);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Preferences.DEVICE, deviceItem);
                        Intent intent = getIntent();
                        intent.putExtra("com/example/tranleduy/aahome/data", bundle);

                        setResult(Variable.ACTIVITY_CHANGE_INFO_DEVICE, intent);
                        Log.e("ACTIVITY_CHANGE_INFO_DEVICE", "STEP 1");
                        DeviceActivity.this.finish();

                    }
                });

            } else if (type == 0) {
                actionBar.setTitle("Thêm thiết bị");
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = editId.getText().toString();
                        String name = editName.getText().toString();
                        String des = editDes.getText().toString();
                        String com = editCommand.getText().toString();
                        if (id + "" == "") {
                            Toast.makeText(DeviceActivity.this, "ID không được trống", Toast.LENGTH_SHORT).show();
                        } else {
                            int _id = Integer.parseInt(id);
                            DeviceItem deviceItem = new DeviceItem(name, des, com, _id, imgId);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Preferences.DEVICE, deviceItem);
                            Intent intent = getIntent();
                            intent.putExtra("com/example/tranleduy/aahome/data", bundle);
                            setResult(Variable.ACTIVITY_ADD_DEVICE, intent);
                            DeviceActivity.this.finish();
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            DeviceActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private int[] imageIds = {};
        private Context context;

        public CustomAdapter(int[] arrayList, Context context) {
            this.imageIds = arrayList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_img, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.imageView.setImageResource(imageIds[position]);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageResource(imageIds[position]);
                    imgId = imageIds[position];
                }
            });
        }


        @Override
        public int getItemCount() {
            return imageIds.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.img);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
            }
        }
    }
}
