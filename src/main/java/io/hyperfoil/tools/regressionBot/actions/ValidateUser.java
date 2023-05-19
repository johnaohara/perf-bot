package io.hyperfoil.tools.regressionBot.actions;

import io.hyperfoil.tools.regressionBot.benchmark.Benchmark;
import org.jboss.logging.Logger;

import java.io.IOException;
public class ValidateUser extends BaseAction {

    private static final Logger logger = Logger.getLogger(ValidateUser.class);

    public static Action instance(){
        return new ValidateUser();
    }

    @Override
    public void perform(ActionContext context) throws IOException {
        String userName = context.payload.getIssue().getUser().getLogin();
        String repo = context.payload.getIssue().getRepository().getFullName();
        if ( context.benchmarks.containsRepo(repo) ) {
            //TODO: this needs to resolve benchmark && repo to validate user
            //there might be multiple benchmarks per repo with different authorization lists
            //need to consider what we return to user who only has access to a subset of benchmarks
            Benchmark benchmark = context.benchmarks.getBenchmark(repo);
            if (benchmark.getAuthorizedUsers().contains(userName)) {
                super.next(context);
            } else {
                logger.warnf("User (%s) not authorized for repository (%s)", userName, repo);
                context.payload.getIssue().comment(String.format("I am sorry %s, you are not authorized to run performance tests against this repository", userName));
            }
        } else {
            logger.infof("Repository (%s) not registered with regression-bot", repo);
        }
    }
}
