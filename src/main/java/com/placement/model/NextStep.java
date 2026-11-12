package com.placement.model;

/** The single most useful thing to do next, shown at the top of each dashboard. */
public class NextStep {
    private final String title, text, href, cta, tone;

    public NextStep(String title, String text, String href, String cta, String tone) {
        this.title = title; this.text = text; this.href = href; this.cta = cta; this.tone = tone;
    }
    public static NextStep todo(String title, String text, String href, String cta) { return new NextStep(title, text, href, cta, "todo"); }
    public static NextStep done(String title, String text, String href, String cta) { return new NextStep(title, text, href, cta, "done"); }

    public String getTitle() { return title; }
    public String getText() { return text; }
    public String getHref() { return href; }
    public String getCta() { return cta; }
    /** todo = needs action, done = all caught up. */
    public String getTone() { return tone; }
}
