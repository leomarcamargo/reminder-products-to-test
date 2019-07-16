package br.unb.cic.reminders.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.InvalidDateException;
import br.unb.cic.reminders.model.InvalidFormatException;
import br.unb.cic.reminders.model.InvalidTextException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;
import br.unb.cic.reminders.model.Category;
import android.widget.Spinner;
import java.util.List;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import br.unb.cic.reminders.model.Priority;
import java.util.Arrays;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import br.unb.cic.reminders.model.InvalidDaysException;

public class ReminderAddActivity extends Activity {
	private EditText edtReminder, edtDetails;
	private EditText edtHour;
	private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
	private Button btnSave, btnCancel;
	private boolean editingReminder;
	private Long previewReminderId;

	private Category selectedCategory;
	private Spinner spinnerCategory;

	private void addListenerToSpinnerCategory() {
		spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<? extends Object> parent, View view, int pos, long id) {
				selectedCategory = (Category) parent.getItemAtPosition(pos);
			}

			public void onNothingSelected(AdapterView<? extends Object> parent) {

			}
		});
	}

	private Spinner getSpinnerCategory() throws Exception {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);

		SpinnerAdapterGenerator<Category> adapterCategoryGenerator = new SpinnerAdapterGenerator<Category>();

		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();

		spinner.setAdapter(adapterCategoryGenerator.getSpinnerAdapter(categories, this));

		return spinner;
	}

	private int categoryToIndex(Category category) throws Exception {
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		int i = 0;
		for (Category c : categories) {
			if (c.getName().equals(category.getName())) {
				return i;
			}
			i++;
		}
		return 0;
	}

	private Priority selectedPriority;
	private Spinner spinnerPriority;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_add);
		Reminder existingReminder = getReminderFromIntent();
		if (existingReminder == null) {
			editingReminder = true;
			Reminder editReminder = getExistingReminder();
			initialize(editReminder);
		} else {
			editingReminder = false;
			initialize(existingReminder);
		}
		configureActionListener();
	}

	private void configureActionListener() {
		addListenerToBtnSave();
		addListenerToBtnCancel();
		addListenerToSpinnerCategory();
		addListenerToSpinnerPriority();
	}

	private void addListenerToBtnSave() {
		btnSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Reminder reminder = createReminder();
					if (editingReminder) {
						reminder.setId(previewReminderId);
						Controller.instance(getApplicationContext()).updateReminder(reminder);
					} else {
						Controller.instance(getApplicationContext()).addReminder(reminder);
					}
					finish();
				} catch (Exception e) {
					Log.e("ReminderAddActivity", e.getMessage());
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

	private Reminder createReminder() {
		Reminder reminder = new Reminder();
		try {
			reminder = createReminderAux();
			reminder.setText(edtReminder.getText().toString());
			reminder.setDetails(edtDetails.getText().toString());
		} catch (InvalidTextException e) {
			Toast.makeText(getApplicationContext(), "Invalid text.", Toast.LENGTH_SHORT).show();
		} catch (InvalidDateException e) {
			Toast.makeText(getApplicationContext(), "Invalid date.", Toast.LENGTH_SHORT).show();
		} catch (InvalidHourException e) {
			Toast.makeText(getApplicationContext(), "Invalid time.", Toast.LENGTH_SHORT).show();
		}
		return reminder;
	}

	private Reminder createReminderAux() {
		Reminder reminder = new Reminder();
		reminder.setHour(edtHour.getText().toString());
		reminder.setMonday(cbMonday.isChecked());
		reminder.setTuesday(cbTuesday.isChecked());
		reminder.setWednesday(cbWednesday.isChecked());
		reminder.setThursday(cbThursday.isChecked());
		reminder.setFriday(cbFriday.isChecked());
		reminder.setSaturday(cbSaturday.isChecked());
		try {
			reminder.setSunday(cbSunday.isChecked());
		} catch (InvalidDaysException e) {
			Toast.makeText(getApplicationContext(), "At least one day should be checked.", Toast.LENGTH_SHORT).show();
		}
		reminder.setCategory(selectedCategory);
		reminder.setPriority(selectedPriority);
		return reminder;
	}

	private Reminder getExistingReminder() {
		Reminder reminder = null;
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
			previewReminderId = intent.getLongExtra("id", 0);
			String text = intent.getStringExtra("text");
			reminder = createReminderExisting(intent);
			reminder.setText(text);
			reminder.setId(previewReminderId);
		}
		return reminder;
	}

	private Reminder createReminderExisting(Intent intent) {
		Reminder reminder = new Reminder();
		String hour = intent.getStringExtra("hour");
		reminder.setHour(hour);
		boolean monday = intent.getBooleanExtra("monday", false);
		boolean tuesday = intent.getBooleanExtra("tuesday", false);
		boolean wednesday = intent.getBooleanExtra("wednesday", false);
		boolean thursday = intent.getBooleanExtra("thursday", false);
		boolean friday = intent.getBooleanExtra("friday", false);
		boolean saturday = intent.getBooleanExtra("saturday", false);
		boolean sunday = intent.getBooleanExtra("sunday", false);
		reminder.setMonday(monday);
		reminder.setTuesday(tuesday);
		reminder.setWednesday(wednesday);
		reminder.setThursday(thursday);
		reminder.setFriday(friday);
		reminder.setSaturday(saturday);
		reminder.setSunday(sunday);
		String categoryName = intent.getStringExtra("category_name");
		String categoryId = intent.getStringExtra("category_id");
		Category category = new Category();
		category.setName(categoryName);
		category.setId(Long.parseLong(categoryId));
		reminder.setCategory(category);
		String priority = intent.getStringExtra("priority");
		reminder.setPriority(Priority.fromCode(Integer.parseInt(priority, 10)));
		return reminder;
	}

	private Reminder getReminderFromIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if ("br.com.positivo.reminders.ADD_REMINDER".equals(action) && "text/plain".equals(type)) {
			try {
				String text = intent.getStringExtra("text");
				String details = intent.getStringExtra("details");
				Reminder reminder = createReminderIntent(intent);
				reminder.setText(text);
				reminder.setDetails(details);
				return reminder;
			} catch (InvalidFormatException e) {
			}
		}
		return null;
	}

	private Reminder createReminderIntent(Intent intent) {
		Reminder reminder = new Reminder();
		String hour = intent.getStringExtra("hour");
		reminder.setHour(hour);
		boolean monday = intent.getBooleanExtra("monday", false);
		boolean tuesday = intent.getBooleanExtra("tuesday", false);
		boolean wednesday = intent.getBooleanExtra("wednesday", false);
		boolean thursday = intent.getBooleanExtra("thursday", false);
		boolean friday = intent.getBooleanExtra("friday", false);
		boolean saturday = intent.getBooleanExtra("saturday", false);
		boolean sunday = intent.getBooleanExtra("sunday", false);
		reminder.setMonday(monday);
		reminder.setTuesday(tuesday);
		reminder.setWednesday(wednesday);
		reminder.setThursday(thursday);
		reminder.setFriday(friday);
		reminder.setSaturday(saturday);
		reminder.setSunday(sunday);
		String category = intent.getStringExtra("category");
		Category auxCategory = new Category();
		auxCategory.setName(category);
		reminder.setCategory(auxCategory);
		String priority = intent.getStringExtra("priority");
		reminder.setPriority(Priority.fromCode(Integer.parseInt(priority, 10)));
		return reminder;
	}

	private void initialize(Reminder reminder) {
		try {
			spinnerCategory = getSpinnerCategory();
		} catch (Exception e) {
			e.printStackTrace();
		}

		spinnerPriority = getSpinnerPriority();

		try {
			edtReminder = (EditText) findViewById(R.id.edtReminder);
			edtDetails = (EditText) findViewById(R.id.edtDetails);
			if (reminder != null) {
				updateFieldsFromReminder(reminder);
			}
			btnSave = (Button) findViewById(R.id.btnSave);
			btnCancel = (Button) findViewById(R.id.btnCancel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateFieldsFromReminder(Reminder reminder) throws Exception {
		edtReminder.setText(reminder.getText());
		edtDetails.setText(reminder.getDetails());
		edtHour.setText(reminder.getHour());
		cbMonday.setChecked(reminder.isMonday());
		cbTuesday.setChecked(reminder.isTuesday());
		cbWednesday.setChecked(reminder.isWednesday());
		cbThursday.setChecked(reminder.isThursday());
		cbFriday.setChecked(reminder.isFriday());
		cbSaturday.setChecked(reminder.isSaturday());
		cbSunday.setChecked(reminder.isSunday());
		spinnerCategory.setSelection(categoryToIndex(reminder.getCategory()));
		spinnerPriority.setSelection(reminder.getPriority());
	}
}
