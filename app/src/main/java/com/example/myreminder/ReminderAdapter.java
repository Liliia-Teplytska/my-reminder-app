package com.example.myreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;
    private OnReminderListener listener;
    private boolean isPastDate;

    public interface OnReminderListener {
        void onEdit(Reminder reminder);
        void onDelete(Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminders, OnReminderListener listener, boolean isPastDate) {
        this.reminders = reminders;
        this.listener = listener;
        this.isPastDate = isPastDate;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder, isPastDate);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, timeView;
        ImageButton editBtn, deleteBtn;

        ReminderViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.reminder_title);
            timeView = itemView.findViewById(R.id.reminder_time);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        void bind(Reminder reminder, boolean isPastDate) {
            titleView.setText(reminder.getTitle());
            timeView.setText(reminder.getTime());

            if (isPastDate) {
                editBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                editBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }

            editBtn.setOnClickListener(v -> listener.onEdit(reminder));
            deleteBtn.setOnClickListener(v -> listener.onDelete(reminder));
        }
    }
}
