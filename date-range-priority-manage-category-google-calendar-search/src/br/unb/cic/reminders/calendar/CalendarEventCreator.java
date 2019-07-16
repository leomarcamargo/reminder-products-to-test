package br.unb.cic.reminders.calendar;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import util.DateFormat;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import br.unb.cic.reminders.model.CalendarNotFoundException;
import br.unb.cic.reminders.model.Reminder;

public class CalendarEventCreator {
	private String userAccount, timeZone, accountName, displayName;
	private long calendarID;
	private boolean haveMainCalendar;

	private static final String[] COLUMNS = new String[] { Calendars._ID,
			Calendars.ACCOUNT_NAME,
			Calendars.CALENDAR_DISPLAY_NAME,
			Calendars.OWNER_ACCOUNT,
			Calendars.CALENDAR_TIME_ZONE
	};

	private void initializer() {
		haveMainCalendar = false;
		userAccount = null;
		accountName = null;
		displayName = null;
		timeZone = TimeZone.getDefault().getID();
		calendarID = -1;
	}

	private void getUserMainCalendar(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		initializer();

		Cursor cur = null;
		cur = cr.query(Calendars.CONTENT_URI, COLUMNS, null, null, null);
		while (cur.moveToNext()) {
			if (cur.getString(3).contains("@gmail.com")) {
				haveMainCalendar = true;
				calendarID = cur.getLong(0);
				accountName = cur.getString(1);
				displayName = cur.getString(2);
				userAccount = cur.getString(3);
				break;
			}
		}
	}

	public void addEventCalendar(Reminder reminder, Context ctx) throws CalendarNotFoundException, ParseException {
		long startMilliSeconds, endMilliSeconds;
		Calendar calStart;
		Calendar calFinal;
		startMilliSeconds = endMilliSeconds = 0;

		getUserMainCalendar(ctx);

		if (haveMainCalendar) {
			ContentResolver cr = ctx.getContentResolver();
			calStart = Calendar.getInstance();
			calFinal = Calendar.getInstance();
			Date dateStart = DateFormat.dateFormater(reminder.getDateStart() + " " + reminder.getHourStart());
			Date dateFinal = DateFormat.dateFormater(reminder.getDateFinal() + " " + reminder.getHourFinal());
			calFinal.setTime(dateFinal);
			calStart.setTime(dateStart);

			startMilliSeconds = calStart.getTimeInMillis();
			endMilliSeconds = calFinal.getTimeInMillis();

			ContentValues values = new ContentValues();

			values.put(Events.DTSTART, startMilliSeconds);
			values.put(Events.DTEND, endMilliSeconds);
			values.put(Events.TITLE, reminder.getText());
			values.put(Events.CALENDAR_ID, calendarID);
			values.put(Events.EVENT_TIMEZONE, timeZone);

			cr.insert(Events.CONTENT_URI, values);
		} else {
			throw new CalendarNotFoundException();
		}
	}
}
