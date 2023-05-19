package io.hyperfoil.tools.regressionBot.svc;


import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("horreum")
public class HorreumCallback {

    @POST
    @Path("/callback")
    public void processHorreumCallback(){

    }
}
