package br.unb.cic.reminders.model.db;

import java.util.List;
import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.model.Reminder;
import br.unb.cic.reminders.model.Category;

public interface ReminderDAO {
	public Long saveReminder(Reminder r) throws DBException;

	public List<Reminder> listReminders() throws DBException;

	public void updateReminder(Reminder reminder) throws DBException;

	public void deleteReminder(Reminder reminder) throws DBException;

	public void persistReminder(Reminder reminder) throws DBException;

	public List<Reminder> listRemindersByCategory(Category category) throws DBException;
}
