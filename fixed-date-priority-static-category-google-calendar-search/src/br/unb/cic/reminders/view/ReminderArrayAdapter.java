package br.unb.cic.reminders.view;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
	private int rowType = NEXT_DAYS;
	public static final int LATE = 0;
	public static final int TODAY = 1;
	public static final int NEXT_DAYS = 2;
	public static final int NO_DATE = 3;

	public ReminderArrayAdapter(Context context, List<Reminder> objects) {
		super(context, R.layout.reminder_row, objects);
		this.context = context;
		this.rowColor = Color.BLACK;
		this.rowType = NEXT_DAYS;
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
		TextView tvDateFirst = (TextView) reminderRow.findViewById(R.id.txtDateFirst);
		TextView tvDateSecond = (TextView) reminderRow.findViewById(R.id.txtDateSecond);
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
		tvDateFirst.setTextColor(rowColor);
		tvDateFirst.setText(getDateFirst(position));
		tvDateSecond.setTextColor(rowColor);
		tvDateSecond.setText(getDateSecond(position));
		tvDone.setChecked(getItem(position).isDone());
		return reminderRow;
	}

	private String getDateFirst(int position) {
		if (getItem(position).getDate() == null) {
			return "";
		}
		String months[] = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
		String week[] = { "", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
		Calendar today = Calendar.getInstance();
		GregorianCalendar thatDay = new GregorianCalendar();
		thatDay.set(Integer.parseInt(getItem(position).getDate().substring(6, 10)),
				Integer.parseInt(getItem(position).getDate().substring(3, 5)) - 1,
				Integer.parseInt(getItem(position).getDate().substring(0, 2)));
		switch (rowType) {
		case LATE:
			long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
			long days = diff / (24 * 60 * 60 * 1000);
			if (days == 1)
				return "Yesterday";
			else
				return days + " days ago";
		case TODAY:
			return getDatesHour(position);
		case NEXT_DAYS:
			diff = thatDay.getTimeInMillis() - today.getTimeInMillis();
			days = diff / (24 * 60 * 60 * 1000);
			if (days == 1) {
				return getDatesHour(position);
			} else if (days < 6) {
				return week[thatDay.get(Calendar.DAY_OF_WEEK)];
			} else {
				return thatDay.get(Calendar.DAY_OF_MONTH) + " " + months[thatDay.get(Calendar.MONTH)];
			}
		default:
			break;
		}
		return getItem(position).getDate();
	}

	private String getDateSecond(int position) {
		switch (rowType) {
		case LATE:
			return getDatesHour(position);
		case TODAY:
			return "today";
		case NEXT_DAYS:
			Calendar today = Calendar.getInstance();
			GregorianCalendar thatDay = new GregorianCalendar();
			thatDay.set(Integer.parseInt(getItem(position).getDate().substring(6, 10)),
					Integer.parseInt(getItem(position).getDate().substring(3, 5)) - 1,
					Integer.parseInt(getItem(position).getDate().substring(0, 2)));
			long diff = thatDay.getTimeInMillis() - today.getTimeInMillis();
			long days = diff / (24 * 60 * 60 * 1000);
			if (days == 1)
				return "tomorrow";
			else
				return getDatesHour(position);
		default:
			break;
		}
		return getItem(position).getHour();
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
