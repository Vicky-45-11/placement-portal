package com.placement.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** A month grid (Monday first) with each day's interviews, for the interview calendar. */
public class CalendarMonth {
    public static class Day {
        private final LocalDate date;
        private final boolean inMonth;
        private final List<Interview> interviews = new ArrayList<>();
        Day(LocalDate date, boolean inMonth) { this.date = date; this.inMonth = inMonth; }
        public int getNumber() { return date.getDayOfMonth(); }
        public boolean isInMonth() { return inMonth; }
        public boolean isToday() { return date.equals(LocalDate.now()); }
        public boolean isWeekend() { return date.getDayOfWeek().getValue() >= 6; }
        public List<Interview> getInterviews() { return interviews; }
        public String getIso() { return date.toString(); }
    }

    private final YearMonth month;
    private final List<List<Day>> weeks = new ArrayList<>();
    private int count;

    public CalendarMonth(YearMonth month, List<Interview> all) {
        this.month = month;
        LocalDate start = month.atDay(1);
        while (start.getDayOfWeek() != DayOfWeek.MONDAY) start = start.minusDays(1);
        LocalDate end = month.atEndOfMonth();
        while (end.getDayOfWeek() != DayOfWeek.SUNDAY) end = end.plusDays(1);
        List<Day> week = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            Day day = new Day(d, YearMonth.from(d).equals(month));
            for (Interview i : all) {
                if (i.getScheduledAt() != null && i.getScheduledAt().toLocalDate().equals(d)) {
                    day.interviews.add(i);
                    if (day.inMonth) count++;
                }
            }
            week.add(day);
            if (week.size() == 7) { weeks.add(week); week = new ArrayList<>(); }
        }
    }

    public List<List<Day>> getWeeks() { return weeks; }
    public String getTitle() { return month.format(DateTimeFormatter.ofPattern("MMMM yyyy")); }
    public String getPrev() { return month.minusMonths(1).toString(); }
    public String getNext() { return month.plusMonths(1).toString(); }
    public int getCount() { return count; }

    public static YearMonth parse(String s) {
        try { return YearMonth.parse(s); } catch (Exception e) { return YearMonth.now(); }
    }
}
