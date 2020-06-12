package dev.bolinger.actionstats.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Models an Action in our API.
 * This provides a bit of type safety & rejects invalid or missing input.
 */

@JsonInclude(NON_NULL)
public class Action {
    private String action;
    private long time;

    // No-arg constructor, useful for JSON library
    public Action() {
        action = "";
        time = 0L;
    }

    // Primary Constructor
    public Action(String action, long time) {
        this.action = action;
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getAction() + ", " + getTime();
    }
}
