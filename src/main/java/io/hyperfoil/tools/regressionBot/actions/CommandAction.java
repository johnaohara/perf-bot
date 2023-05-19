package io.hyperfoil.tools.regressionBot.actions;

import java.io.IOException;

public class CommandAction extends BaseAction {

    public static Action instance() {
        return new CommandAction();
    }

    @Override
    public void perform(ActionContext context) throws IOException {
        String cmd = context.comment[1];
        Action cmdAction = null;
        switch (cmd) {
            case "status":
                cmdAction = StatusCmdAction.instance();
                break;
            case "run":
                cmdAction = RunCmdAction.instance();
                break;
            case "list":
                cmdAction = ListCmdAction.instance();
                break;
            case "help":
                cmdAction = HelpCmdAction.instance();
                break;
            default:
                ;
        }
        if (cmdAction != null) {
            cmdAction.perform(context);
        }
        super.next(context);
    }
}
