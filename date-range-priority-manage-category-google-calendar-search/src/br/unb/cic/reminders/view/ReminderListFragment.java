package br.unb.cic.reminders.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import util.Utility;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.controller.AllRemindersFilter;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.controller.ReminderFilter;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders2.R;

public class ReminderListFragment extends Fragment implements FiltersListChangeListener {
	private static String TAG = "reminder fragment list";
	private ListView lvReminderLate, lvReminderToday, lvReminderNextDays;
	private ReminderArrayAdapter adapter;
	private ReminderArrayAdapter contextMenuAdapter;
	private View view;

	@Override
	public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		view = inflater.inflate(R.layout.reminders_list_fragment, container, false);
		createUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateListView(null);
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ListView view = (ListView) v;
		contextMenuAdapter = (ReminderArrayAdapter) view.getAdapter();
		menu.setHeaderTitle(R.string.context_menu_reminder_title);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.reminder_list_fragment_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == R.id.context_menu_reminder) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			Reminder reminder = (Reminder) contextMenuAdapter.getItem(info.position);
			switch (item.getItemId()) {
			case R.id.edit:
				Intent editIntent = editIntent(reminder);
				editIntent.putExtra("id", reminder.getId());
				editIntent.putExtra("text", reminder.getText());
				editIntent.putExtra("details", reminder.getDetails());
				startActivity(editIntent);
				updateListView(null);
				return true;
			case R.id.delete:
				try {
					Controller.instance(getActivity().getApplicationContext()).deleteReminder(reminder);
				} catch (DBException e) {
					Log.e(TAG, e.getMessage());
				}
				reloadFilterListFragment();
				updateListView(null);
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		}
		return super.onContextItemSelected(item);
	}

	public void reloadFilterListFragment() {
		Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.listCategories);
		if (currentFragment instanceof FilterListFragment) {
			FragmentTransaction fragTransaction = (getActivity()).getFragmentManager().beginTransaction();
			fragTransaction.detach(currentFragment);
			fragTransaction.attach(currentFragment);
			fragTransaction.commit();
		}
	}

	private Intent editIntent(Reminder reminder) {
		Intent editIntent = new Intent(getActivity().getApplicationContext(), EditReminderActivity.class);
		editIntent.putExtra("dateStart", reminder.getDateStart());
		editIntent.putExtra("hourStart", reminder.getHourStart());
		editIntent.putExtra("dateFinal", reminder.getDateFinal());
		editIntent.putExtra("hourFinal", reminder.getHourFinal());
		editIntent.putExtra("category_name", reminder.getCategory().getName());
		editIntent.putExtra("category_id", Long.toString(reminder.getCategory().getId()));
		editIntent.putExtra("priority", Integer.toString(reminder.getPriority()));
		return editIntent;
	}

	public void createUI() {
		lvReminderLate = (ListView) view.findViewById(R.id.lvRemindersLate);
		lvReminderToday = (ListView) view.findViewById(R.id.lvRemindersToday);
		lvReminderNextDays = (ListView) view.findViewById(R.id.lvRemindersNextDays);
		updateListView(null);
		registerForContextMenu(lvReminderLate);
		registerForContextMenu(lvReminderToday);
		registerForContextMenu(lvReminderNextDays);
	}

	public void updateListView(ReminderFilter filter) {
		if (filter == null)
			filter = new AllRemindersFilter(getActivity());
		adapter = new ReminderArrayAdapter(getActivity().getApplicationContext(), filter.getReminderList());
		ReminderArrayAdapter adapterLate, adapterToday, adapterNextDays;
		Reminder r = new Reminder();
		List<Reminder> remindersLate = new ArrayList<Reminder>();
		List<Reminder> remindersToday = new ArrayList<Reminder>();
		List<Reminder> remindersNextDays = new ArrayList<Reminder>();

		for (int i = 0; i < adapter.getCount(); ++i) {
			r = adapter.getItem(i);
			if (r.getDateFinal() != null && r.getDateStart() != null) {
				String dayStart = r.getDateStart().substring(0, 2);
				String monthStart = r.getDateStart().substring(3, 5);
				String yearStart = r.getDateStart().substring(6, 10);
				String dayFinal = r.getDateFinal().substring(0, 2);
				String monthFinal = r.getDateFinal().substring(3, 5);
				String yearFinal = r.getDateFinal().substring(6, 10);

				Calendar cal = Calendar.getInstance();
				GregorianCalendar gc = new GregorianCalendar();
				GregorianCalendar gc2 = new GregorianCalendar();
				if (r.getHourFinal() != null) {
					String hour = r.getHourFinal().substring(0, 2);
					String min = r.getHourFinal().substring(3, 5);
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal));
				}
				if (r.getHourStart() != null) {
					String hour = r.getHourStart().substring(0, 2);
					String min = r.getHourStart().substring(3, 5);
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart));
				}
				if (gc.before(cal))
					remindersLate.add(r);
				else if (gc.after(cal) && gc2.before(cal))
					remindersToday.add(r);
				else
					remindersNextDays.add(r);
			}
		}

		adapterLate = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersLate,
				Color.rgb(0xED, 0x1C, 0x24), ReminderArrayAdapter.LATE);
		adapterToday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersToday,
				Color.rgb(0x33, 0xB5, 0xE5), ReminderArrayAdapter.TODAY);
		adapterNextDays = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersNextDays,
				Color.rgb(0x99, 0x99, 0x99), ReminderArrayAdapter.NEXT_DAYS);
		lvReminderLate.setAdapter(adapterLate);
		Utility.setListViewHeightBasedOnChildren(lvReminderLate);
		lvReminderToday.setAdapter(adapterToday);
		Utility.setListViewHeightBasedOnChildren(lvReminderToday);
		lvReminderNextDays.setAdapter(adapterNextDays);
		Utility.setListViewHeightBasedOnChildren(lvReminderNextDays);
	}

	public void onSelectedFilterChanged(ReminderFilter filter) {
		updateListView(filter);
	}

	public void updateListViewFilter(String filterRemider) {

		ReminderFilter filter = new AllRemindersFilter(getActivity());
		ArrayList<Reminder> reminders = new ArrayList<Reminder>();
		for (Reminder item : filter.getReminderList()) {

			if (item.getText().contains(filterRemider)) {
				reminders.add(item);
			}
		}

		adapter = new ReminderArrayAdapter(getActivity().getApplicationContext(), reminders);
		ReminderArrayAdapter adapterLate, adapterToday, adapterNextDays;
		Reminder r = new Reminder();
		List<Reminder> remindersLate = new ArrayList<Reminder>();
		List<Reminder> remindersToday = new ArrayList<Reminder>();
		List<Reminder> remindersNextDays = new ArrayList<Reminder>();

		for (int i = 0; i < adapter.getCount(); ++i) {
			r = adapter.getItem(i);
			if (r.getDateFinal() != null && r.getDateStart() != null) {
				String dayStart = r.getDateStart().substring(0, 2);
				String monthStart = r.getDateStart().substring(3, 5);
				String yearStart = r.getDateStart().substring(6, 10);
				String dayFinal = r.getDateFinal().substring(0, 2);
				String monthFinal = r.getDateFinal().substring(3, 5);
				String yearFinal = r.getDateFinal().substring(6, 10);

				Calendar cal = Calendar.getInstance();
				GregorianCalendar gc = new GregorianCalendar();
				GregorianCalendar gc2 = new GregorianCalendar();
				if (r.getHourFinal() != null) {
					String hour = r.getHourFinal().substring(0, 2);
					String min = r.getHourFinal().substring(3, 5);
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc.set(Integer.parseInt(yearFinal), Integer.parseInt(monthFinal) - 1, Integer.parseInt(dayFinal));
				}
				if (r.getHourStart() != null) {
					String hour = r.getHourStart().substring(0, 2);
					String min = r.getHourStart().substring(3, 5);
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart),
							Integer.parseInt(hour), Integer.parseInt(min));
				} else {
					gc2.set(Integer.parseInt(yearStart), Integer.parseInt(monthStart) - 1, Integer.parseInt(dayStart));
				}
				if (gc.before(cal))
					remindersLate.add(r);
				else if (gc.after(cal) && gc2.before(cal))
					remindersToday.add(r);
				else
					remindersNextDays.add(r);
			}
		}

		adapterLate = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersLate,
				Color.rgb(0xED, 0x1C, 0x24), ReminderArrayAdapter.LATE);
		adapterToday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersToday,
				Color.rgb(0x33, 0xB5, 0xE5), ReminderArrayAdapter.TODAY);
		adapterNextDays = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersNextDays,
				Color.rgb(0x99, 0x99, 0x99), ReminderArrayAdapter.NEXT_DAYS);
		lvReminderLate.setAdapter(adapterLate);
		Utility.setListViewHeightBasedOnChildren(lvReminderLate);
		lvReminderToday.setAdapter(adapterToday);
		Utility.setListViewHeightBasedOnChildren(lvReminderToday);
		lvReminderNextDays.setAdapter(adapterNextDays);
		Utility.setListViewHeightBasedOnChildren(lvReminderNextDays);
	}
}
