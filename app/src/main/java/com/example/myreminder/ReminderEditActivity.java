package com.example.myreminder;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ReminderEditActivity extends AppCompatActivity {
    private EditText titleInput;
    private TextView timeView;
    private Spinner repetitionSpinner;
    private Button saveBtn, deleteBtn;
    private ReminderDatabase database;
    private Reminder currentReminder;
    private String selectedTime;
    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_edit);

        initViews();
        database = new ReminderDatabase(this);

        Intent intent = getIntent();
        if (intent.hasExtra("reminder")) {
            currentReminder = (Reminder) intent.getSerializableExtra("reminder");
            loadReminderData();
            deleteBtn.setVisibility(android.view.View.VISIBLE);
        } else {
            selectedDate = intent.getStringExtra("date");
            selectedTime = timeFormat.format(new Date());
            timeView.setText(selectedTime);
            deleteBtn.setVisibility(android.view.View.GONE);
        }

        saveBtn.setOnClickListener(v -> saveReminder());
        deleteBtn.setOnClickListener(v -> deleteReminder());
        timeView.setOnClickListener(v -> showTimePicker());
    }

    private void initViews() {
        titleInput = findViewById(R.id.title_input);
        timeView = findViewById(R.id.time_view);
        repetitionSpinner = findViewById(R.id.repetition_spinner);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        String[] repetitions = {"Без повторения", "Каждые 10 минут", "Каждые 30 минут", 
                                "Каждые 2 часа", "Каждые 4 часа"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, repetitions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repetitionSpinner.setAdapter(adapter);
    }

    private void loadReminderData() {
        titleInput.setText(currentReminder.getTitle());
        timeView.setText(currentReminder.getTime());
        selectedTime = currentReminder.getTime();
        selectedDate = currentReminder.getDate();

        String[] repetitionValues = {"none", "10min", "30min", "2hour", "4hour"};
        for (int i = 0; i < repetitionValues.length; i++) {
            if (repetitionValues[i].equals(currentReminder.getRepetition())) {
                repetitionSpinner.setSelection(i);
                break;
            }
        }
    }

    private void showTimePicker() {
        String[] time = selectedTime.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            if (selectedDate.equals(dateFormat.format(new Date()))) {
                Calendar now = Calendar.getInstance();
                if (hourOfDay < now.get(Calendar.HOUR_OF_DAY) || 
                    (hourOfDay == now.get(Calendar.HOUR_OF_DAY) && minute1 < now.get(Calendar.MINUTE))) {
                    Toast.makeText(this, "Время не может быть в прошлом", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            timeView.setText(selectedTime);
        }, hour, minute, true);
        dialog.show();
    }

    private void saveReminder() {
        String title = titleInput.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название события", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.length() > 50) {
            Toast.makeText(this, "Максимум 50 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] repetitionValues = {"none", "10min", "30min", "2hour", "4hour"};
        String repetition = repetitionValues[repetitionSpinner.getSelectedItemPosition()];

        if (currentReminder != null) {
            currentReminder.setTitle(title);
            currentReminder.setTime(selectedTime);
            currentReminder.setRepetition(repetition);
            database.updateReminder(currentReminder);
        } else {
            Reminder reminder = new Reminder(
                    UUID.randomUUID().toString(),
                    title,
                    selectedDate,
                    selectedTime,
                    repetition
            );
            database.addReminder(reminder);
        }

        setResult(RESULT_OK);
        finish();
    }

    private void deleteReminder() {
        if (currentReminder != null) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Удалить?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        database.deleteReminder(currentReminder.getId());
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }
    }
}
