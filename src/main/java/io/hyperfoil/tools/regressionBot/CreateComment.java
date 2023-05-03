package io.hyperfoil.tools.regressionBot;

import io.quarkiverse.githubapp.event.Issue;
import org.kohsuke.github.GHEventPayload;

import java.io.IOException;

public class CreateComment {

    void onOpen(@Issue.Opened GHEventPayload.Issue issuePayload) throws IOException {
        issuePayload.getIssue().comment("Hello from my GitHub App");
    }
}
