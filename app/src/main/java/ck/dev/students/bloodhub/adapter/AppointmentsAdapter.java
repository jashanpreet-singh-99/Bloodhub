package ck.dev.students.bloodhub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.Appointment;
import ck.dev.students.bloodhub.model.User;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.appointmentViewHolder> {

    private final ArrayList<Appointment> dataList;
    private final ArrayList<User> userList;
    private final ArrayList<String> userKeyList;

    public AppointmentsAdapter(ArrayList<Appointment> dataList, ArrayList<User> userList, ArrayList<String> userKeyList) {
        this.dataList = dataList;
        this.userList = userList;
        this.userKeyList = userKeyList;
    }

    @NonNull
    @Override
    public appointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item_appointments, parent, false);
        return new appointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull appointmentViewHolder holder, int position) {
        Appointment appointment = dataList.get(position);

        if (userKeyList.contains(appointment.userid)) {
            User u = userList.get(userKeyList.indexOf(appointment.userid));
            holder.nameTxtView.setText(u.username);
            holder.bloodLayout.setVisibility(View.VISIBLE);
            holder.bloodTypeView.setText(u.blood_group);
        } else{
            holder.nameTxtView.setText(appointment.userid);
            holder.bloodLayout.setVisibility(View.GONE);
        }

        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
        final String date = df.format(appointment.date);
        holder.dateTxtView.setText(date);

        holder.transportTxtView.setText((appointment.transport)? "Yes": "No");

        final String cDate = df.format(appointment.created_at);
        holder.createdDateTxtView.setText(cDate);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class appointmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTxtView;
        private final TextView dateTxtView;
        private final TextView transportTxtView;
        private final TextView createdDateTxtView;
        private final LinearLayout bloodLayout;
        private final TextView bloodTypeView;

        public appointmentViewHolder(View itemView) {
            super(itemView);
            nameTxtView        = itemView.findViewById(R.id.name_view);
            dateTxtView        = itemView.findViewById(R.id.date_view);
            transportTxtView   = itemView.findViewById(R.id.transport_view);
            createdDateTxtView = itemView.findViewById(R.id.created_at_view);
            bloodLayout        = itemView.findViewById(R.id.blood_layout);
            bloodTypeView      = itemView.findViewById(R.id.blood_type_view);
        }

    }
    
}
