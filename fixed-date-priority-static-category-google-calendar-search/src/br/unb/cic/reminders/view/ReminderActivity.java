package br.unb.cic.reminders.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.unb.cic.reminders.model.InvalidDateException;
import br.unb.cic.reminders.model.InvalidTextException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;
import br.unb.cic.reminders.model.Category;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Priority;
import java.util.Arrays;
import br.unb.cic.reminders.calendar.CalendarEventCreator;
import br.unb.cic.reminders.model.CalendarNotFoundException;
import android.widget.CheckBox;

public abstract class ReminderActivity extends Activity {
	protected Reminder reminder;
	protected Calendar date;
	protected Spinner spinnerDate;
	protected EditText edtDate;
	protected Calendar time;
	protected EditText edtTime;
	protected Spinner spinnerTime;
	protected EditText edtReminder, edtDetails;
	private Button btnSave, btnCancel;
	private CalendarEventCreator creator;
	private CheckBox cbCalendar;

	protected Spinner spinnerCategory;

	private void addListenerToSpinnerCategory() {
		spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
			}

			public void onNothingSelected(AdapterView<? extends Object> parent) {

			}
		});
	}

	private Spinner getSpinnerCategory() throws Exception {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);

		SpinnerAdapterGenerator<Category> adapterCategoryGenerator = new SpinnerAdapterGenerator<Category>();

		List<Category> categories = getCategories();

		ArrayAdapter<Category> adapter = adapterCategoryGenerator.getSpinnerAdapter(categories, this);

		spinner.setAdapter(adapter);

		return spinner;
	}

	protected List<Category> getCategories() throws Exception {
		return Controller.instance(getApplicationContext()).listCategories();
	}

	protected Spinner getSpinnerCategory(List<Category> categories) throws Exception {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);

		SpinnerAdapterGenerator<Category> adapterCategoryGenerator = new SpinnerAdapterGenerator<Category>();

		spinner.setAdapter(adapterCategoryGenerator.getSpinnerAdapter(categories, this));

		return spinner;
	}

	private Priority selectedPriority;
	protected Spinner spinnerPriority;

	private void addListenerToSpinnerPriority() {
		spinnerPriority.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				selectedPriority = (Priority) parent.getItemAtPosition(pos);
			}

			public void onNothingSelected(AdapterView<? extends Object> parent) {

			}
		});
	}

	private Spinner getSpinnerPriority() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);

		SpinnerAdapterGenerator<Priority> adapterPriorityGenerator = new SpinnerAdapterGenerator<Priority>();

		List<Priority> priorityValues = Arrays.asList(Priority.values());

		ArrayAdapter<Priority> priorityArrayAdapter = adapterPriorityGenerator.getSpinnerAdapter(priorityValues, this);

		spinner.setAdapter(priorityArrayAdapter);

		spinner.setSelection(Priority.NORMAL.getCode());

		return spinner;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_add);
		if (reminder == null)
			reminder = new Reminder();
		initializeFields();
		initializeListeners();
		initializeValues();
	}

	private void initializeFields() {
		btnSave = (Button) findViewById(R.id.btnSave);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		edtReminder = (EditText) findViewById(R.id.edtReminder);
		edtDetails = (EditText) findViewById(R.id.edtDetails);
		spinnerDate = getSpinnerDate();
		spinnerTime = getSpinnerTime();
		try {
			spinnerCategory = getSpinnerCategory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		spinnerPriority = getSpinnerPriority();
		cbCalendar = (CheckBox) findViewById(R.id.cbCalendar);
	}

	private void initializeListeners() {
		addListenerToBtnSave();
		addListenerToBtnCancel();
		addListenerToSpinnerDate();
		addListenerToSpinnerTime();
		addListenerToSpinnerCategory();
		addListenerToSpinnerPriority();
	}

	protected abstract void initializeValues();

	private void addListenerToBtnSave() {
		btnSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					createReminder();
					persist(reminder);
					finish();
				} catch (Exception e) {
					Log.e("ReminderActivity", e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	private void addListenerToBtnCancel() {
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void addListenerToSpinnerDate() {
		spinnerDate.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerDate = getSpinnerDate();
				return false;
			}
		});
		spinnerDate.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerDate = getSpinnerDate();
				return false;
			}
		});
		spinnerDate.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				switch (pos) {
				case 0:
					date = null;
					break;
				case 1:
					if (date == null)
						date = Calendar.getInstance();
					DialogFragment newFragment = new DatePickerDialogFragment(date, spinnerDate);
					newFragment.show(getFragmentManager(), "datePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {
			}
		});

	}

	private void addListenerToSpinnerTime() {
		spinnerTime.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				spinnerTime = getSpinnerTime();
				return false;
			}
		});
		spinnerTime.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				spinnerTime = getSpinnerTime();
				return false;
			}
		});
		spinnerTime.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				switch (pos) {
				case 0:
					time = null;
					break;
				case 1:
					if (time == null)
						time = Calendar.getInstance();
					DialogFragment newFragment = new TimePickerDialogFragment(time, spinnerTime);
					newFragment.show(getFragmentManager(), "timePicker");
					break;
				default:
				}
			}

			public void onNothingSelected(AdapterView<? extends Object> arg0) {
			}
		});

	}

	private void createReminder() {
		try {
			reminder.setText(edtReminder.getText().toString());
			reminder.setDetails(edtDetails.getText().toString());
			setValuesOnReminder();
		} catch (InvalidTextException e) {
			Toast.makeText(getApplicationContext(), "Invalid text.", Toast.LENGTH_SHORT).show();
		} catch (InvalidDateException e) {
			Toast.makeText(getApplicationContext(), "Invalid date.", Toast.LENGTH_SHORT).show();
		} catch (InvalidHourException e) {
			Toast.makeText(getApplicationContext(), "Invalid time.", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Serious error.", Toast.LENGTH_SHORT).show();
		}
	}

	private void setValuesOnReminder() throws Exception {
		reminder.setDate(dateToString());
		reminder.setHour(timeToString());
		reminder.setCategory((Category) spinnerCategory.getSelectedItem());
		reminder.setPriority(selectedPriority);
		if (cbCalendar.isChecked()) {
			creator = new CalendarEventCreator();
			creator.addEventCalendar(reminder, getApplicationContext());
		}
	}

	private String dateToString(
	) {
		if (date == null)
			return null;
		String sDate;
		sDate = Integer.toString(date.get(Calendar.MONTH) + 1);
		if (date.get(Calendar.MONTH) + 1 < 10)
			sDate = "0" + sDate;
		sDate = Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + "-" + sDate;
		if (date.get(Calendar.DAY_OF_MONTH) < 10)
			sDate = "0" + sDate;
		sDate += "-" + Integer.toString(date.get(Calendar.YEAR));
		return sDate;
	}

	private String timeToString(
	) {
		if (time == null)
			return null;
		String sTime;
		sTime = Integer.toString(time.get(Calendar.MINUTE));
		if (time.get(Calendar.MINUTE) < 10)
			sTime = "0" + sTime;
		sTime = Integer.toString(time.get(Calendar.HOUR_OF_DAY)) + ":" + sTime;
		if (time.get(Calendar.HOUR_OF_DAY) < 10)
			sTime = "0" + sTime;
		return sTime;
	}

	protected void updateDateFromString(String sDate
	) {
		if (sDate == null || sDate.equals("")) {
			date = null;
			return;
		}
		char sDay[] = { sDate.charAt(0), sDate.charAt(1) };
		int day = Integer.parseInt(new String(sDay), 10);
		char sMonth[] = { sDate.charAt(3), sDate.charAt(4) };
		int month = Integer.parseInt(new String(sMonth), 10);
		char sYear[] = { sDate.charAt(6), sDate.charAt(7), sDate.charAt(8), sDate.charAt(9) };
		int year = Integer.parseInt(new String(sYear), 10);

		if (date == null)
			date = Calendar.getInstance();
		date.set(year, month - 1, day);

	}

	protected void updateTimeFromString(String sTime
	) {
		if (sTime == null || sTime.equals("")) {
			time = null;
			return;
		}
		char sHour[] = { sTime.charAt(0), sTime.charAt(1) };
		int hour = Integer.parseInt(new String(sHour), 10);
		char sMinute[] = { sTime.charAt(3), sTime.charAt(4) };
		int minute = Integer.parseInt(new String(sMinute), 10);

		if (time == null)
			time = Calendar.getInstance();
		time.set(Calendar.MINUTE, minute);
		time.set(Calendar.HOUR_OF_DAY, hour);

	}

	@SuppressWarnings("unchecked")
	protected void updateSpinnerDateHour(Spinner spinner, String dateOrHour) {
		if (dateOrHour == null)
			return;
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
		int count = adapter.getCount();
		if (count > 2) {
			for (int i = 2; i < count; ++i)
				adapter.remove(adapter.getItem(i));
		}
		adapter.add(dateOrHour);
		spinner.setSelection(2);
	}

	private Spinner getSpinnerDate() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerDate);
		SpinnerAdapterGenerator<String> adapterDateGenerator = new SpinnerAdapterGenerator<String>();
		List<String> items = new ArrayList<String>();
		items.add("No date");
		items.add("+ Select");
		spinner.setAdapter(adapterDateGenerator.getSpinnerAdapter(items, this));
		return spinner;
	}

	private Spinner getSpinnerTime() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerTime);
		SpinnerAdapterGenerator<String> adapterTimeGenerator = new SpinnerAdapterGenerator<String>();
		List<String> items = new ArrayList<String>();
		items.add("No time");
		items.add("+ Select");
		spinner.setAdapter(adapterTimeGenerator.getSpinnerAdapter(items, this));
		return spinner;
	}

	protected abstract void persist(Reminder reminder);
}
