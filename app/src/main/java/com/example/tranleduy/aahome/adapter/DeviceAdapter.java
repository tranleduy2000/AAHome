package com.example.tranleduy.aahome.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tranleduy.aahome.R;

import java.util.ArrayList;

import com.example.tranleduy.aahome.data.Database;
import com.example.tranleduy.aahome.items.DeviceItem;
import com.example.tranleduy.aahome.listener.SpinnerInteractionListener;
import com.example.tranleduy.aahome.utils.Protocol;
import com.example.tranleduy.aahome.utils.Connector;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private ArrayList<DeviceItem> deviceItems = new ArrayList<>();
    private Context context;
    private Database database;
    private Connector connector = null;
    private DeviceListener deviceListener;

    public DeviceAdapter(Context context, Database database, DeviceListener deviceListener) {
        this.context = context;
        this.database = database;
        this.deviceItems = database.getAllDevice();
        this.deviceListener = deviceListener;
    }

    public DeviceItem getDeviceItem(int position) {
        return deviceItems.get(position);
    }

    public void addDevice(DeviceItem deviceItem) {
        deviceItems.add(deviceItem);
        notifyItemInserted(deviceItems.size() - 1);
    }

    public void removeDevice(DeviceItem deviceItem, int position) {
        deviceItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DeviceItem deviceItem = deviceItems.get(position);
        holder.txtId.setText(deviceItem.getId() + "");
        holder.txtName.setText(deviceItem.getName());
        holder.txtDes.setText(deviceItem.getDes());
        holder.imageView.setImageResource(deviceItem.getImg());
        holder.aSwitch.setChecked(deviceItem.isOn());
        holder.aSwitch.setText(deviceItem.isOn() ? R.string.on : R.string.off);
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connector != null) {
                    connector.sendMessenge(
                            Protocol.POST +
                                    (holder.aSwitch.isChecked() ? Protocol.SET_ON : Protocol.SET_OFF) +
                                    deviceItem.getId());
                    deviceItem.setOn(holder.aSwitch.isChecked());
                    database.updateDevice(deviceItem);
                    notifyItemChanged(position);
                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
                aBuilder.setMessage("Bạn có muốn xóa thiết bị?");
                aBuilder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                aBuilder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDevice(deviceItem, position);
                        database.removeDevice(deviceItem);
                    }
                });
                aBuilder.create().show();
                return false;
            }
        });

//        SpinnerDeviceInfoAdapter spinnerDeviceInfoAdapter =
//                new SpinnerDeviceInfoAdapter(context, R.layout.item_spin_detail);
//        holder.spinner.setAdapter(spinnerDeviceInfoAdapter);
        SpinnerInteractionListener listener = new SpinnerInteractionListener();

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        deviceListener.deviceDetail(position, deviceItem);
                        break;
                    case 1:
                        deviceListener.deviceChangeInfo(position, deviceItem);
                        break;
                    case 2:
                        deviceListener.deviceDelete(position, deviceItem);
                        break;
                }
                Log.d("index", String.valueOf(position));
//                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public boolean setStatusPin(int pin, boolean isOn) {
        for (int count = 0; count < getItemCount(); count++) {
            DeviceItem deviceItem = deviceItems.get(count);
            if (deviceItem.getId() == pin) {
                deviceItem.setOn(isOn);
                notifyItemChanged(count);

                long res = database.updateDevice(deviceItem);
                Log.e("DATABASE", String.valueOf(res));
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return deviceItems.size();
    }

    public void addSocket(Connector connector) {
        this.connector = connector;
    }

    public void setDeviceListener(DeviceListener deviceListener) {
        this.deviceListener = deviceListener;
    }

    public void updateDevice(DeviceItem deviceItem) {
        for (int index = 0; index < getItemCount(); index++) {
            if (deviceItem.getId() == deviceItems.get(index).getId()) {
                deviceItems.set(index, deviceItem);
                notifyItemChanged(index);
                break;
            }
        }
    }

    public interface DeviceListener {
        void deviceDetail(int pos, DeviceItem deviceItem);

        void deviceChangeInfo(int pos, DeviceItem deviceItem);

        void deviceDelete(int pos, DeviceItem deviceItem);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDes, txtId;
        SwitchCompat aSwitch;
        CardView cardView;
        ImageView imageView;
        Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            txtId = (TextView) itemView.findViewById(R.id.txt_id);
            txtDes = (TextView) itemView.findViewById(R.id.txt_des_item);
            txtName = (TextView) itemView.findViewById(R.id.txt_name_item);
            cardView = (CardView) itemView.findViewById(R.id.cv_item);
            aSwitch = (SwitchCompat) itemView.findViewById(R.id.sw_item);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            spinner = (Spinner) itemView.findViewById(R.id.spinnerDetail);
        }
    }
}
