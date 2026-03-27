package com.example.tripmanagerpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripmanagerpro.data.AppDatabase;
import com.example.tripmanagerpro.data.Excursion;
import com.example.tripmanagerpro.data.Vacation;
import com.example.tripmanagerpro.security.InputSanitizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetailActivity extends AppCompatActivity {

    private static final int MAX_TITLE_LENGTH = 50;

    private AppDatabase db;

    private EditText titleInput;
    private EditText dateInput;

    private int vacationId = -1;
    private int excursionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excursion Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);

        titleInput = findViewById(R.id.excursionTitleInput);
        dateInput = findViewById(R.id.excursionDateInput);

        Button saveButton = findViewById(R.id.saveExcursionButton);
        Button deleteButton = findViewById(R.id.deleteExcursionButton);
        Button alertButton = findViewById(R.id.alertExcursionButton);

        Intent intent = getIntent();
        vacationId = intent.getIntExtra("vacationId", -1);
        excursionId = intent.getIntExtra("excursionId", -1);

        if (vacationId == -1) {
            Toast.makeText(this, "Missing vacation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (excursionId != -1) {
            Excursion existing = db.excursionDao().getExcursionById(excursionId);
            if (existing != null) {
                titleInput.setText(existing.getTitle());
                dateInput.setText(existing.getDate());
            }
        }

        saveButton.setOnClickListener(v -> saveExcursion());
        deleteButton.setOnClickListener(v -> deleteExcursion());

        alertButton.setOnClickListener(v -> {
            if (excursionId == -1) {
                Toast.makeText(this, "Save excursion first", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = InputSanitizer.sanitizeText(titleInput.getText().toString(), MAX_TITLE_LENGTH);
            String dateStr = InputSanitizer.sanitizeDate(dateInput.getText().toString());

            long trigger = toTriggerMillis(dateStr);
            if (trigger == -1) {
                Toast.makeText(this, "Date must be MM/dd/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertHelper.schedule(
                    this,
                    trigger,
                    excursionId * 10 + 1,
                    "Excursion alert",
                    title + " is today"
            );

            Toast.makeText(this, "Excursion alert set", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveExcursion() {
        String rawTitle = titleInput.getText().toString();
        String rawDate = dateInput.getText().toString();

        String title = InputSanitizer.sanitizeText(rawTitle, MAX_TITLE_LENGTH);
        String dateStr = InputSanitizer.sanitizeDate(rawDate);

        titleInput.setText(title);
        dateInput.setText(dateStr);

        if (InputSanitizer.isBlank(title) || InputSanitizer.isBlank(dateStr)) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        Date excursionDate = parseDate(dateStr);
        if (excursionDate == null) {
            Toast.makeText(this, "Date must be MM/dd/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        Vacation vacation = db.vacationDao().getVacationById(vacationId);
        if (vacation == null) {
            Toast.makeText(this, "Vacation not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Date vacStart = parseDate(vacation.getStartDate());
        Date vacEnd = parseDate(vacation.getEndDate());

        if (vacStart == null || vacEnd == null) {
            Toast.makeText(this, "Vacation dates invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (excursionDate.before(vacStart) || excursionDate.after(vacEnd)) {
            Toast.makeText(this, "Excursion date must be within vacation dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (excursionId == -1) {
            Excursion e = new Excursion(vacationId, title, dateStr);
            long newId = db.excursionDao().insert(e);
            excursionId = (int) newId;
            Toast.makeText(this, "Excursion saved", Toast.LENGTH_SHORT).show();
        } else {
            Excursion e = new Excursion(vacationId, title, dateStr);
            e.setId(excursionId);
            db.excursionDao().update(e);
            Toast.makeText(this, "Excursion updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void deleteExcursion() {
        if (excursionId == -1) {
            finish();
            return;
        }

        Excursion e = db.excursionDao().getExcursionById(excursionId);
        if (e != null) {
            db.excursionDao().delete(e);
        }

        Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private Date parseDate(String input) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        sdf.setLenient(false);
        try {
            return sdf.parse(input);
        } catch (ParseException e) {
            return null;
        }
    }

    private long toTriggerMillis(String input) {
        Date date = parseDate(input);
        if (date == null) {
            return -1;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long trigger = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        if (trigger <= now) {
            return now + 60000;
        }

        return trigger;
    }
}