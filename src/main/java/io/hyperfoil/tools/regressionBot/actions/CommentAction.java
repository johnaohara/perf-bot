package io.hyperfoil.tools.regressionBot.actions;

import java.io.IOException;

public class CommentAction extends BaseAction {

    private String msg;

    public static Action instance(String msg) {
        CommentAction action = new CommentAction();
        action.msg = msg;
        return action;
    }

    @Override
    public void perform(ActionContext context) throws IOException {
        if ( msg != null ) {
            context.payload.getIssue().comment(msg);
        } else if(context.response != null) {
            context.payload.getIssue().comment(context.response);
        }
        super.next(context);
    }
}
