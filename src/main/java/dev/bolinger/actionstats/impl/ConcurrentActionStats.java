package dev.bolinger.actionstats.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dev.bolinger.actionstats.ActionStats;
import dev.bolinger.actionstats.api.Action;
import dev.bolinger.actionstats.api.ActionResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConcurrentActionStats implements ActionStats {

    ConcurrentHashMap<String, ActionMetric> stats = new ConcurrentHashMap<String, ActionMetric>();

    /**
     * Add one action to the data set
     * @param json string representation of an action with time
     * @throws JsonProcessingException | IOException
     */
    @Override
    public void addAction(String json) throws IOException {
        // parse JSON to get an action
        ObjectMapper mapper = new ObjectMapper();
        Action sample = mapper.readValue(json, Action.class);

        // Reject any empty action string (Jackson library rejects null)
        if (0 == sample.getAction().length()) {
            throw new IOException("action must be defined");
        }

        // Reject negative time values
        if (sample.getTime() < 0L) {
            throw new IOException("time must be zero or greater");
        }

        // Uses java version 1.8 or higher
        // OpenJDK version 9 fixes a performance bug:
        // https://bugs.openjdk.java.net/browse/JDK-8161372
        stats.computeIfAbsent(sample.getAction(), (x) -> new ActionMetric())
                .add(sample.getTime());
    }


    /**
     * Report a snapshot of the current state.
     *
     * @return String representation of the internal statistics
     */
    @Override
    public String getStats() {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

        // map entries into JSON
        List<ActionResponse> objs = stats.entrySet()
                .stream()
                .map( (e) -> new ActionResponse(e.getKey(), e.getValue().getAverage()))
                .collect(Collectors.toList());

        String result;
        try {
            result = writer.writeValueAsString(objs);
        } catch (JsonProcessingException e) {
            result = "";    // Empty results for any JSON error (different from an empty array)
        }

        return result;
    }
}