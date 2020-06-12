package dev.bolinger.actionstats.api;

/**
 * Models an Action API response.
 */

public class ActionResponse {
    private String action;
    private long average;

    // No-arg constructor, useful for JSON library
    public ActionResponse() {
        action = "";
        average = 0L;
    }

    // Primary Constructor
    public ActionResponse(String action, long average) {
        this.action = action;
        this.average = average;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getAverage() {
        return average;
    }

    public void setAverage(long average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getAction() + ", " + getAverage();
    }
}
