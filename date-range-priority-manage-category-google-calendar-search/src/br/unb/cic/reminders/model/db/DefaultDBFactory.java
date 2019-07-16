package br.unb.cic.reminders.model.db;

import android.content.Context;

public class DefaultDBFactory extends DBFactory {
	public DefaultDBFactory(Context context) {
		super(context);
	}

	@Override
	public ReminderDAO createReminderDAO() {
		return new DefaultReminderDAO(context);
	}

	@Override
	public CategoryDAO createCategoryDAO() {
		return new DefaultCategoryDAO(context);
	}
}
