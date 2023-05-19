package io.hyperfoil.tools.regressionBot.ghEvents;

import io.hyperfoil.tools.regressionBot.actions.*;
import io.hyperfoil.tools.regressionBot.benchmark.Benchmarks;
import io.hyperfoil.tools.regressionBot.util.EventCache;
import io.hyperfoil.tools.regressionBot.util.EventCacheProducer;
import io.quarkiverse.githubapp.GitHubEvent;
import io.quarkiverse.githubapp.event.InstallationRepositories;
import io.quarkiverse.githubapp.event.IssueComment;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.kohsuke.github.GHEventPayload;

import java.io.IOException;

@ApplicationScoped
public class GithubEventHandler {
    private static final Logger LOG = Logger.getLogger(EventCacheProducer.class);

    @ConfigProperty(name = "perf-bot.prompt")
    String prompt;

    @ConfigProperty(name = "perf-bot.message.no-pr")
    String msgNotPr;

    @Inject
    EventCache cache;

    @Inject
    Benchmarks benchmarks;


    //observe and cache the raw Events
    void rawEventObserve(@Observes GitHubEvent event) {
        LOG.debugf("Received Event: %s", event.getPayload());
        JsonObject payload = new JsonObject(event.getPayload());
        if (payload != null && payload.getJsonObject("comment") != null) {
            cache.put(Long.toString(payload.getJsonObject("comment").getLong("id")), payload);
        }
    }


    void onInstallation(@InstallationRepositories.Added GHEventPayload.InstallationRepositories installationPayload) throws IOException {
        //tood - something when bot is installed in a new repo
    }


    void onIssueComment(@IssueComment.Created GHEventPayload.IssueComment issueComment) throws IOException {

        String[] comment = issueComment.getComment().getBody().split(" "); //TODO: impl natural language parsing of command(s)
        ActionContext context = new ActionContext();
        context.benchmarks = benchmarks;
        context.payload = issueComment;
        context.comment = comment;

        if ( comment.length > 0  && comment[0].equals(prompt)) { //ignore all comments not starting with `/regression-bot`
            if (!issueComment.getIssue().isPullRequest()) {
                issueComment.getIssue().comment(msgNotPr);
            } else {
                Action actionChain = ValidateUser.instance();
                Action cmdAction = actionChain.then(CommandAction.instance());
                cmdAction.then(CommentAction.instance(null));

                actionChain.perform(context);
            }
        }
    }
}
