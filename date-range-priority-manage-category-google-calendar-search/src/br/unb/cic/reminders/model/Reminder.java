package br.unb.cic.reminders.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.Patterns;
import br.unb.cic.framework.persistence.DBTypes;
import br.unb.cic.framework.persistence.annotations.Column;
import br.unb.cic.framework.persistence.annotations.Entity;
import br.unb.cic.framework.persistence.annotations.ForeignKey;
import br.unb.cic.reminders.view.InvalidHourException;

@Entity(table = "REMINDER")
public class Reminder {
	@Column(column = "PK", primaryKey = true, type = DBTypes.LONG)
	private Long id;
	@Column(column = "TEXT", type = DBTypes.TEXT)
	private String text;
	@Column(column = "DETAILS", type = DBTypes.TEXT)
	private String details;

	@Column(column = "INITIAL_DATE", type = DBTypes.TEXT)
	private String dateStart;
	@Column(column = "INITIAL_HOUR", type = DBTypes.TEXT)
	private String hourStart;
	@Column(column = "FINAL_DATE", type = DBTypes.TEXT)
	private String dateFinal;
	@Column(column = "FINAL_HOUR", type = DBTypes.TEXT)
	private String hourFinal;

	@Column(column = "DONE", type = DBTypes.INT)
	private boolean done;

	@Column(column = "FK_CATEGORY", type = DBTypes.LONG)
	@ForeignKey(mappedBy = "id")
	private Category category;

	@Column(column = "PRIORITY", type = DBTypes.INT)
	private Priority priority;

	public Reminder() {
	}

	public Reminder(Long id, String text) {
		this.id = id;
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (text == null || text.trim().equals("")) {
			throw new InvalidTextException(text);
		}
		this.text = text;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		if (details == null || details.trim().equals("")) {
			this.details = null;
		} else {
			this.details = details;
		}
	}

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {

		if (dateStart == null)
			throw new InvalidDateException(dateStart);
		if (!(dateStart == null || dateStart.equals("")) && !checkFormat(dateStart, Patterns.DATE_PATTERN)) {
			throw new InvalidDateException(dateStart);
		}
		this.dateStart = dateStart;
	}

	public String getHourStart() {
		return hourStart;
	}

	public void setHourStart(String hourStart) {
		if (!(hourStart == null || hourStart.equals("")) && !checkFormat(hourStart, Patterns.HOUR_PATTERN)) {
			throw new InvalidHourException(hourStart);
		}
		this.hourStart = hourStart;
	}

	public String getDateFinal() {
		return dateFinal;
	}

	public void setDateFinal(String dateFinal) {

		if (dateFinal == null)
			throw new InvalidDateException(dateFinal);
		if (!(dateFinal == null || dateFinal.equals("")) && !checkFormat(dateFinal, Patterns.DATE_PATTERN)) {
			throw new InvalidDateException(dateFinal);
		}
		this.dateFinal = dateFinal;
	}

	public String getHourFinal() {
		return hourFinal;
	}

	public void setHourFinal(String hourFinal) {
		if (!(hourFinal == null || hourFinal.equals("")) && !checkFormat(hourFinal, Patterns.HOUR_PATTERN)) {
			throw new InvalidHourException(hourFinal);
		}
		this.hourFinal = hourFinal;
	}

	private boolean checkFormat(String date, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(date);
		return m.matches();
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getPriority() {
		return priority.getCode();
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isValid() {
		return (text != null
				&& dateStart != null && hourStart != null && dateFinal != null && hourFinal != null
				&& category != null
				&& priority != null
		);
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getDone() {
		return done ? 1 : 0;
	}

	public void setDone(int done) {
		this.done = (done == 0 ? false : true);
	}
}
