package cc.sadalsuud.webipv6supportdetection;

public class ReportGenerator {


    private final StringBuilder report;

    public ReportGenerator() {
        report = new StringBuilder();
    }

    public void addSection(String title, String content) {
        report.append(title).append(": ").append(content).append("\n");
    }

    public void printReport() {
        System.out.println(report);
    }

}
