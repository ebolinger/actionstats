package dev.bolinger.actionstats;

import java.io.IOException;

public interface ActionStats {
    void addAction(String json) throws IOException;

    String getStats();
}
