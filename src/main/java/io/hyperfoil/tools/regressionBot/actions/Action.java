package io.hyperfoil.tools.regressionBot.actions;

import io.hyperfoil.tools.regressionBot.benchmark.Benchmarks;
import org.kohsuke.github.GHEventPayload;

import java.io.IOException;

public interface Action {
    void perform(ActionContext context) throws IOException;
    Action then(Action action);

}
