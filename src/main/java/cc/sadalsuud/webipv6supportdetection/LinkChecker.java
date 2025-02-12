package cc.sadalsuud.webipv6supportdetection;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class LinkChecker {
    // 检查网站二级和三级内链的IPv6支持
    public double checkInnerLinksIPv6Support(ExecutorService executor, List<String> currentLinks) throws InterruptedException {
        List<Callable<Boolean>> checkTasks = currentLinks.stream()
                .map(link -> (Callable<Boolean>) () -> checkPageIPv6Support(link))
                .collect(Collectors.toList());

        List<Future<Boolean>> results =executor.invokeAll(checkTasks);

        long successfulLinks = results.stream().filter(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }).count();

        return currentLinks.isEmpty() ? 0 : (double) successfulLinks / currentLinks.size();
    }

    private boolean checkPageIPv6Support(String url) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(url);
            for (InetAddress address : addresses) {
                if (address instanceof Inet6Address) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
