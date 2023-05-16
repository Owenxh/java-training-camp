package io.github.owenxh.irina.client.example;

import io.github.owenxh.irina.client.ConfigChangedEntry;
import io.github.owenxh.irina.client.IrinaTemplate;
import io.github.owenxh.irina.client.LifeCycle;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Client demo
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class ClientDemo {

    public static void main(String[] args) throws InterruptedException {
        IrinaTemplate irinaTemplate = new IrinaTemplate("http://localhost:8080");

        // Get config
        Map<String, String> kvs = irinaTemplate.getConfig("user.json");
        kvs.forEach((k, v) -> System.out.printf("%s = %s\n", k, v));

        LifeCycle lifeCycle = irinaTemplate.watchConfig("user.json", entries -> {
            for (ConfigChangedEntry entry : entries) {
                System.out.printf("key [%s] changed from [%s] = [%s]\n",
                        entry.getKey(), entry.getOldValue(), entry.getValue());
            }
        });

        // start watch
        lifeCycle.start();

        TimeUnit.MINUTES.sleep(2);

        // stop watch
        lifeCycle.close();
    }
}
