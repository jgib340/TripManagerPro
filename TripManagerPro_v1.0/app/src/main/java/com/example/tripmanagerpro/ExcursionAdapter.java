package com.example.tripmanagerpro.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripmanagerpro.R;
import com.example.tripmanagerpro.data.Excursion;

import java.util.ArrayList;
import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    public interface OnExcursionClickListener {
        void onExcursionClick(Excursion excursion);
    }

    private final OnExcursionClickListener listener;
    private final List<Excursion> excursions = new ArrayList<>();

    public ExcursionAdapter(OnExcursionClickListener listener) {
        this.listener = listener;
    }

    public void setExcursions(List<Excursion> list) {
        excursions.clear();
        excursions.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_excursion, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion e = excursions.get(position);
        holder.titleText.setText(e.getTitle());
        holder.dateText.setText(e.getDate());
        holder.itemView.setOnClickListener(v -> listener.onExcursionClick(e));
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }

    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView dateText;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.excursionTitleText);
            dateText = itemView.findViewById(R.id.excursionDateText);
        }
    }
}