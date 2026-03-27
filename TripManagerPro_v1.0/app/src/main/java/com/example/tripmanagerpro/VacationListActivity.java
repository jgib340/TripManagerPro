package com.example.tripmanagerpro;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripmanagerpro.data.AppDatabase;
import com.example.tripmanagerpro.data.Vacation;
import com.example.tripmanagerpro.reports.BaseReportItem;
import com.example.tripmanagerpro.reports.VacationReportItem;
import com.example.tripmanagerpro.ui.VacationAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationListActivity extends AppCompatActivity {

    private AppDatabase db;
    private VacationAdapter adapter;
    private EditText searchVacationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vacations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);

        searchVacationInput = findViewById(R.id.searchVacationInput);
        Button viewReportButton = findViewById(R.id.viewReportButton);

        RecyclerView recyclerView = findViewById(R.id.vacationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VacationAdapter(vacation -> {
            Intent intent = new Intent(VacationListActivity.this, VacationDetailActivity.class);
            intent.putExtra("vacationId", vacation.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addVacationFab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(VacationListActivity.this, VacationDetailActivity.class);
            startActivity(intent);
        });

        viewReportButton.setOnClickListener(v -> showVacationReport());

        searchVacationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadVacations(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no action needed
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVacations(searchVacationInput.getText().toString().trim());
    }

    private void loadVacations(String query) {
        List<Vacation> vacations;

        if (query == null || query.trim().isEmpty()) {
            vacations = db.vacationDao().getAllVacations();
        } else {
            vacations = db.vacationDao().searchVacations(query.trim());
        }

        adapter.setVacations(vacations);
    }

    private void showVacationReport() {
        List<Vacation> vacations = db.vacationDao().getAllVacations();
        List<BaseReportItem> reportItems = new ArrayList<>();

        for (Vacation vacation : vacations) {
            reportItems.add(
                    new VacationReportItem(
                            vacation.getTitle(),
                            vacation.getHotel(),
                            vacation.getStartDate(),
                            vacation.getEndDate()
                    )
            );
        }

        String generatedAt = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US).format(new Date());

        StringBuilder report = new StringBuilder();
        report.append("Vacation Report\n");
        report.append("Generated: ").append(generatedAt).append("\n\n");
        report.append(String.format(Locale.US, "%-18s %-18s %-12s %-12s%n",
                "TITLE", "HOTEL", "START", "END"));
        report.append("----------------------------------------------------------------\n");

        if (reportItems.isEmpty()) {
            report.append("No vacations found.\n");
        } else {
            for (BaseReportItem item : reportItems) {
                report.append(item.formatForReportRow());
            }
        }

        TextView reportTextView = new TextView(this);
        reportTextView.setText(report.toString());
        reportTextView.setTypeface(Typeface.MONOSPACE);
        reportTextView.setTextSize(14);
        reportTextView.setPadding(32, 32, 32, 32);
        reportTextView.setTextIsSelectable(true);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(reportTextView);

        new AlertDialog.Builder(this)
                .setTitle("Vacation Report")
                .setView(scrollView)
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}