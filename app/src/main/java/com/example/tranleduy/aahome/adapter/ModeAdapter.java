package com.example.tranleduy.aahome.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.tranleduy.aahome.AbstractTheme;
import com.example.tranleduy.aahome.R;
import com.example.tranleduy.aahome.data.Database;
import com.example.tranleduy.aahome.items.ModeItem;
import com.example.tranleduy.aahome.utils.Protocol;
import com.example.tranleduy.aahome.utils.Connector;

import java.util.ArrayList;

/**
 * Custom com.example.tranleduy.aahome.adapter for {@link AbstractTheme.SectionsPagerAdapter}
 * <p/>
 * Created by tranleduy on 24-May-16.
 */
public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.ViewHolder> {

    private ArrayList<ModeItem> modeItems = new ArrayList<>();
    private Context context;
    private Connector connector = null;
    private Database database;

    public ModeAdapter(Context context, Database database) {
        this.context = context;
        this.modeItems = database.getAllMode();
        this.database = database;
    }

    /**
     * get mode
     *
     * @param pos - position
     * @return ModeItem in position
     */
    public ModeItem getMode(int pos) {
        return modeItems.get(pos);
    }

    /**
     * set socket
     *
     * @param connector - socket
     */
    public void addSocket(Connector connector) {
        this.connector = connector;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(context).inflate(R.layout.item_mode, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ModeItem modeItem = modeItems.get(position);
        holder.txtId.setText("ID: " + modeItem.getId() + "");
        holder.txtName.setText("NAME: " + modeItem.getName());
        holder.txtDes.setText("DESCRIPTION: " + modeItem.getDes());
        holder.txtCommnand.setText("COMMAND: " + modeItem.getCommand());
        holder.checkBox.setChecked(modeItem.isOn());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modeItem.setOn(isChecked);
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connector != null) {
                    connector.sendMessenge(Protocol.POST + modeItem.getCommand() +
                                                (holder.checkBox.isChecked() ? "1" : "0"));
                }
                notifyItemChanged(position);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
                aBuilder.setMessage("Bạn có muốn xóa chế độ?");
                aBuilder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                aBuilder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeMode(modeItem, position);
                        long res = database.removeMode(modeItem);
                        Log.e("DATABASE", String.valueOf(res));
                    }
                });
                aBuilder.create().show();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return modeItems.size();
    }

    public void addMode(ModeItem modeItem) {
        modeItems.add(modeItem);
        notifyItemInserted(modeItems.size() - 1);
    }

    /**
     * remove mode
     *
     * @param modeItem - {@link ModeItem}
     * @param position - position
     */
    public void removeMode(ModeItem modeItem, int position) {
        modeItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtId, txtDes, txtCommnand;
        CheckBox checkBox;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtId = (TextView) itemView.findViewById(R.id.txt_id);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtDes = (TextView) itemView.findViewById(R.id.txt_des);
            txtCommnand = (TextView) itemView.findViewById(R.id.txt_command);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_mode);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}

