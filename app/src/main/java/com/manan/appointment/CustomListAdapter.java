package com.manan.appointment;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.manan.appointment.data.AppointmentInfo;

import java.util.ArrayList;

/**
 * Created by Manan on 7/15/2016.
 */
public class CustomListAdapter extends BaseAdapter {
    private ArrayList<AppointmentInfo> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context aContext, ArrayList<AppointmentInfo> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.dateView = (TextView) convertView.findViewById(R.id.dateData);
            holder.locationView = (TextView) convertView.findViewById(R.id.locationData);
            holder.timeView = (TextView) convertView.findViewById(R.id.timeData);
            holder.statusView= (TextView)convertView.findViewById(R.id.statusData);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dateView.setText(listData.get(position).getDate());
        holder.locationView.setText("Location : "+listData.get(position).getLocation());
        holder.timeView.setText(listData.get(position).getTime());
        String status=listData.get(position).getStatus().toUpperCase();
        if(status.equals("REQUESTED"))
            holder.statusView.setText(Html.fromHtml("Status : "+"<font color='#FE2020'>"+status+"</font>"));
        else
            holder.statusView.setText(Html.fromHtml("Status : "+"<font color='#00A701'>"+status+"</font>"));
        return convertView;
    }

    static class ViewHolder {
        TextView dateView;
        TextView locationView;
        TextView timeView;
        TextView statusView;
    }
}
