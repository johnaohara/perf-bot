package io.hyperfoil.tools.regressionBot;

import io.quarkiverse.githubapp.event.Issue;
import io.quarkiverse.githubapp.event.PullRequestReviewComment;
import org.kohsuke.github.GHEventPayload;

import java.io.IOException;

public class PullRequestRegressionTest {

    void onPrComment(@PullRequestReviewComment.Created  GHEventPayload.PullRequestReviewComment pullRequestReviewComment) throws IOException {
        if (pullRequestReviewComment.getComment().getBodyText().startsWith("/regression-bot")) {
            pullRequestReviewComment.getPullRequest().comment("OK! starting performance regresssion test!");
        }
    }

}
