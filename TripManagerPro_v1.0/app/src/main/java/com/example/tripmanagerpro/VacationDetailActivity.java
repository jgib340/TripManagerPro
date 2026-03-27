package com.example.tripmanagerpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripmanagerpro.data.AppDatabase;
import com.example.tripmanagerpro.data.Vacation;
import com.example.tripmanagerpro.security.InputSanitizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationDetailActivity extends AppCompatActivity {

    private static final int MAX_TITLE_LENGTH = 50;
    private static final int MAX_HOTEL_LENGTH = 50;

    private AppDatabase db;

    private EditText titleInput;
    private EditText hotelInput;
    private EditText startDateInput;
    private EditText endDateInput;

    private int vacationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vacation Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);

        titleInput = findViewById(R.id.titleInput);
        hotelInput = findViewById(R.id.hotelInput);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);

        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button shareButton = findViewById(R.id.shareButton);
        Button excursionsButton = findViewById(R.id.excursionsButton);
        Button alertStartButton = findViewById(R.id.alertStartButton);
        Button alertEndButton = findViewById(R.id.alertEndButton);

        Intent intent = getIntent();
        vacationId = intent.getIntExtra("vacationId", -1);

        if (vacationId != -1) {
            Vacation existing = db.vacationDao().getVacationById(vacationId);
            if (existing != null) {
                titleInput.setText(existing.getTitle());
                hotelInput.setText(existing.getHotel());
                startDateInput.setText(existing.getStartDate());
                endDateInput.setText(existing.getEndDate());
            }
        }

        saveButton.setOnClickListener(v -> saveVacation());
        deleteButton.setOnClickListener(v -> deleteVacation());
        shareButton.setOnClickListener(v -> shareVacation());

        excursionsButton.setOnClickListener(v -> {
            if (vacationId == -1) {
                Toast.makeText(this, "Save vacation first", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent excursionIntent = new Intent(VacationDetailActivity.this, ExcursionListActivity.class);
            excursionIntent.putExtra("vacationId", vacationId);
            startActivity(excursionIntent);
        });

        alertStartButton.setOnClickListener(v -> {
            if (vacationId == -1) {
                Toast.makeText(this, "Save vacation first", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = InputSanitizer.sanitizeText(titleInput.getText().toString(), MAX_TITLE_LENGTH);
            String start = InputSanitizer.sanitizeDate(startDateInput.getText().toString());

            long trigger = toTriggerMillis(start);
            if (trigger == -1) {
                Toast.makeText(this, "Enter a valid start date first", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertHelper.schedule(
                    this,
                    trigger,
                    vacationId * 10 + 1,
                    "Vacation starts",
                    title + " starts today"
            );

            Toast.makeText(this, "Start date alert set", Toast.LENGTH_SHORT).show();
        });

        alertEndButton.setOnClickListener(v -> {
            if (vacationId == -1) {
                Toast.makeText(this, "Save vacation first", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = InputSanitizer.sanitizeText(titleInput.getText().toString(), MAX_TITLE_LENGTH);
            String end = InputSanitizer.sanitizeDate(endDateInput.getText().toString());

            long trigger = toTriggerMillis(end);
            if (trigger == -1) {
                Toast.makeText(this, "Enter a valid end date first", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertHelper.schedule(
                    this,
                    trigger,
                    vacationId * 10 + 2,
                    "Vacation ends",
                    title + " ends today"
            );

            Toast.makeText(this, "End date alert set", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveVacation() {

        String rawTitle = titleInput.getText().toString();
        String rawHotel = hotelInput.getText().toString();
        String rawStart = startDateInput.getText().toString();
        String rawEnd = endDateInput.getText().toString();

        String title = InputSanitizer.sanitizeText(rawTitle, MAX_TITLE_LENGTH);
        String hotel = InputSanitizer.sanitizeText(rawHotel, MAX_HOTEL_LENGTH);
        String start = InputSanitizer.sanitizeDate(rawStart);
        String end = InputSanitizer.sanitizeDate(rawEnd);

        titleInput.setText(title);
        hotelInput.setText(hotel);
        startDateInput.setText(start);
        endDateInput.setText(end);

        if (InputSanitizer.isBlank(title) ||
                InputSanitizer.isBlank(hotel) ||
                InputSanitizer.isBlank(start) ||
                InputSanitizer.isBlank(end)) {

            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Date startDate = parseDate(start);
        Date endDate = parseDate(end);

        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Dates must be MM/dd/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate.before(startDate)) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vacationId == -1) {
            Vacation newVacation = new Vacation(title, hotel, start, end);
            long id = db.vacationDao().insert(newVacation);
            vacationId = (int) id;

            Toast.makeText(this, "Vacation saved", Toast.LENGTH_SHORT).show();
        } else {
            Vacation existing = db.vacationDao().getVacationById(vacationId);

            if (existing != null) {
                existing.setTitle(title);
                existing.setHotel(hotel);
                existing.setStartDate(start);
                existing.setEndDate(end);

                db.vacationDao().update(existing);

                Toast.makeText(this, "Vacation updated", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void deleteVacation() {

        if (vacationId == -1) {
            finish();
            return;
        }

        int excursionCount = db.excursionDao().countExcursionsForVacation(vacationId);

        if (excursionCount > 0) {
            Toast.makeText(this, "Cannot delete vacation with excursions", Toast.LENGTH_SHORT).show();
            return;
        }

        Vacation existing = db.vacationDao().getVacationById(vacationId);

        if (existing != null) {
            db.vacationDao().delete(existing);
        }

        Toast.makeText(this, "Vacation deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void shareVacation() {

        String title = InputSanitizer.sanitizeText(titleInput.getText().toString(), MAX_TITLE_LENGTH);
        String hotel = InputSanitizer.sanitizeText(hotelInput.getText().toString(), MAX_HOTEL_LENGTH);
        String start = InputSanitizer.sanitizeDate(startDateInput.getText().toString());
        String end = InputSanitizer.sanitizeDate(endDateInput.getText().toString());

        String shareText =
                "Vacation: " + title +
                        "\nHotel: " + hotel +
                        "\nStart Date: " + start +
                        "\nEnd Date: " + end;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(intent, "Share Vacation"));
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