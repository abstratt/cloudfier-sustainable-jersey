package resource.service_requests;

import resource.util.EntityResourceHelper;
import util.SecurityHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.annotation.security.*;

import java.io.IOException;

import service_requests.*;

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

import userprofile.*;
import service_requests.Resident;
import service_requests.CityOfficial;
import service_requests.SystemAdministrator;

@Path("entities/service_requests.ServiceRequest/")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
public class ServiceRequestResource {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };
    private ServiceRequestService service = new ServiceRequestService();
    @Context
    UriInfo uri;
    @GET
    @PermitAll
    public Response getEntity() {
        try {
            String contents = EntityResourceHelper.getEntityRepresentation("service_requests.ServiceRequest", uri.getRequestUri().resolve("..").toString());
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
            ServiceRequest template = new ServiceRequest(); 
            return status(Response.Status.OK).entity(toExternalRepresentation(template, uri.getRequestUri().resolve(""))).build();
        }
        Long id = Long.parseLong(idString);
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity(Collections.singletonMap("message", "ServiceRequest not found: " + id)).build();
        /*Read: allTautologies*/    
        return status(Response.Status.OK).entity(toFullExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @PUT
    @Path("instances/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
    public Response put(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        try {    
            updateFromExternalRepresentation(found, representation);
        } catch (RuntimeException e) {
            return errorStatus(Response.Status.BAD_REQUEST, e.getMessage()).build();
        }
        if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("Resident")) {
        	Resident asResident = SecurityHelper.getCurrentResident();
        	if (!ServiceRequest.Permissions.canUpdate(asResident, found)) {
        		return status(Response.Status.FORBIDDEN).build();
            }
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }    
        service.update(found);
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @POST
    @Path("instances")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
    public Response post(Map<String, Object> representation) {
        ServiceRequest newInstance = new ServiceRequest();
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
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
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
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response getList(@Context SecurityContext securityContext) {
        Collection<ServiceRequest> models = service.findAll();
        return toExternalList(uri, models).build();
    }
    
    @GET
    @Path("instances/{id}/relationships/serviceRequestType/domain")
    @RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
    public Response listDomainForServiceRequestType(@PathParam("id") Long id) {
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        Collection<ServiceRequestType> domain = new ServiceRequestService().getDomainForServiceRequestType(found);
        return ServiceRequestTypeResource.toExternalList(uri, domain).build();
    }
    @GET
    @Path("instances/{id}/relationships/resident/domain")
    @RolesAllowed({"CityOfficial", "SystemAdministrator", "Resident"})
    public Response listDomainForResident(@PathParam("id") Long id) {
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        Collection<Resident> domain = new ServiceRequestService().getDomainForResident(found);
        return ResidentResource.toExternalList(uri, domain).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/accept")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executeAccept(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.accept();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/reject")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executeReject(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        String reason;
        if (representation.get("reason") != null)
            reason = (String) representation.get("reason");
        else
            return errorStatus(Response.Status.BAD_REQUEST, "Missing argument for required parameter 'reason'").build();
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.reject(reason);
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/complete")
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response executeComplete(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.complete();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/myRequests")
    @RolesAllowed({"SystemAdministrator", "Resident", "CityOfficial"})
    public Response executeMyRequests(Map<String, Object> representation) {
        Collection<ServiceRequest> models = service.myRequests();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/toValidate")
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response executeToValidate(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Collection<ServiceRequest> models = service.toValidate();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/toComplete")
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response executeToComplete(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Collection<ServiceRequest> models = service.toComplete();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/completed")
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response executeCompleted(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Collection<ServiceRequest> models = service.completed();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/byResident")
    @RolesAllowed({"CityOfficial", "SystemAdministrator"})
    public Response executeByResident(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Resident resident;
        if (representation.get("resident") != null)
            resident = Optional.ofNullable(((Map<String, Object>) representation.get("resident"))).map(it -> new ResidentService().find(Long.parseLong((String) it.get("objectId")))).orElse(null);
        else
            return errorStatus(Response.Status.BAD_REQUEST, "Missing argument for required parameter 'resident'").build();
        Collection<ServiceRequest> models = service.byResident(resident);
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
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
            entityCapabilities.add("Create");
            queries.put("myRequests", Arrays.asList("StaticCall"));
        }
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
            entityCapabilities.add("StaticCall");
            entityCapabilities.add("List");
            queries.put("toValidate", Arrays.asList("StaticCall"));
            queries.put("toComplete", Arrays.asList("StaticCall"));
            queries.put("completed", Arrays.asList("StaticCall"));
            queries.put("byResident", Arrays.asList("StaticCall"));
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
            entityCapabilities.add("StaticCall");
            entityCapabilities.add("List");
            queries.put("myRequests", Arrays.asList("StaticCall"));
            queries.put("toValidate", Arrays.asList("StaticCall"));
            queries.put("toComplete", Arrays.asList("StaticCall"));
            queries.put("completed", Arrays.asList("StaticCall"));
            queries.put("byResident", Arrays.asList("StaticCall"));
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
        actions.put("accept", new LinkedHashSet<String>());
        actions.put("reject", new LinkedHashSet<String>());
        actions.put("complete", new LinkedHashSet<String>());
        result.put("relationships", relationships);
        result.put("attributes", attributes);
        
        ServiceRequest found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("ServiceRequest not found: " + id).build();
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
        	Resident asResident = SecurityHelper.getCurrentResident();
            if (ServiceRequest.Permissions.canRead(asResident, found)) {
               instance.add("Read");
            }
            if (ServiceRequest.Permissions.canUpdate(asResident, found)) {
               instance.add("Update");
            }
            if (ServiceRequest.Permissions.canDelete(asResident, found)) {
               instance.add("Delete");
            }
        }
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
        	CityOfficial asCityOfficial = SecurityHelper.getCurrentCityOfficial();
            if (ServiceRequest.Permissions.canRead(asCityOfficial, found)) {
               instance.add("Read");
            }
            if (ServiceRequest.Permissions.canUpdate(asCityOfficial, found)) {
               instance.add("Update");
            }
            if (ServiceRequest.Permissions.canDelete(asCityOfficial, found)) {
               instance.add("Delete");
            }
            // accept
            if (ServiceRequest.Permissions.isAcceptAllowedFor(asCityOfficial, found)) {
                actions.get("accept").add("Call");
            }
            // reject
            if (ServiceRequest.Permissions.isRejectAllowedFor(asCityOfficial, found)) {
                actions.get("reject").add("Call");
            }
            // complete
            if (ServiceRequest.Permissions.isCompleteAllowedFor(asCityOfficial, found)) {
                actions.get("complete").add("Call");
            }
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
        	SystemAdministrator asSystemAdministrator = SecurityHelper.getCurrentSystemAdministrator();
            if (ServiceRequest.Permissions.canRead(asSystemAdministrator, found)) {
               instance.add("Read");
            }
            if (ServiceRequest.Permissions.canUpdate(asSystemAdministrator, found)) {
               instance.add("Update");
            }
            if (ServiceRequest.Permissions.canDelete(asSystemAdministrator, found)) {
               instance.add("Delete");
            }
            // accept
            if (ServiceRequest.Permissions.isAcceptAllowedFor(asSystemAdministrator, found)) {
                actions.get("accept").add("Call");
            }
            // reject
            if (ServiceRequest.Permissions.isRejectAllowedFor(asSystemAdministrator, found)) {
                actions.get("reject").add("Call");
            }
            // complete
            if (ServiceRequest.Permissions.isCompleteAllowedFor(asSystemAdministrator, found)) {
                actions.get("complete").add("Call");
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
    
    public static Response.ResponseBuilder toExternalList(UriInfo uriInfo, Collection<ServiceRequest> models) {
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
    
    private static Map<String, Object> toExternalRepresentation(ServiceRequest toRender, URI instancesURI) {
        return ServiceRequestJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, ServiceRequestJAXRSSerialization.Feature.Values, ServiceRequestJAXRSSerialization.Feature.Links);
    }
    
    private static Map<String, Object> toFullExternalRepresentation(ServiceRequest toRender, URI instancesURI, ServiceRequestJAXRSSerialization.Feature... featureOptions) {
        return ServiceRequestJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, ServiceRequestJAXRSSerialization.Feature.values());
    }
    
    private static void updateFromExternalRepresentation(ServiceRequest toUpdate, Map<String, Object> external) {
        ServiceRequestJAXRSSerialization.updateFromExternalRepresentation(toUpdate, external);
    }
}
