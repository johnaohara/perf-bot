package io.hyperfoil.tools.regressionBot.actions;

import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.util.Set;

public class StatusCmdAction extends BaseAction {

    public static Action instance() {
        return new StatusCmdAction();
    }

    @Override
    public void perform(ActionContext context) throws IOException {

        GHIssue issue = context.payload.getIssue();

        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append("""
                Regression Bot Status:
                ```
                No performance tests for this repo are currently running!
                ```
                """);

        issue.comment(commentBuilder.toString());

        super.next(context);
    }
}
