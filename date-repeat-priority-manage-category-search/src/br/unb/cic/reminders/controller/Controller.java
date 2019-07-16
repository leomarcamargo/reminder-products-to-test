package br.unb.cic.reminders.controller;

import java.util.List;
import android.content.Context;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders.model.db.DBFactory;
import br.unb.cic.reminders.model.Category;

public class Controller {
	private Context context;
	private static Controller instance;

	private Controller(Context c) {
		this.context = c;
	}

	public static final Controller instance(Context c) {
		if (instance == null) {
			instance = new Controller(c);
		}
		return instance;
	}

	public List<Reminder> listReminders() throws Exception {
		try {
			return DBFactory.factory(context).createReminderDAO().listReminders();
		} catch (DBException e) {
			throw e;
		}
	}

	public void addReminder(Reminder reminder) throws DBException {
		try {
			DBFactory.factory(context).createReminderDAO().saveReminder(reminder);
		} catch (DBException e) {
			throw e;
		}
	}

	public void updateReminder(Reminder reminder) throws DBException {
		try {
			DBFactory.factory(context).createReminderDAO().updateReminder(reminder);
		} catch (DBException e) {
			throw e;
		}
	}

	public void deleteReminder(Reminder reminder) throws DBException {
		try {
			DBFactory.factory(context).createReminderDAO().deleteReminder(reminder);
		} catch (DBException e) {
			throw e;
		}
	}

	public void persistReminder(Reminder reminder) throws DBException {
		try {
			DBFactory.factory(context).createReminderDAO().persistReminder(reminder);
		} catch (DBException e) {
			throw e;
		}
	}

	public List<Category> listCategories() throws Exception {
		try {
			return DBFactory.factory(context).createCategoryDAO().listCategories();
		} catch (DBException e) {
			throw e;
		}
	}

	public Category findCategory(Long id) throws Exception {
		return DBFactory.factory(context).createCategoryDAO().findCategoryById(id);
	}

	public Category findCategory(String name) throws Exception {
		return DBFactory.factory(context).createCategoryDAO().findCategory(name);
	}

	public List<Reminder> listRemindersByCategory(Category category) throws Exception {
		try {
			return DBFactory.factory(context).createReminderDAO().listRemindersByCategory(category);
		} catch (DBException e) {
			throw e;
		}
	}

	public Category getCategory(String name) throws DBException {
		try {
			List<Category> categories = DBFactory.factory(context).createCategoryDAO().listCategories();
			for (Category c : categories) {
				if (c.getName().equals(name))
					return c;
			}
			return null;
		} catch (DBException e) {
			throw e;
		}
	}

	public void deleteReminderByCategory(Category category) throws DBException {
		try {
			List<Reminder> allReminders = DBFactory.factory(context).createReminderDAO()
					.listRemindersByCategory(category);
			for (Reminder reminder : allReminders) {
				DBFactory.factory(context).createReminderDAO().deleteReminder(reminder);
			}
		} catch (DBException e) {
			throw e;
		}
	}

	public void addCategory(Category category) throws Exception {
		try {
			DBFactory.factory(context).createCategoryDAO().saveCategory(category);
		} catch (DBException e) {
			throw e;
		}
	}

	public void updateCategory(Category category) throws DBException {
		try {
			DBFactory.factory(context).createCategoryDAO().updateCategory(category);
		} catch (DBException e) {
			throw e;
		}
	}

	public void deleteCategory(Category category) throws DBException {
		try {
			DBFactory.factory(context).createCategoryDAO().deleteCategory(category);
		} catch (DBException e) {
			throw e;
		}
	}
}
