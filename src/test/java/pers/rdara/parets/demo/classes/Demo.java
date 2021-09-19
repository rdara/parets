package pers.rdara.parets.demo.classes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Ramesh Dara
 * @since Sep-2021
 */
public class Demo {
    private static Map<HttpMethod, Integer> failureCounterMap = new ConcurrentHashMap<>();

    //Simulate a Http Call delaying by 5 seconds and always return lower case HttpMethod name.
    public static String delayedHttpCall(HttpMethod method) {
        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return method.name().toLowerCase();
    }

    //Simulate flaky test that would fail twice and would succeed, if repeated for 3 times in total.
    //Will take 2+2+2 = 6 secs to succeed and retry would take 2 seconds.
    public static String failTwice(HttpMethod method) {
        int retries = failureCounterMap.compute( method, (key, value) -> {
            value = ((value == null ? -1  : value) + 1) % 3;
            return value;
        });
        try {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return retries == 2 ? method.name().toLowerCase() : "Failing with retry: " + retries;
    }
}
