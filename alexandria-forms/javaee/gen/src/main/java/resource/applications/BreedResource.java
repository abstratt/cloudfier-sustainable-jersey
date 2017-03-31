package resource.applications;

import resource.util.EntityResourceHelper;
import util.SecurityHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.annotation.security.*;

import java.io.IOException;

import applications.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

import resource.alexandria_forms.*;
import resource.userprofile.*;
import userprofile.*;

import alexandria_forms.*;
import userprofile.*;
import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Path("entities/applications.Breed/")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
public class BreedResource {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };
    private BreedService service = new BreedService();
    @Context
    UriInfo uri;
    @GET
    @PermitAll
    public Response getEntity() {
        try {
            String contents = EntityResourceHelper.getEntityRepresentation("applications.Breed", uri.getRequestUri().resolve("..").toString());
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
    @RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
    public Response getSingle(@PathParam("id") String idString) {
        if ("_template".equals(idString)) {
            Breed template = new Breed(); 
            return status(Response.Status.OK).entity(toExternalRepresentation(template, uri.getRequestUri().resolve(""))).build();
        }
        Long id = Long.parseLong(idString);
        Breed found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity(Collections.singletonMap("message", "Breed not found: " + id)).build();
        /*Read: allTautologies*/    
        return status(Response.Status.OK).entity(toFullExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @PUT
    @Path("instances/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response put(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        Breed found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("Breed not found: " + id).build();
        try {    
            updateFromExternalRepresentation(found, representation);
        } catch (RuntimeException e) {
            return errorStatus(Response.Status.BAD_REQUEST, e.getMessage()).build();
        }
        if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }    
        service.update(found);
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @POST
    @Path("instances")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response post(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Breed newInstance = new Breed();
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
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response delete(@Context SecurityContext securityContext, @PathParam("id") Long id) {
        Breed found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("Breed not found: " + id).build();
        if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }                    
        service.delete(id);    
        return Response.noContent().build();
    }
                    
    @GET
    @Path("instances")
    @RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
    public Response getList() {
        Collection<Breed> models = service.findAll();
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
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
            entityCapabilities.add("Create");
            entityCapabilities.add("StaticCall");
            entityCapabilities.add("List");
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
            entityCapabilities.add("Create");
            entityCapabilities.add("StaticCall");
            entityCapabilities.add("List");
        }
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
            entityCapabilities.add("List");
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
        
        Breed found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("Breed not found: " + id).build();
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
        	CityOfficial asCityOfficial = SecurityHelper.getCurrentCityOfficial();
            if (Breed.Permissions.canRead(asCityOfficial, found)) {
               instance.add("Read");
            }
            if (Breed.Permissions.canUpdate(asCityOfficial, found)) {
               instance.add("Update");
            }
            if (Breed.Permissions.canDelete(asCityOfficial, found)) {
               instance.add("Delete");
            }
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
        	SystemAdministrator asSystemAdministrator = SecurityHelper.getCurrentSystemAdministrator();
            if (Breed.Permissions.canRead(asSystemAdministrator, found)) {
               instance.add("Read");
            }
            if (Breed.Permissions.canUpdate(asSystemAdministrator, found)) {
               instance.add("Update");
            }
            if (Breed.Permissions.canDelete(asSystemAdministrator, found)) {
               instance.add("Delete");
            }
        }
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
        	Resident asResident = SecurityHelper.getCurrentResident();
            if (Breed.Permissions.canRead(asResident, found)) {
               instance.add("Read");
            }
            if (Breed.Permissions.canUpdate(asResident, found)) {
               instance.add("Update");
            }
            if (Breed.Permissions.canDelete(asResident, found)) {
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
    
    public static Response.ResponseBuilder toExternalList(UriInfo uriInfo, Collection<Breed> models) {
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
    
    private static Map<String, Object> toExternalRepresentation(Breed toRender, URI instancesURI) {
        return BreedJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, BreedJAXRSSerialization.Feature.Values, BreedJAXRSSerialization.Feature.Links);
    }
    
    private static Map<String, Object> toFullExternalRepresentation(Breed toRender, URI instancesURI, BreedJAXRSSerialization.Feature... featureOptions) {
        return BreedJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, BreedJAXRSSerialization.Feature.values());
    }
    
    private static void updateFromExternalRepresentation(Breed toUpdate, Map<String, Object> external) {
        BreedJAXRSSerialization.updateFromExternalRepresentation(toUpdate, external);
    }
}
