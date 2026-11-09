package com.placement.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** One row of a grouped placement report (by department or by company). */
public class ReportRow {
    private String label;
    private int total;
    private int placed;
    private BigDecimal avgCtc;
    private BigDecimal maxCtc;

    public int getPercent() { return total == 0 ? 0 : Math.round(placed * 100f / total); }
    public BigDecimal getAvgCtcRounded() { return avgCtc == null ? null : avgCtc.setScale(2, RoundingMode.HALF_UP); }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPlaced() { return placed; }
    public void setPlaced(int placed) { this.placed = placed; }
    public BigDecimal getAvgCtc() { return avgCtc; }
    public void setAvgCtc(BigDecimal avgCtc) { this.avgCtc = avgCtc; }
    public BigDecimal getMaxCtc() { return maxCtc; }
    public void setMaxCtc(BigDecimal maxCtc) { this.maxCtc = maxCtc; }
}
