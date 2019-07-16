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

}
