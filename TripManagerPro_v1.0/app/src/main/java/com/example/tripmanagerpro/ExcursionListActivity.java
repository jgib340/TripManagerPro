package com.example.tripmanagerpro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripmanagerpro.data.AppDatabase;
import com.example.tripmanagerpro.data.Excursion;
import com.example.tripmanagerpro.ui.ExcursionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExcursionListActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExcursionAdapter adapter;
    private int vacationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excursions");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        vacationId = getIntent().getIntExtra("vacationId", -1);

        db = AppDatabase.getInstance(this);

        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExcursionAdapter(excursion -> {
            Intent intent = new Intent(ExcursionListActivity.this, ExcursionDetailActivity.class);
            intent.putExtra("vacationId", vacationId);
            intent.putExtra("excursionId", excursion.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addExcursionFab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ExcursionListActivity.this, ExcursionDetailActivity.class);
            intent.putExtra("vacationId", vacationId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Excursion> list = db.excursionDao().getExcursionsForVacation(vacationId);
        adapter.setExcursions(list);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}