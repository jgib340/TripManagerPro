package com.example.tripmanagerpro.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripmanagerpro.R;
import com.example.tripmanagerpro.data.Vacation;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    public interface OnVacationClickListener {
        void onVacationClick(Vacation vacation);
    }

    private final OnVacationClickListener listener;
    private final List<Vacation> vacations = new ArrayList<>();

    public VacationAdapter(OnVacationClickListener listener) {
        this.listener = listener;
    }

    public void setVacations(List<Vacation> vacationList) {
        vacations.clear();
        vacations.addAll(vacationList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacations.get(position);
        holder.titleText.setText(vacation.getTitle());
        holder.datesText.setText(
                holder.itemView.getContext().getString(
                        R.string.vacation_dates,
                        vacation.getStartDate(),
                        vacation.getEndDate()
                )
        );
        holder.itemView.setOnClickListener(v -> listener.onVacationClick(vacation));
    }

    @Override
    public int getItemCount() {
        return vacations.size();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView datesText;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.vacationTitleText);
            datesText = itemView.findViewById(R.id.vacationDatesText);
        }
    }
}