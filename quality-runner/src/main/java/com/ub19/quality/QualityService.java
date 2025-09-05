package com.ub19.quality;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QualityService {

    private final Invoker invoker;
    private final ObjectMapper mapper;

    public QualityService(Invoker invoker, ObjectMapper mapper) {
        this.invoker = invoker;
        this.mapper = mapper;
    }

    public int run(Path modulePath) {
        int exit = 0;
        try {
            InvocationRequest req = new DefaultInvocationRequest();
            req.setPomFile(modulePath.resolve("pom.xml").toFile());
            req.setBaseDirectory(modulePath.toFile());
            req.setGoals(List.of("-q", "-Pci", "verify"));
            InvocationResult result = invoker.execute(req);
            exit = result.getExitCode();
        } catch (MavenInvocationException e) {
            exit = 1;
        }
        QualityReport report = collect(modulePath);
        writeReports(modulePath, report);
        if (exit == 0 && report.hasBlockers()) {
            exit = 1;
        }
        return exit;
    }

    private QualityReport collect(Path modulePath) {
        List<ReportFile> surefire = findReports(modulePath, "target/surefire-reports", "*.xml");
        List<ReportFile> failsafe = findReports(modulePath, "target/failsafe-reports", "*.xml");
        List<ReportFile> pmd = new ArrayList<>();
        pmd.addAll(findReports(modulePath, "target/sarif", "pmd*.sarif"));
        pmd.addAll(findReports(modulePath, "target", "pmd*.xml"));
        List<ReportFile> spotbugs = new ArrayList<>();
        spotbugs.addAll(findReports(modulePath, "target/sarif", "spotbugs*.sarif"));
        spotbugs.addAll(findReports(modulePath, "target", "spotbugsXml*.xml"));
        List<ReportFile> semgrep = new ArrayList<>();
        semgrep.addAll(findReports(modulePath, "target/sarif", "semgrep*.sarif"));
        semgrep.addAll(findReports(modulePath, "target", "semgrep*.sarif"));
        return new QualityReport(modulePath.getFileName().toString(), surefire, failsafe, pmd, spotbugs, semgrep);
    }

    private List<ReportFile> findReports(Path modulePath, String sub, String glob) {
        Path dir = modulePath.resolve(sub);
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile)
                    .filter(p -> matcher.matches(p.getFileName()))
                    .map(p -> new ReportFile(modulePath.relativize(p).toString(), isBlocker(p)))
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private boolean isBlocker(Path file) {
        try {
            String c = Files.readString(file);
            String l = c.toLowerCase();
            return l.contains("\"level\":\"error\"") || l.contains("blocker") || l.contains("<error") || l.contains("failure");
        } catch (IOException e) {
            return false;
        }
    }

    private void writeReports(Path modulePath, QualityReport report) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(modulePath.resolve("quality-report.json").toFile(), report);
            String html = HtmlRenderer.render(report);
            Files.writeString(modulePath.resolve("quality-report.html"), html);
        } catch (IOException e) {
            // ignore
        }
    }
}
