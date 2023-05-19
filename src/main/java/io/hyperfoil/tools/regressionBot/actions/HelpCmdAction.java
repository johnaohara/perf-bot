package io.hyperfoil.tools.regressionBot.actions;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import org.kohsuke.github.GHIssue;

import java.io.IOException;

public class HelpCmdAction extends BaseAction {

    @Location("reports/v1/help")
    Template helpTemplate;

    public static Action instance() {
        return new HelpCmdAction();
    }

    @Override
    public void perform(ActionContext context) throws IOException {

        GHIssue issue = context.payload.getIssue();

        String comment = helpTemplate.render();

        issue.comment(comment);

        super.next(context);
    }
}
