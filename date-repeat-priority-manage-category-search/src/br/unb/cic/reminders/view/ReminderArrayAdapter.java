package br.unb.cic.reminders.view;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;
import android.widget.ImageView;
import android.graphics.Typeface;

public class ReminderArrayAdapter extends ArrayAdapter<Reminder> {
	private Context context;
	private int rowColor = Color.BLACK;
	private int rowType = MONDAY;
	public static final int MONDAY = 0;
	public static final int TUESDAY = 1;
	public static final int WEDNESDAY = 2;
	public static final int THURSDAY = 3;
	public static final int FRIDAY = 4;
	public static final int SATURDAY = 5;
	public static final int SUNDAY = 6;

	public ReminderArrayAdapter(Context context, List<Reminder> objects) {
		super(context, R.layout.reminder_row, objects);
		this.context = context;
		this.rowColor = Color.BLACK;
		this.rowType = MONDAY;
	}

	public ReminderArrayAdapter(Context context, List<Reminder> objects, int rowColor, int rowType) {
		super(context, R.layout.reminder_row, objects);
		this.context = context;
		this.rowColor = rowColor;
		this.rowType = rowType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout reminderRow;
		if (convertView == null) {
			reminderRow = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(R.layout.reminder_row, reminderRow, true);
		} else {
			reminderRow = (LinearLayout) convertView;
		}
		ImageView ivPriority = (ImageView) reminderRow.findViewById(R.id.ivPriority);
		TextView tvReminder = (TextView) reminderRow.findViewById(R.id.txtReminder);
		TextView tvHour = (TextView) reminderRow.findViewById(R.id.txtHour);
		CheckBox tvDone = (CheckBox) reminderRow.findViewById(R.id.cbDone);
		tvDone.setTag(position);
		tvDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				try {
					Reminder reminder = getItem((Integer) buttonView.getTag());
					reminder.setDone(isChecked);
					Controller.instance(getContext()).updateReminder(reminder);
				} catch (DBException e) {
					e.printStackTrace();
				}
			}
		});
		if (getItem(position).getPriority() == 1)
			ivPriority.setImageResource(R.drawable.important);
		else if (getItem(position).getPriority() == 2)
			ivPriority.setImageResource(R.drawable.urgent);

		if (getItem(position).getPriority() != 0)
			tvReminder.setTypeface(null, Typeface.BOLD);
		tvReminder.setTextColor(rowColor);
		tvReminder.setText(getItem(position).getText());
		tvHour.setTextColor(rowColor);
		tvHour.setText(getDatesHour(position));
		tvDone.setChecked(getItem(position).isDone());
		return reminderRow;
	}

	private String getDatesHour(int position) {
		if (getItem(position).getHour() == null) {
			return "";
		}
		if (getItem(position).getHour().substring(3, 5) != "00")
			return getItem(position).getHour().substring(0, 2) + "h" + getItem(position).getHour().substring(3, 5);
		else
			return getItem(position).getHour().substring(0, 2) + "h";
	}

	public int getRowColor() {
		return rowColor;
	}

	public void setRowColor(int rowColor) {
		this.rowColor = rowColor;
	}

	public int getRowType() {
		return rowType;
	}

	public void setRowType(int rowType) {
		this.rowType = rowType;
	}
}
