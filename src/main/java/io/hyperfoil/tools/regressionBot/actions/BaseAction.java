package io.hyperfoil.tools.regressionBot.actions;

import java.io.IOException;

public abstract class BaseAction implements Action {
    Action next;

    @Override
    public Action then(Action then) {
        this.next = then;
        return this.next;
    }

    void next(ActionContext context) throws IOException {
        if ( next != null ){
            next.perform(context);
        }
    }


}
