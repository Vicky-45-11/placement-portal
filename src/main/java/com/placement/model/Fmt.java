package com.placement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Date formatting used by the model getters (JSP fmt tags do not handle java.time). */
final class Fmt {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private Fmt() {}
    static String date(LocalDate d) { return d == null ? "" : d.format(DATE); }
    static String dateTime(LocalDateTime d) { return d == null ? "" : d.format(DATE_TIME); }
}
