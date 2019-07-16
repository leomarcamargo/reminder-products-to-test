package br.unb.cic.reminders.view;

import java.util.ArrayList;
import java.util.List;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import br.unb.cic.reminders.controller.AllRemindersFilter;
import br.unb.cic.reminders.controller.Controller;
import br.unb.cic.reminders.controller.ReminderFilter;
import br.unb.cic.reminders2.R;
import br.unb.cic.reminders.controller.CategoryFilter;
import br.unb.cic.reminders.model.Category;
import android.util.Log;
import br.unb.cic.framework.persistence.DBException;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import br.unb.cic.reminders.controller.PriorityFilter;
import br.unb.cic.reminders.model.Priority;

public class FilterListFragment extends Fragment implements OnItemClickListener {

	private static final String CURRENT_FILTER_KEY = "current_filter";
	private static String TAG = "filter fragment list";
	private int currentFilterIndex;
	private List<FiltersListChangeListener> listeners;
	private FiltersListChangeListener filtersChangeListener;
	private int currentFilterId;
	private ReminderFilterArrayAdapter adapter;
	private View view;

	private Button btAddCategory;
	private ListView lvFilters;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			currentFilterIndex = savedInstanceState.getInt(CURRENT_FILTER_KEY);
		}

		currentFilterId = 0;
		adapter = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.categories_list_fragment, container, false);
		createUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateListView();
	}

	public void addListener(FiltersListChangeListener filter) {
		if (listeners == null)
			listeners = new ArrayList<FiltersListChangeListener>();
		listeners.add(filter);
	}

	public void notifyListeners(ReminderFilter filter) {
		for (FiltersListChangeListener c : listeners) {
			c.onSelectedFilterChanged(filter);
		}
	}

	private void createUI() {
		lvFilters = (ListView) view.findViewById(R.id.listCategories);
		lvFilters.setOnItemClickListener(this);
		registerForContextMenu(lvFilters);
		updateListView();
	}

	private void updateListView() {
		List<ReminderFilter> filters = listOfFilters();
		adapter = new ReminderFilterArrayAdapter(getActivity().getApplicationContext(), filters);
		lvFilters.setAdapter(adapter);
	}

	private List<ReminderFilter> listOfFilters() {
		List<ReminderFilter> filters = new ArrayList<ReminderFilter>();

		AllRemindersFilter allRemindersFilter = new AllRemindersFilter(getActivity());
		filters.add(allRemindersFilter);

		List<Category> categories = new ArrayList<Category>();
		try {
			categories = Controller.instance(getActivity().getApplicationContext()).listCategories();
			notifyListeners(null);
		} catch (DBException e) {
			Log.e(CURRENT_FILTER_KEY, "STORAGE_SERVICE error. Message: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(CURRENT_FILTER_KEY, "STORAGE_SERVICE error. Message: " + e.getMessage());
			e.printStackTrace();
		}

		ReminderFilter filter;
		for (Category c : categories) {
			filter = new CategoryFilter(c, getActivity());
			filters.add(filter);
		}

		PriorityFilter highPriorityFilter = new PriorityFilter(Priority.HIGH, getActivity());
		filters.add(highPriorityFilter);
		PriorityFilter normalPriorityFilter = new PriorityFilter(Priority.NORMAL, getActivity());
		filters.add(normalPriorityFilter);
		PriorityFilter lowPriorityFilter = new PriorityFilter(Priority.LOW, getActivity());
		filters.add(lowPriorityFilter);

		return filters;
	}

	public void onItemClick(AdapterView<? extends Object> adapterView, View view, int position, long id) {
		notifyListeners(adapter.getItem(position));

	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(R.string.context_menu_category_title);

		MenuInflater inflater = getActivity().getMenuInflater();

		if (((AdapterContextMenuInfo) menuInfo).position < 1) {
			return;
		}

		List<Category> categories = new ArrayList<Category>();
		try {
			categories = Controller.instance(getActivity().getApplicationContext()).listCategories();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		if (((AdapterContextMenuInfo) menuInfo).position > categories.size()) {
			return;
		}

		inflater.inflate(R.menu.category_list_fragment_context_menu, menu);
	}

	public void reloadReminderListFragment() {
		Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.listReminders);
		if (currentFragment instanceof ReminderListFragment) {
			FragmentTransaction fragTransaction = (getActivity()).getFragmentManager().beginTransaction();
			fragTransaction.detach(currentFragment);
			fragTransaction.attach(currentFragment);
			fragTransaction.commit();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == R.id.context_menu_category) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			Category category = ((CategoryFilter) lvFilters.getAdapter().getItem(info.position)).getCategory();

			switch (item.getItemId()) {
			case R.id.edit:
				DialogFragment newFragment = EditCategoryDialogFragment.newInstance(category);
				newFragment.show(getFragmentManager(), "" + R.string.dialog_editcategory_title);
				updateListView();
				return true;
			case R.id.delete:
				try {
					Controller.instance(getActivity().getApplicationContext()).deleteReminderByCategory(category);
					Controller.instance(getActivity().getApplicationContext()).deleteCategory(category);
				} catch (DBException e) {
					Log.e(TAG, e.getMessage());
				}
				reloadReminderListFragment();
				updateListView();
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		}
		return super.onContextItemSelected(item);
	}
}
