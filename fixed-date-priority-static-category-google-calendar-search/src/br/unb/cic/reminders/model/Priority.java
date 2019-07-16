package br.unb.cic.reminders.model;

public enum Priority {

    LOW(0, "No Priority"), NORMAL(1, "Important"), HIGH(2, "Urgent");

    int code;
    String description;

    private Priority(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    static public Priority fromCode(int code) {
        for (Priority p : Priority.values()) {
            if (p.getCode() == code) {
                return p;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return description;
    }

}