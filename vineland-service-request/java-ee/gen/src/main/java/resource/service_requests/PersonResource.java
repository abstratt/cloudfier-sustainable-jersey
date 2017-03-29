package resource.service_requests;

import resource.util.EntityResourceHelper;
import util.SecurityHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.annotation.security.*;

import java.io.IOException;


@Path("entities/service_requests.Person/")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class PersonResource {
    @Context
    UriInfo uri;
    @GET
    @PermitAll
    public Response getEntity() {
        try {
            String contents = EntityResourceHelper.getEntityRepresentation("service_requests.Person", uri.getRequestUri().resolve("..").toString());
            if (contents == null) {
                return Response.status(404).build();
            }
            return Response.ok(contents, MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }
    
}
