package io.hyperfoil.tools.regressionBot.actions;

import io.hyperfoil.tools.regressionBot.benchmark.Benchmarks;
import org.kohsuke.github.GHEventPayload;

public class ActionContext {

    public String[] comment;
    public GHEventPayload.IssueComment payload;
    public Benchmarks benchmarks;
    public String response;
}
