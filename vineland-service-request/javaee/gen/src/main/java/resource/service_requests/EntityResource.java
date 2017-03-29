package resource.service_requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import resource.util.EntityResourceHelper;

@Path("entities")
@Produces("application/json")
public class EntityResource {
	@Context
    UriInfo uri;
	
    @GET
    public Response getList() {
        String[] entityNames = {
            "userprofile.Profile", "service_requests.ServiceRequest", "service_requests.Person", "service_requests.Resident", "service_requests.CityOfficial", "service_requests.SystemAdministrator", "service_requests.ServiceRequestType"
        };
        try {
            List<String> entities = new ArrayList<>();
            for (String entityName : entityNames)
                entities.add(EntityResourceHelper.getEntityRepresentation(entityName, uri.getRequestUri().resolve("..").toString()));
            String result = "[\n" + StringUtils.join(entities, ",\n") + "\n]"; 
            return Response.ok(result, MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }
}