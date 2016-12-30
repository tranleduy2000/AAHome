package com.example.tranleduy.aahome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tranleduy.aahome.data.Preferences;
import com.example.tranleduy.aahome.items.DeviceItem;
import com.example.tranleduy.aahome.utils.Variable;

public class AddDeviceActivity extends AppCompatActivity {
    private EditText editId, editName, editDes, editCommand;
    private Button btnAdd;
    private ImageView imageView;
    private RecyclerView rcImg;
    private CustomAdapter customAdapter;
    private int imgId = R.drawable.ic_air_conditioner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        editId = (EditText) findViewById(R.id.edit_device_id);
        editName = (EditText) findViewById(R.id.edit_device_name);
        editDes = (EditText) findViewById(R.id.edit_device_des);
        editCommand = (EditText) findViewById(R.id.edit_device_command);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String name = editName.getText().toString();
                String des = editDes.getText().toString();
                String com = editCommand.getText().toString();
                if (id + "" == "") {
                    Toast.makeText(AddDeviceActivity.this, "ID Not empty", Toast.LENGTH_SHORT).show();
                } else {
                    int _id = Integer.parseInt(id);
                    DeviceItem deviceItem = new DeviceItem(name, des, com, _id, imgId);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Preferences.DEVICE, deviceItem);
                    Intent intent = getIntent();
                    intent.putExtra("com/example/tranleduy/aahome/data", bundle);
                    setResult(AbstractTheme.ACTIVITY_ADD_DEVICE, intent);
                    AddDeviceActivity.this.finish();
                }
            }
        });

        rcImg = (RecyclerView) findViewById(R.id.rc_img);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcImg.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapter(Variable.IMG_DEVICE, this);
        rcImg.setAdapter(customAdapter);


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
