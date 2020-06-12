package dev.bolinger.actionstats;

import com.fasterxml.jackson.databind.JsonMappingException;
import dev.bolinger.actionstats.api.Action;
import dev.bolinger.actionstats.api.ActionResponse;
import dev.bolinger.actionstats.impl.ConcurrentActionStats;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class ApiTest {
    // Share the implementation across tests
    ActionStats stats = new ConcurrentActionStats();

    @Test(expected = IllegalArgumentException.class)
    public void testNull() throws IOException {
        stats.addAction(null);
    }

    @Test(expected = JsonMappingException.class)
    public void testEmptyString() throws IOException {
        stats.addAction("");
    }

    @Test(expected = IOException.class)
    public void testEmptyJson() throws IOException {
        stats.addAction("{ }");
    }

    @Test(expected = IOException.class)
    public void testMissingAction() throws IOException {
        stats.addAction("{ \"time\": 11 }");
    }

    @Test(expected = IOException.class)
    public void testEmptyAction() throws IOException {
        stats.addAction("{ \"action\": \"\", \"time\": 22 }");
    }

    @Test(expected = IOException.class)
    public void testMissingTime() throws IOException {
        stats.addAction("{ \"action\": \"\" }");
    }

    @Test(expected = IOException.class)
    public void testNegativeTime() throws IOException {
        stats.addAction("{ \"action\": \"negative five\", \"time\": -55 }");
    }

    // Jackson is rather lenient with mixed types
    @Test
    public void testTypeAction() throws IOException {
        stats.addAction("{ \"action\": 66, \"time\": 77 }");
    }

    // Jackson is rather lenient with mixed types
    @Test
    public void testTypeTime() throws IOException {
        stats.addAction("{ \"action\": \"eight\", \"time\": \"88\" }");
    }

    @Test
    public void testStats() throws IOException {
        ActionStats stats2 = new ConcurrentActionStats();
        stats2.addAction("{ \"action\":\"jump\", \"time\": 100 }");
        stats2.addAction("{ \"action\":\"run\", \"time\": 75 }");
        stats2.addAction("{ \"action\":\"jump\", \"time\": 200 }");

        assertEquals("[ {\n" +
                "  \"action\" : \"run\",\n" +
                "  \"average\" : 75\n" +
                "}, {\n" +
                "  \"action\" : \"jump\",\n" +
                "  \"average\" : 150\n" +
                "} ]", stats2.getStats());
    }

    @Test
    public void testStrings() {
        Action action = new Action("act", 22L);
        assertEquals("Action: act, 22", action.toString());

        ActionResponse resp = new ActionResponse("resp", 33L);
        assertEquals("ActionResponse: resp, 33", resp.toString());
    }
}
