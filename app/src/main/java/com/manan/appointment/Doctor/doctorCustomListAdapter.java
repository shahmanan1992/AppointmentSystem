package com.manan.appointment.Doctor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.manan.appointment.R;
import com.manan.appointment.data.AppointmentInfo;

import java.util.ArrayList;

/**
 * Created by Manan on 8/1/2016.
 */
public class doctorCustomListAdapter extends BaseAdapter {

    private ArrayList<AppointmentInfo> listData;
    private LayoutInflater layoutInflater;

    public doctorCustomListAdapter(Context aContext, ArrayList<AppointmentInfo> listData) {
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
            convertView = layoutInflater.inflate(R.layout.doctor_list_row_layout, null);
            holder = new ViewHolder();
            holder.dateView = (TextView) convertView.findViewById(R.id.doctor_dateData);
            holder.doctorPatientView = (TextView) convertView.findViewById(R.id.doctor_patient);
            holder.doctorPatientContactView= (TextView)convertView.findViewById(R.id.doctor_patient_contact);
            holder.doctorPatientReasonView= (TextView)convertView.findViewById(R.id.doctor_patient_reason);
            holder.timeView = (TextView) convertView.findViewById(R.id.doctor_timeData);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dateView.setText(listData.get(position).getDate());
        holder.doctorPatientView.setText(listData.get(position).getPatientName());
        if(!listData.get(position).getReason().isEmpty())
        {
            holder.doctorPatientReasonView.setText("Reason : "+listData.get(position).getReason());
        }
        else
        {
            holder.doctorPatientContactView.setBackground(null);
        }
        holder.timeView.setText(listData.get(position).getTime());
        return convertView;
    }

    static class ViewHolder {
        TextView dateView;
        TextView doctorPatientView;
        TextView doctorPatientContactView;
        TextView doctorPatientReasonView;
        TextView timeView;
    }
}
