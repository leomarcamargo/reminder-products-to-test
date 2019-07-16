package br.unb.cic.reminders.view;

import java.util.ArrayList;
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
	private ListView lvMonday, lvTuesday, lvWednesday, lvThursday, lvFriday, lvSaturday, lvSunday;
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
		editIntent.putExtra("hour", reminder.getHour());
		editIntent.putExtra("monday", reminder.isMonday());
		editIntent.putExtra("tuesday", reminder.isTuesday());
		editIntent.putExtra("wednesday", reminder.isWednesday());
		editIntent.putExtra("thursday", reminder.isThursday());
		editIntent.putExtra("friday", reminder.isFriday());
		editIntent.putExtra("saturday", reminder.isSaturday());
		editIntent.putExtra("sunday", reminder.isSunday());
		editIntent.putExtra("category_name", reminder.getCategory().getName());
		editIntent.putExtra("category_id", Long.toString(reminder.getCategory().getId()));
		editIntent.putExtra("priority", Integer.toString(reminder.getPriority()));
		return editIntent;
	}

	public void createUI() {
		lvMonday = (ListView) view.findViewById(R.id.lvMonday);
		lvTuesday = (ListView) view.findViewById(R.id.lvTuesday);
		lvWednesday = (ListView) view.findViewById(R.id.lvWednesday);
		lvThursday = (ListView) view.findViewById(R.id.lvThursday);
		lvFriday = (ListView) view.findViewById(R.id.lvFriday);
		lvSaturday = (ListView) view.findViewById(R.id.lvSaturday);
		lvSunday = (ListView) view.findViewById(R.id.lvSunday);
		updateListView(null);
		registerForContextMenu(lvMonday);
		registerForContextMenu(lvTuesday);
		registerForContextMenu(lvWednesday);
		registerForContextMenu(lvThursday);
		registerForContextMenu(lvFriday);
		registerForContextMenu(lvSaturday);
		registerForContextMenu(lvSunday);
	}

	public void updateListView(ReminderFilter filter) {
		if (filter == null)
			filter = new AllRemindersFilter(getActivity());
		adapter = new ReminderArrayAdapter(getActivity().getApplicationContext(), filter.getReminderList());
		ReminderArrayAdapter adapterMonday, adapterTuesday, adapterWednesday, adapterThursday, adapterFriday,
				adapterSaturday, adapterSunday;
		Reminder r = new Reminder();
		List<Reminder> remindersModay = new ArrayList<Reminder>();
		List<Reminder> remindersTuesday = new ArrayList<Reminder>();
		List<Reminder> remindersWednesday = new ArrayList<Reminder>();
		List<Reminder> remindersThursday = new ArrayList<Reminder>();
		List<Reminder> remindersFriday = new ArrayList<Reminder>();
		List<Reminder> remindersSaturday = new ArrayList<Reminder>();
		List<Reminder> remindersSunday = new ArrayList<Reminder>();

		for (int i = 0; i < adapter.getCount(); ++i) {
			r = adapter.getItem(i);
			if (r.isMonday())
				remindersModay.add(r);
			if (r.isTuesday())
				remindersTuesday.add(r);
			if (r.isWednesday())
				remindersWednesday.add(r);
			if (r.isThursday())
				remindersThursday.add(r);
			if (r.isFriday())
				remindersFriday.add(r);
			if (r.isSaturday())
				remindersSaturday.add(r);
			if (r.isSunday())
				remindersSunday.add(r);
		}

		adapterMonday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersModay,
				Color.rgb(0, 0, 0), ReminderArrayAdapter.MONDAY);
		adapterTuesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersTuesday,
				Color.rgb(0, 0, 255), ReminderArrayAdapter.TUESDAY);
		adapterWednesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersWednesday,
				Color.rgb(210, 105, 30), ReminderArrayAdapter.WEDNESDAY);
		adapterThursday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersThursday,
				Color.rgb(0, 128, 0), ReminderArrayAdapter.THURSDAY);
		adapterFriday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersFriday,
				Color.rgb(255, 20, 147), ReminderArrayAdapter.FRIDAY);
		adapterSaturday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSaturday,
				Color.rgb(255, 0, 0), ReminderArrayAdapter.SATURDAY);
		adapterSunday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSunday,
				Color.rgb(128, 0, 128), ReminderArrayAdapter.SUNDAY);

		lvMonday.setAdapter(adapterMonday);
		Utility.setListViewHeightBasedOnChildren(lvMonday);
		lvTuesday.setAdapter(adapterTuesday);
		Utility.setListViewHeightBasedOnChildren(lvTuesday);
		lvWednesday.setAdapter(adapterWednesday);
		Utility.setListViewHeightBasedOnChildren(lvWednesday);
		lvThursday.setAdapter(adapterThursday);
		Utility.setListViewHeightBasedOnChildren(lvThursday);
		lvFriday.setAdapter(adapterFriday);
		Utility.setListViewHeightBasedOnChildren(lvFriday);
		lvSaturday.setAdapter(adapterSaturday);
		Utility.setListViewHeightBasedOnChildren(lvSaturday);
		lvSunday.setAdapter(adapterSunday);
		Utility.setListViewHeightBasedOnChildren(lvSunday);
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
		ReminderArrayAdapter adapterMonday, adapterTuesday, adapterWednesday, adapterThursday, adapterFriday,
				adapterSaturday, adapterSunday;
		Reminder r = new Reminder();
		List<Reminder> remindersModay = new ArrayList<Reminder>();
		List<Reminder> remindersTuesday = new ArrayList<Reminder>();
		List<Reminder> remindersWednesday = new ArrayList<Reminder>();
		List<Reminder> remindersThursday = new ArrayList<Reminder>();
		List<Reminder> remindersFriday = new ArrayList<Reminder>();
		List<Reminder> remindersSaturday = new ArrayList<Reminder>();
		List<Reminder> remindersSunday = new ArrayList<Reminder>();

		for (int i = 0; i < adapter.getCount(); ++i) {
			r = adapter.getItem(i);
			if (r.isMonday())
				remindersModay.add(r);
			if (r.isTuesday())
				remindersTuesday.add(r);
			if (r.isWednesday())
				remindersWednesday.add(r);
			if (r.isThursday())
				remindersThursday.add(r);
			if (r.isFriday())
				remindersFriday.add(r);
			if (r.isSaturday())
				remindersSaturday.add(r);
			if (r.isSunday())
				remindersSunday.add(r);
		}

		adapterMonday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersModay,
				Color.rgb(0, 0, 0), ReminderArrayAdapter.MONDAY);
		adapterTuesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersTuesday,
				Color.rgb(0, 0, 255), ReminderArrayAdapter.TUESDAY);
		adapterWednesday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersWednesday,
				Color.rgb(210, 105, 30), ReminderArrayAdapter.WEDNESDAY);
		adapterThursday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersThursday,
				Color.rgb(0, 128, 0), ReminderArrayAdapter.THURSDAY);
		adapterFriday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersFriday,
				Color.rgb(255, 20, 147), ReminderArrayAdapter.FRIDAY);
		adapterSaturday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSaturday,
				Color.rgb(255, 0, 0), ReminderArrayAdapter.SATURDAY);
		adapterSunday = new ReminderArrayAdapter(getActivity().getApplicationContext(), remindersSunday,
				Color.rgb(128, 0, 128), ReminderArrayAdapter.SUNDAY);

		lvMonday.setAdapter(adapterMonday);
		Utility.setListViewHeightBasedOnChildren(lvMonday);
		lvTuesday.setAdapter(adapterTuesday);
		Utility.setListViewHeightBasedOnChildren(lvTuesday);
		lvWednesday.setAdapter(adapterWednesday);
		Utility.setListViewHeightBasedOnChildren(lvWednesday);
		lvThursday.setAdapter(adapterThursday);
		Utility.setListViewHeightBasedOnChildren(lvThursday);
		lvFriday.setAdapter(adapterFriday);
		Utility.setListViewHeightBasedOnChildren(lvFriday);
		lvSaturday.setAdapter(adapterSaturday);
		Utility.setListViewHeightBasedOnChildren(lvSaturday);
		lvSunday.setAdapter(adapterSunday);
		Utility.setListViewHeightBasedOnChildren(lvSunday);
	}
}
