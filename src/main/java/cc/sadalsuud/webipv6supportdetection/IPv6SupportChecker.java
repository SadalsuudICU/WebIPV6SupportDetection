package cc.sadalsuud.webipv6supportdetection;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class IPv6SupportChecker implements AutoCloseable {

    private static final int MULTICAST_ATTEMPTS = 5;
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);
    private static final int CONNECTION_TIMEOUT = 3000;

    private final String ipv6Url;
    private final String ipv4Url;
    private final String domain;

    public IPv6SupportChecker(String ipv6Url) {
        this(ipv6Url, ipv6Url);
    }

    public IPv6SupportChecker(String ipv6Url, String ipv4Url) {
        this.ipv6Url = ipv6Url;
        this.ipv4Url = ipv4Url;
        this.domain = extractDomain(ipv6Url);
        if (domain == null) {
            throw new IllegalArgumentException("Invalid domain");
        }
    }

    /**
     * Extract the domain from the IPv6 URL.
     *
     * @param ipv6Url The IPv6 URL.
     * @return The domain extracted from the URL.
     */
    private String extractDomain(String ipv6Url) {
        try {
            return new URI(ipv6Url).getHost();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + ipv6Url);
        }
    }

    /**
     * Check if the domain has an AAAA record (IPv6 address).
     *
     * @return true if the domain has an AAAA record, false otherwise.
     */
    public boolean hasAAAARecord() {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            for (InetAddress address : addresses) {
                if (address instanceof Inet6Address) {
                    return true;
                }
            }
        } catch (UnknownHostException e) {
            return false;
        }
        return false;
    }

    /**
     * Check the IPv6 support of the website by accessing its homepage multiple times.
     *
     * @param attempts The number of times to attempt accessing the homepage.
     * @return The success rate of accessing the homepage with IPv6.
     */
    public double checkPageIPv6Support(int attempts) {
        int successCount = 0;
        for (int i = 0; i < attempts; i++) {
            if (checkPageIPv6Support()) {
                successCount++;
            }
        }
        return (double) successCount / attempts;
    }

    /**
     * Check the IPv6 support of the website by accessing its homepage.
     *
     * @return true if the homepage can be accessed with IPv6, false otherwise.
     */
    private boolean checkPageIPv6Support() {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            for (InetAddress address : addresses) {
                if (address instanceof Inet6Address) {
                    HttpURLConnection connection = (HttpURLConnection) new URL(ipv6Url).openConnection();
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    return connection.getResponseCode() == 200;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Check the consistency of the IPv6 and IPv4 content by comparing their similarity.
     *
     * @return The similarity score between the IPv6 and IPv4 content.
     */
    public double checkContentSimilarity() {
        try {
            Document ipv6Doc = Jsoup.connect(ipv6Url).get();
            Document ipv4Doc = Jsoup.connect(ipv4Url).get();

            String ipv6Text = ipv6Doc.text();
            String ipv4Text = ipv4Doc.text();

            Set<String> ipv6Words = new HashSet<>(Arrays.asList(ipv6Text.split("\\s+")));
            Set<String> ipv4Words = new HashSet<>(Arrays.asList(ipv4Text.split("\\s+")));

            int intersectionSize = (int) ipv6Words.stream().filter(ipv4Words::contains).count();
            int unionSize = ipv6Words.size() + ipv4Words.size() - intersectionSize;

            return (double) intersectionSize / unionSize;
        } catch (IOException e) {
            return 0.0;
        }
    }

    /**
     * Generate a report summarizing the IPv6 support analysis.
     */
    public void generateReport() {
        System.out.println("IPv6 Support Report for Domain: " + domain);
        System.out.println("==============================================");

        System.out.println("AAAA Record: " + (hasAAAARecord() ? "Supported" : "Not Supported"));
        double ipv6SuccessRate = checkPageIPv6Support(MULTICAST_ATTEMPTS);
        System.out.println("IPv6 Homepage Access Success Rate: " + ipv6SuccessRate * 100 + "%");
        System.out.println("IPv6 and IPv4 Content Consistency: " + checkContentSimilarity() * 100 + "%");

        System.out.println("==============================================");
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }
}

