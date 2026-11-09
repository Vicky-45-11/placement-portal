package com.placement.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Explainable match score between a student and a drive, 0-100.
 * 70% comes from how many of the drive's required skills the student lists,
 * 30% from how far the student's CGPA is above the drive's minimum.
 */
public final class SkillMatch {
    private final int percent;
    private final List<String> matched;
    private final List<String> missing;

    private SkillMatch(int percent, List<String> matched, List<String> missing) {
        this.percent = percent;
        this.matched = matched;
        this.missing = missing;
    }

    public static SkillMatch of(String requiredCsv, String studentSkillsCsv, BigDecimal cgpa, BigDecimal minCgpa, boolean eligible) {
        List<String> required = split(requiredCsv);
        Set<String> have = split(studentSkillsCsv).stream().map(SkillMatch::norm).collect(Collectors.toSet());
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String r : required) {
            if (have.contains(norm(r)) || have.stream().anyMatch(h -> h.contains(norm(r)) || norm(r).contains(h) && h.length() > 2)) matched.add(r);
            else missing.add(r);
        }
        double skill = required.isEmpty() ? 0.5 : (double) matched.size() / required.size();
        double margin = cgpa == null || minCgpa == null ? 0 : cgpa.subtract(minCgpa).doubleValue();
        double academic = Math.max(0, Math.min(1, 0.5 + margin / 3.0));
        int pct = (int) Math.round(100 * (0.7 * skill + 0.3 * academic));
        if (!eligible) pct = Math.min(pct, 35);
        return new SkillMatch(pct, matched, missing);
    }

    static List<String> split(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(",")).map(String::trim).filter(x -> !x.isEmpty()).toList();
    }

    private static String norm(String s) { return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9+#]", ""); }

    public int getPercent() { return percent; }
    public List<String> getMatched() { return matched; }
    public List<String> getMissing() { return missing; }
    /** CSS hint: strong, fair or weak. */
    public String getLevel() { return percent >= 70 ? "strong" : percent >= 45 ? "fair" : "weak"; }
}
