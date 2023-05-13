package ck.dev.students.bloodhub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.BloodRequest;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.appointmentViewHolder> {

    private final ArrayList<BloodRequest> dataList;

    public RequestsAdapter(ArrayList<BloodRequest> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public appointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item_blood_requests, parent, false);
        return new appointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull appointmentViewHolder holder, int position) {
        BloodRequest request = dataList.get(position);

        holder.nameTxtView.setText(request.name);
        holder.contactTxtView.setText(request.number);
        holder.locationTxtView.setText(request.location);
        holder.bloodTypeView.setText(request.blood_group);
        holder.diagnoseTxtView.setText(request.diagnosis);
        holder.qtyTxtView.setText(request.quantity);

        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
        final String date = df.format(request.date);
        holder.dateTxtView.setText(date);

        holder.transportTxtView.setText((request.transport)? "Yes": "No");

        final String cDate = df.format(request.created_at);
        holder.createdDateTxtView.setText(cDate);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class appointmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTxtView;
        private final TextView contactTxtView;
        private final TextView locationTxtView;
        private final TextView diagnoseTxtView;
        private final TextView qtyTxtView;
        private final TextView dateTxtView;
        private final TextView transportTxtView;
        private final TextView createdDateTxtView;
        private final TextView bloodTypeView;

        public appointmentViewHolder(View itemView) {
            super(itemView);
            nameTxtView        = itemView.findViewById(R.id.name_view);
            dateTxtView        = itemView.findViewById(R.id.date_view);
            transportTxtView   = itemView.findViewById(R.id.transport_view);
            createdDateTxtView = itemView.findViewById(R.id.created_at_view);
            bloodTypeView      = itemView.findViewById(R.id.blood_type_view);
            contactTxtView      = itemView.findViewById(R.id.contact_view);
            locationTxtView      = itemView.findViewById(R.id.location_view);
            diagnoseTxtView      = itemView.findViewById(R.id.diagnosis_view);
            qtyTxtView      = itemView.findViewById(R.id.qty_view);
        }

    }
    
}
