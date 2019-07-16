package br.unb.cic.reminders.view;

import android.content.Intent;
import android.os.Bundle;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders.model.Category;
import java.util.List;
import br.unb.cic.reminders.model.Priority;

public class ExternalAddReminderActivity extends ReminderActivity {
	private Category newCategory = null;

	private void setNewCategory(Intent intent) throws Exception {
		String categoryName = intent.getStringExtra("category_name");
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		for (Category c : categories) {
			if (c.getName().equals(categoryName)) {
				newCategory = c;
				break;
			}
		}
	}

	@Override
	protected List<Category> getCategories() throws Exception {
		List<Category> categories = super.getCategories();
		return categories;
	}

	private Category findCategory(Category category) throws Exception {
		List<Category> categories = Controller.instance(getApplicationContext()).listCategories();
		for (Category c : categories) {
			if (c.getName().equals(category.getName()))
				return c;
		}
		return null;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		reminder = new Reminder();
		try {
			setReminderFromIntent();
		} catch (Exception e) {
			Intent intent2 = new Intent(getApplicationContext(), AddReminderActivity.class);
			startActivity(intent2);
			finish();
		}
		super.onCreate(savedInstanceState);
	}

	private void setReminderFromIntent() throws Exception {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (action.equals("br.com.positivo.reminders.ADD_REMINDER") && "text/plain".equals(type)) {
			String text = intent.getStringExtra("text");
			String details = intent.getStringExtra("details");
			reminder.setText(text);
			reminder.setDetails(details);
			reminderFromIntent(intent);
		} else
			reminder = null;
	}

	private void reminderFromIntent(Intent intent) throws Exception {
		String date = intent.getStringExtra("date");
		reminder.setDate(date);
		String hour = intent.getStringExtra("hour");
		reminder.setHour(hour);
		setNewCategory(intent);
		reminder.setCategory(newCategory);
		String priority = intent.getStringExtra("priority");
		reminder.setPriority(Priority.fromCode(Integer.parseInt(priority)));
	}

	@Override
	protected void initializeValues() {
		if (!reminder.isValid())
			return;
		edtReminder.setText(reminder.getText());
		edtDetails.setText(reminder.getDetails());
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws Exception {
		updateSpinnerDateHour(spinnerDate, reminder.getDate());
		updateDateFromString(reminder.getDate());
		updateSpinnerDateHour(spinnerTime, reminder.getHour());
		updateTimeFromString(reminder.getHour());
		spinnerCategory.setSelection(categoryToIndex(reminder.getCategory()));
		spinnerPriority.setSelection(reminder.getPriority());
	}

	@Override
	protected void persist(Reminder reminder) {
		try {
			Controller.instance(getApplicationContext()).addReminder(reminder);
		} catch (DBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
