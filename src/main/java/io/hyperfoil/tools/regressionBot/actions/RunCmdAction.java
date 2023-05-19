package io.hyperfoil.tools.regressionBot.actions;

import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.util.Set;

public class RunCmdAction extends BaseAction {

    public static Action instance() {
        return new RunCmdAction();
    }

    @Override
    public void perform(ActionContext context) throws IOException {

        GHIssue issue = context.payload.getIssue();

        Set<String> availableBenchmarks = context.benchmarks.getBechmarksPerRepo(context.payload.getIssue().getRepository().getFullName());
        StringBuilder commentBuilder = new StringBuilder();
        if ( availableBenchmarks.contains(context.comment[2])) {
            commentBuilder.append("Awesome! running benchmark: **");
            commentBuilder.append(context.comment[2]);
            commentBuilder.append("**");
            commentBuilder.append("\n\n");

            commentBuilder.append("@").append(context.payload.getComment().getUser().getLogin()).append(" I will ping you when it completes!");


        } else {
            commentBuilder.append(String.format("Could not find benchmark : %s for this repository"));
        }
        issue.comment(commentBuilder.toString());


        super.next(context);
    }
}
