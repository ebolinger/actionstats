package dev.bolinger.actionstats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bolinger.actionstats.api.Action;
import dev.bolinger.actionstats.api.ActionResponse;
import dev.bolinger.actionstats.impl.ConcurrentActionStats;
import junit.framework.TestCase;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConcurrentTest extends TestCase {

    static final String[] ONE_KEY = new String[] { "one-key" };
    static final String[] MANY_KEYS = new String[] { "run", "jump", "fly", "sit", "squat","crouch", "fall" };
    static final Pair<Long,Long> ZERO = new Pair(0L, 0L);

    public void testOneKey() {
        driver(ONE_KEY, 50, 20000);
    }

    public void testManyKeys() {
        driver(MANY_KEYS, 10, 20000);
    }

    public void testSingleProducer() {
        driver(MANY_KEYS, 1, 20000);
    }

    public void testTooManyThreads() {
        driver(MANY_KEYS, 100, 20000);
    }


    /**
     * These concurrency tests have the same "shape".  So parameterize them accordingly.
     * @param keys array of 1 or more keys
     * @param numThreads number of Java threads to spwan during this test
     * @param numActions total number of actions to generate, spread across all threads
     */
    public void driver(String[] keys, int numThreads, int numActions) {

        // Test subject
        ActionStats stats = new ConcurrentActionStats();

        // Collects all actions generated during the tests
        BlockingQueue<Action> actionQueue = new ArrayBlockingQueue<Action>(50);

        int threadCount = 0;
        while (threadCount < numThreads) {
            threadCount++;
            Thread t = new Thread(new ProducerThread(stats, actionQueue, numActions / numThreads, keys));
            t.start();
        }

        // This consumer is single-threaded, so run a similar algorithm without concurrency.
        HashMap<String, Pair<Long,Long>> log = new HashMap<String,Pair<Long,Long>>();

        while (0 < threadCount) {
            try {
                // Wait until data is available
                Action action = actionQueue.take();
                if (action.getTime() == ProducerThread.SENTINEL.getTime()) {
                    threadCount--;
                } else {
                    // Update both the count and sum for an entry
                    Pair<Long,Long> entry = log.getOrDefault(action.getAction(), ZERO);
                    Long count = entry.getValue0() + 1;
                    Long sum   = entry.getValue1() + action.getTime();
                    Pair<Long,Long> updated = new Pair(count, sum);

                    log.put(action.getAction(), updated);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String json = stats.getStats();
        ObjectMapper mapper = new ObjectMapper();
        ActionResponse[] measurement = new ActionResponse[0];
        try {
            measurement = mapper.readValue(json, ActionResponse[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(keys.length, measurement.length);

        // We have all of the expected keys, so verify their metrics
        for (ActionResponse m : measurement) {
            Pair<Long,Long> entry = log.get(m.getAction());
            Long average = entry.getValue1() / entry.getValue0();
            assertEquals(average.longValue(), m.getAverage());
        }
    }
}

