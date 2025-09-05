package com.ub19.quality;

import java.util.List;

/**
 * Minimal HTML report generator.
 */
public final class HtmlRenderer {
    private HtmlRenderer() {
    }

    public static String render(QualityReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><h1>Quality Report: ").append(report.module()).append("</h1>");
        renderSection(sb, "Surefire", report.surefire());
        renderSection(sb, "Failsafe", report.failsafe());
        renderSection(sb, "PMD", report.pmd());
        renderSection(sb, "SpotBugs", report.spotbugs());
        renderSection(sb, "Semgrep", report.semgrep());
        sb.append("</body></html>");
        return sb.toString();
    }

    private static void renderSection(StringBuilder sb, String title, List<ReportFile> files) {
        sb.append("<h2>").append(title).append("</h2><ul>");
        for (ReportFile f : files) {
            sb.append("<li>").append(f.path());
            if (f.blocker()) {
                sb.append(" (BLOCKER)");
            }
            sb.append("</li>");
        }
        sb.append("</ul>");
    }
}
