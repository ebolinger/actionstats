package dev.bolinger.actionstats;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bolinger.actionstats.api.Action;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ProducerThread implements Runnable {
    ActionStats stats;
    BlockingQueue<Action> queue;
    int count;
    String[] keys;

    public static final Action SENTINEL = new Action("", Long.MIN_VALUE);

    public ProducerThread(ActionStats stats, BlockingQueue<Action> queue, int count, String[] keys) {
        this.stats = stats;
        this.queue = queue;
        this.count = count;
        this.keys = keys;
    }


    public String getKey(long i) {
        return keys[(int) (Math.abs(i) % keys.length)];
    }


    public void run() {
        Random rnd = new Random();
        ObjectMapper mapper = new ObjectMapper();

        try {
            while (0 < count--) {
                long x = (long) Math.abs(rnd.nextInt());    // Only Natural numbers
                Action obj = new Action(getKey(x), x);
                String json = mapper.writeValueAsString(obj);

                stats.addAction(json);
                queue.put(obj);             // blocks until space is available
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            try {
                queue.put(SENTINEL);        // Signal to the test consumer that we are done
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
