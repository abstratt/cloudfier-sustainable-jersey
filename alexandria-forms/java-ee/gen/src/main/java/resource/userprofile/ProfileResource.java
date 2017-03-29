package resource.userprofile;

import resource.util.EntityResourceHelper;
import util.SecurityHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.annotation.security.*;

import java.io.IOException;

import userprofile.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

import resource.userprofile.*;
import userprofile.*;

import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Path("entities/userprofile.Profile/")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class ProfileResource {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };
    private ProfileService service = new ProfileService();
    @Context
    UriInfo uri;
    @GET
    @PermitAll
    public Response getEntity() {
        try {
            String contents = EntityResourceHelper.getEntityRepresentation("userprofile.Profile", uri.getRequestUri().resolve("..").toString());
            if (contents == null) {
                return Response.status(404).build();
            }
            return Response.ok(contents, MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }
    
    @GET
    @Path("instances/{id}")
    @PermitAll
    public Response getSingle(@Context SecurityContext securityContext, @PathParam("id") String idString) {
        if ("_template".equals(idString)) {
            Profile template = new Profile(); 
            return status(Response.Status.OK).entity(toExternalRepresentation(template, uri.getRequestUri().resolve(""))).build();
        }
        Long id = Long.parseLong(idString);
        Profile found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity(Collections.singletonMap("message", "Profile not found: " + id)).build();
        /*explicitConstraintsPerRole.empty*/    
        return status(Response.Status.OK).entity(toFullExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @PUT
    @Path("instances/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response put(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        Profile found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("Profile not found: " + id).build();
        try {    
            updateFromExternalRepresentation(found, representation);
        } catch (RuntimeException e) {
            return errorStatus(Response.Status.BAD_REQUEST, e.getMessage()).build();
        }
        /*explicitConstraintsPerRole.empty*/    
        service.update(found);
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @POST
    @Path("instances")
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response post(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Profile newInstance = new Profile();
        try {    
            updateFromExternalRepresentation(newInstance, representation);
        } catch (RuntimeException e) {
            return errorStatus(Response.Status.BAD_REQUEST, e.getMessage()).build();
        }    
        service.create(newInstance);
        return status(Response.Status.CREATED).entity(toExternalRepresentation(newInstance, uri.getRequestUri().resolve(newInstance.getId().toString()))).build();
    }
    
    @DELETE
    @Path("instances/{id}")
    @PermitAll
    public Response delete(@Context SecurityContext securityContext, @PathParam("id") Long id) {
        Profile found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("Profile not found: " + id).build();
        /*explicitConstraintsPerRole.empty*/                    
        service.delete(id);    
        return Response.noContent().build();
    }
                    
    @GET
    @Path("instances")
    @PermitAll
    public Response getList(@Context SecurityContext securityContext) {
        Collection<Profile> models = service.findAll();
        return toExternalList(uri, models).build();
    }
    
    
    


    @GET
    @Path("capabilities")
    @PermitAll
    public Response getEntityCapabilities(@Context SecurityContext securityContext) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Collection<String>> queries = new LinkedHashMap<>();
        Map<String, Collection<String>> actions = new LinkedHashMap<>();
        Collection<String> entityCapabilities = new LinkedHashSet<>();
        result.put("target", uri.getRequestUri().resolve("."));
        result.put("queries", queries);
        result.put("actions", actions);
        result.put("entity", entityCapabilities);
        entityCapabilities.add("Create");entityCapabilities.add("StaticCall");entityCapabilities.add("List");
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
        }
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
        }
        return status(Response.Status.OK).entity(result).build();
    }
    
    @GET
    @Path("instances/{id}/capabilities")
    @PermitAll
    public Response getInstanceCapabilities(@Context SecurityContext securityContext, @PathParam("id") Long id) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> instance = new ArrayList<>();
        Map<String, Collection<String>> relationships = new LinkedHashMap<>();
        Map<String, Collection<String>> actions = new LinkedHashMap<>();
        Map<String, Collection<String>> attributes = new LinkedHashMap<>();
        result.put("target", uri.getRequestUri().resolve("."));
        result.put("instance", instance);
        result.put("actions", actions);
        result.put("relationships", relationships);
        result.put("attributes", attributes);
        
        Profile found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("Profile not found: " + id).build();
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
        	CityOfficial asCityOfficial = SecurityHelper.getCurrentCityOfficial();
            if (Profile.Permissions.canRead(asCityOfficial, found)) {
               instance.add("Read");
            }
            if (Profile.Permissions.canUpdate(asCityOfficial, found)) {
               instance.add("Update");
            }
            if (Profile.Permissions.canDelete(asCityOfficial, found)) {
               instance.add("Delete");
            }
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
        	SystemAdministrator asSystemAdministrator = SecurityHelper.getCurrentSystemAdministrator();
            if (Profile.Permissions.canRead(asSystemAdministrator, found)) {
               instance.add("Read");
            }
            if (Profile.Permissions.canUpdate(asSystemAdministrator, found)) {
               instance.add("Update");
            }
            if (Profile.Permissions.canDelete(asSystemAdministrator, found)) {
               instance.add("Delete");
            }
        }
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
        	Resident asResident = SecurityHelper.getCurrentResident();
            if (Profile.Permissions.canRead(asResident, found)) {
               instance.add("Read");
            }
            if (Profile.Permissions.canUpdate(asResident, found)) {
               instance.add("Update");
            }
            if (Profile.Permissions.canDelete(asResident, found)) {
               instance.add("Delete");
            }
        }
        return status(Response.Status.OK).entity(result).build();
    }
    
    private static Response.ResponseBuilder status(Response.Status status) {
        return Response.status(status).type(MediaType.APPLICATION_JSON);
    }
    
    private static Response.ResponseBuilder errorStatus(Response.Status status, String message) {
        return Response.status(status).type(MediaType.APPLICATION_JSON).entity(Collections.singletonMap("message", message));
    }
    
    private static Long parseId(String idStr) {
        String[] components = StringUtils.split(idStr, '@');
        return Long.parseLong(components[components.length - 1]);
    }
    
    public static Response.ResponseBuilder toExternalList(UriInfo uriInfo, Collection<Profile> models) {
        URI extentURI = uriInfo.getRequestUri();
        Collection<Map<String, Object>> items = models.stream().map(toMap -> {
            return toExternalRepresentation(toMap, extentURI);
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("contents", items);
        result.put("offset", 0);
        result.put("length", items.size());  
        return status(Response.Status.OK).entity(result);
    }
    
    private static Map<String, Object> toExternalRepresentation(Profile toRender, URI instancesURI) {
        return ProfileJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, ProfileJAXRSSerialization.Feature.Values, ProfileJAXRSSerialization.Feature.Links);
    }
    
    private static Map<String, Object> toFullExternalRepresentation(Profile toRender, URI instancesURI, ProfileJAXRSSerialization.Feature... featureOptions) {
        return ProfileJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, ProfileJAXRSSerialization.Feature.values());
    }
    
    private static void updateFromExternalRepresentation(Profile toUpdate, Map<String, Object> external) {
        ProfileJAXRSSerialization.updateFromExternalRepresentation(toUpdate, external);
    }
}
