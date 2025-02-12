package cc.sadalsuud.webipv6supportdetection;

public class IPV6SupportDetectionTest {
    public static void main(String[] args) {
        String ipv6Url = "https://www.sadalsuud.cc";
        String ipv4Url = "https://www.sadalsuud.cc";
        try(IPv6SupportChecker checker = new IPv6SupportChecker(ipv6Url, ipv4Url)) {
            checker.generateReport();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
