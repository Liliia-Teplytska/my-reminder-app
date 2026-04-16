package com.example.myreminder;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ReminderDatabase {
    private static final String PREFS_NAME = "reminders_db";
    private static final String REMINDERS_KEY = "reminders_list";
    private SharedPreferences prefs;
    private Gson gson;

    public ReminderDatabase(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void addReminder(Reminder reminder) {
        List<Reminder> reminders = getAllReminders();
        reminders.add(reminder);
        saveReminders(reminders);
    }

    public void updateReminder(Reminder reminder) {
        List<Reminder> reminders = getAllReminders();
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getId().equals(reminder.getId())) {
                reminders.set(i, reminder);
                break;
            }
        }
        saveReminders(reminders);
    }

    public void deleteReminder(String reminderId) {
        List<Reminder> reminders = getAllReminders();
        reminders.removeIf(r -> r.getId().equals(reminderId));
        saveReminders(reminders);
    }

    public List<Reminder> getRemindersByDate(String date) {
        List<Reminder> allReminders = getAllReminders();
        List<Reminder> filtered = new ArrayList<>();
        for (Reminder r : allReminders) {
            if (r.getDate().equals(date)) {
                filtered.add(r);
            }
        }
        Collections.sort(filtered, (a, b) -> a.getTime().compareTo(b.getTime()));
        return filtered;
    }

    public List<Reminder> getAllReminders() {
        String json = prefs.getString(REMINDERS_KEY, "[]");
        Type type = new TypeToken<List<Reminder>>(){}.getType();
        List<Reminder> reminders = gson.fromJson(json, type);
        return reminders != null ? reminders : new ArrayList<>();
    }

    public void deleteOldReminders() {
        List<Reminder> reminders = getAllReminders();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        String threeMonthsAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        reminders.removeIf(r -> r.getDate().compareTo(threeMonthsAgo) < 0);
        saveReminders(reminders);
    }

    private void saveReminders(List<Reminder> reminders) {
        String json = gson.toJson(reminders);
        prefs.edit().putString(REMINDERS_KEY, json).apply();
    }
}
