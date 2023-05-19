package io.hyperfoil.tools.regressionBot.actions;

import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.util.Set;

public class ListCmdAction extends BaseAction {

    public static Action instance() {
        return new ListCmdAction();
    }

    @Override
    public void perform(ActionContext context) throws IOException {

        GHIssue issue = context.payload.getIssue();

        Set<String> availableBenchmarks = context.benchmarks.getBechmarksPerRepo(context.payload.getIssue().getRepository().getFullName());

        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append("""
                Available benchmarks are:
                ```
                """);
        availableBenchmarks.forEach(benchmark -> commentBuilder.append(benchmark).append("\n"));
        commentBuilder.append("```");

        issue.comment(commentBuilder.toString());

        super.next(context);
    }
}
