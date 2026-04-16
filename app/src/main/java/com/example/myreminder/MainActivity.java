package com.example.myreminder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ReminderAdapter.OnReminderListener {
    private CalendarView calendarView;
    private RecyclerView remindersList;
    private FloatingActionButton addBtn;
    private ImageButton aboutBtn, deleteAppBtn;
    private TextView emptyView;
    private ReminderDatabase database;
    private ReminderAdapter adapter;
    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        database = new ReminderDatabase(this);
        
        database.deleteOldReminders();

        selectedDate = dateFormat.format(new Date());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedDate = dateFormat.format(cal.getTime());
            loadReminders();
        });

        addBtn.setOnClickListener(v -> addReminder());
        aboutBtn.setOnClickListener(v -> openAbout());
        deleteAppBtn.setOnClickListener(v -> deleteApp());

        loadReminders();
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendar_view);
        remindersList = findViewById(R.id.reminders_list);
        addBtn = findViewById(R.id.add_btn);
        aboutBtn = findViewById(R.id.about_btn);
        deleteAppBtn = findViewById(R.id.delete_app_btn);
        emptyView = findViewById(R.id.empty_view);

        remindersList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadReminders() {
        List<Reminder> reminders = database.getRemindersByDate(selectedDate);
        
        if (reminders.isEmpty()) {
            emptyView.setVisibility(android.view.View.VISIBLE);
            remindersList.setVisibility(android.view.View.GONE);
        } else {
            emptyView.setVisibility(android.view.View.GONE);
            remindersList.setVisibility(android.view.View.VISIBLE);
        }

        boolean isPastDate = selectedDate.compareTo(dateFormat.format(new Date())) < 0;
        addBtn.setEnabled(!isPastDate);
        addBtn.setAlpha(isPastDate ? 0.5f : 1.0f);

        adapter = new ReminderAdapter(reminders, this, isPastDate);
        remindersList.setAdapter(adapter);
    }

    private void addReminder() {
        Intent intent = new Intent(this, ReminderEditActivity.class);
        intent.putExtra("date", selectedDate);
        startActivityForResult(intent, 1);
    }

    private void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void deleteApp() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onEdit(Reminder reminder) {
        Intent intent = new Intent(this, ReminderEditActivity.class);
        intent.putExtra("reminder", reminder);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDelete(Reminder reminder) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Удалить напоминание?")
                .setMessage(reminder.getTitle())
                .setPositiveButton("Удалить", (dialog, which) -> {
                    database.deleteReminder(reminder.getId());
                    loadReminders();
                    Toast.makeText(this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadReminders();
        }
    }
}
