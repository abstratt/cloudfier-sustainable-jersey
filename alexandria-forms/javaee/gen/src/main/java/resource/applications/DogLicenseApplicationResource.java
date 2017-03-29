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

import userprofile.*;
import applications.CityOfficial;
import applications.SystemAdministrator;
import applications.Resident;

@Path("entities/applications.DogLicenseApplication/")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
public class DogLicenseApplicationResource {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };
    private DogLicenseApplicationService service = new DogLicenseApplicationService();
    @Context
    UriInfo uri;
    @GET
    @PermitAll
    public Response getEntity() {
        try {
            String contents = EntityResourceHelper.getEntityRepresentation("applications.DogLicenseApplication", uri.getRequestUri().resolve("..").toString());
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
    @RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
    public Response getSingle(@Context SecurityContext securityContext, @PathParam("id") String idString) {
        if ("_template".equals(idString)) {
            DogLicenseApplication template = new DogLicenseApplication(); 
            return status(Response.Status.OK).entity(toExternalRepresentation(template, uri.getRequestUri().resolve(""))).build();
        }
        Long id = Long.parseLong(idString);
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity(Collections.singletonMap("message", "DogLicenseApplication not found: " + id)).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else if (securityContext.isUserInRole("Resident")) {
        	Resident asResident = SecurityHelper.getCurrentResident();
        	if (!DogLicenseApplication.Permissions.canRead(asResident, found)) {
        		return status(Response.Status.FORBIDDEN).build();
            }
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }    
        return status(Response.Status.OK).entity(toFullExternalRepresentation(found, uri.getRequestUri().resolve(""))).build();
    }
    
    @PUT
    @Path("instances/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
    public Response put(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        try {    
            updateFromExternalRepresentation(found, representation);
        } catch (RuntimeException e) {
            return errorStatus(Response.Status.BAD_REQUEST, e.getMessage()).build();
        }
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else if (securityContext.isUserInRole("Resident")) {
        	Resident asResident = SecurityHelper.getCurrentResident();
        	if (!DogLicenseApplication.Permissions.canUpdate(asResident, found)) {
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
    @RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
    public Response post(Map<String, Object> representation) {
        DogLicenseApplication newInstance = new DogLicenseApplication();
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
    @RolesAllowed({"SystemAdministrator"})
    public Response delete(@Context SecurityContext securityContext, @PathParam("id") Long id) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }                    
        service.delete(id);    
        return Response.noContent().build();
    }
                    
    @GET
    @Path("instances")
    @RolesAllowed({"SystemAdministrator"})
    public Response getList(@Context SecurityContext securityContext) {
        Collection<DogLicenseApplication> models = service.findAll();
        return toExternalList(uri, models).build();
    }
    
    @GET
    @Path("instances/{id}/relationships/dogOwner/domain")
    @RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
    public Response listDomainForDogOwner(@PathParam("id") Long id) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        Collection<Resident> domain = new DogLicenseApplicationService().getDomainForDogOwner(found);
        return ResidentResource.toExternalList(uri, domain).build();
    }
    @GET
    @Path("instances/{id}/relationships/breed/domain")
    @RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
    public Response listDomainForBreed(@PathParam("id") Long id) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        Collection<Breed> domain = new DogLicenseApplicationService().getDomainForBreed(found);
        return BreedResource.toExternalList(uri, domain).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/submit")
    @RolesAllowed({"SystemAdministrator", "CityOfficial", "Resident"})
    public Response executeSubmit(@PathParam("id") Long id, Map<String, Object> representation) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        /*Call: allTautologies*/
        found.submit();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/receivePayment")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executeReceivePayment(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.receivePayment();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/reject")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executeReject(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.reject();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/approve")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executeApprove(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.approve();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("instances/{id}/actions/cancel")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executeCancel(@Context SecurityContext securityContext, @PathParam("id") Long id, Map<String, Object> representation) {
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        if (securityContext.isUserInRole("SystemAdministrator")) {
        	// no further checks
        } else if (securityContext.isUserInRole("CityOfficial")) {
        	// no further checks
        } else {
        	return status(Response.Status.FORBIDDEN).build();
        }
        found.cancel();
        return status(Response.Status.OK).entity(toExternalRepresentation(found, uri.getRequestUri().resolve(".."))).build();
    }
    
    

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/myRequests")
    @RolesAllowed({"SystemAdministrator", "Resident", "CityOfficial"})
    public Response executeMyRequests(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Collection<DogLicenseApplication> models = service.myRequests();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/pendingPayment")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executePendingPayment(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Collection<DogLicenseApplication> models = service.pendingPayment();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/pendingApproval")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executePendingApproval(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Collection<DogLicenseApplication> models = service.pendingApproval();
        return toExternalList(uri, models).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("finders/paidWithinPeriod")
    @RolesAllowed({"SystemAdministrator", "CityOfficial"})
    public Response executePaidWithinPeriod(@Context SecurityContext securityContext, Map<String, Object> representation) {
        Date start = null;
        if (representation.get("start") != null)
            try {
                start = DateUtils.parseDate((String) representation.get("start"), DATE_FORMATS);
            } catch (ParseException e) {
                throw new ConversionException("Invalid format for date in 'start': " + representation.get("start"));
            }
        
        Date end_ = null;
        if (representation.get("end_") != null)
            try {
                end_ = DateUtils.parseDate((String) representation.get("end_"), DATE_FORMATS);
            } catch (ParseException e) {
                throw new ConversionException("Invalid format for date in 'end_': " + representation.get("end_"));
            }
        Collection<DogLicenseApplication> models = service.paidWithinPeriod(start, end_);
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
            queries.put("myRequests", Arrays.asList("StaticCall"));
            queries.put("pendingPayment", Arrays.asList("StaticCall"));
            queries.put("pendingApproval", Arrays.asList("StaticCall"));
            queries.put("paidWithinPeriod", Arrays.asList("StaticCall"));
            queries.put("dogOwners", Arrays.asList("StaticCall"));
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
            entityCapabilities.add("Create");
            entityCapabilities.add("StaticCall");
            entityCapabilities.add("List");
            queries.put("myRequests", Arrays.asList("StaticCall"));
            queries.put("pendingPayment", Arrays.asList("StaticCall"));
            queries.put("pendingApproval", Arrays.asList("StaticCall"));
            queries.put("paidWithinPeriod", Arrays.asList("StaticCall"));
            queries.put("dogOwners", Arrays.asList("StaticCall"));
        }
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
            entityCapabilities.add("Create");
            queries.put("myRequests", Arrays.asList("StaticCall"));
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
        actions.put("submit", new LinkedHashSet<String>());
        actions.put("receivePayment", new LinkedHashSet<String>());
        actions.put("reject", new LinkedHashSet<String>());
        actions.put("approve", new LinkedHashSet<String>());
        actions.put("cancel", new LinkedHashSet<String>());
        result.put("relationships", relationships);
        result.put("attributes", attributes);
        
        DogLicenseApplication found = service.find(id);
        if (found == null)
            return status(Response.Status.NOT_FOUND).entity("DogLicenseApplication not found: " + id).build();
        if (securityContext.isUserInRole(CityOfficial.ROLE_ID)) {
        	CityOfficial asCityOfficial = SecurityHelper.getCurrentCityOfficial();
            if (DogLicenseApplication.Permissions.canRead(asCityOfficial, found)) {
               instance.add("Read");
            }
            if (DogLicenseApplication.Permissions.canUpdate(asCityOfficial, found)) {
               instance.add("Update");
            }
            if (DogLicenseApplication.Permissions.canDelete(asCityOfficial, found)) {
               instance.add("Delete");
            }
            // submit
            if (DogLicenseApplication.Permissions.isSubmitAllowedFor(asCityOfficial, found)) {
                actions.get("submit").add("Call");
            }
            // receivePayment
            if (DogLicenseApplication.Permissions.isReceivePaymentAllowedFor(asCityOfficial, found)) {
                actions.get("receivePayment").add("Call");
            }
            // reject
            if (DogLicenseApplication.Permissions.isRejectAllowedFor(asCityOfficial, found)) {
                actions.get("reject").add("Call");
            }
            // approve
            if (DogLicenseApplication.Permissions.isApproveAllowedFor(asCityOfficial, found)) {
                actions.get("approve").add("Call");
            }
            // cancel
            if (DogLicenseApplication.Permissions.isCancelAllowedFor(asCityOfficial, found)) {
                actions.get("cancel").add("Call");
            }
        }
        if (securityContext.isUserInRole(SystemAdministrator.ROLE_ID)) {
        	SystemAdministrator asSystemAdministrator = SecurityHelper.getCurrentSystemAdministrator();
            if (DogLicenseApplication.Permissions.canRead(asSystemAdministrator, found)) {
               instance.add("Read");
            }
            if (DogLicenseApplication.Permissions.canUpdate(asSystemAdministrator, found)) {
               instance.add("Update");
            }
            if (DogLicenseApplication.Permissions.canDelete(asSystemAdministrator, found)) {
               instance.add("Delete");
            }
            // submit
            if (DogLicenseApplication.Permissions.isSubmitAllowedFor(asSystemAdministrator, found)) {
                actions.get("submit").add("Call");
            }
            // receivePayment
            if (DogLicenseApplication.Permissions.isReceivePaymentAllowedFor(asSystemAdministrator, found)) {
                actions.get("receivePayment").add("Call");
            }
            // reject
            if (DogLicenseApplication.Permissions.isRejectAllowedFor(asSystemAdministrator, found)) {
                actions.get("reject").add("Call");
            }
            // approve
            if (DogLicenseApplication.Permissions.isApproveAllowedFor(asSystemAdministrator, found)) {
                actions.get("approve").add("Call");
            }
            // cancel
            if (DogLicenseApplication.Permissions.isCancelAllowedFor(asSystemAdministrator, found)) {
                actions.get("cancel").add("Call");
            }
        }
        if (securityContext.isUserInRole(Resident.ROLE_ID)) {
        	Resident asResident = SecurityHelper.getCurrentResident();
            if (DogLicenseApplication.Permissions.canRead(asResident, found)) {
               instance.add("Read");
            }
            if (DogLicenseApplication.Permissions.canUpdate(asResident, found)) {
               instance.add("Update");
            }
            if (DogLicenseApplication.Permissions.canDelete(asResident, found)) {
               instance.add("Delete");
            }
            // submit
            if (DogLicenseApplication.Permissions.isSubmitAllowedFor(asResident, found)) {
                actions.get("submit").add("Call");
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
    
    public static Response.ResponseBuilder toExternalList(UriInfo uriInfo, Collection<DogLicenseApplication> models) {
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
    
    private static Map<String, Object> toExternalRepresentation(DogLicenseApplication toRender, URI instancesURI) {
        return DogLicenseApplicationJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, DogLicenseApplicationJAXRSSerialization.Feature.Values, DogLicenseApplicationJAXRSSerialization.Feature.Links);
    }
    
    private static Map<String, Object> toFullExternalRepresentation(DogLicenseApplication toRender, URI instancesURI, DogLicenseApplicationJAXRSSerialization.Feature... featureOptions) {
        return DogLicenseApplicationJAXRSSerialization.toExternalRepresentation(toRender, instancesURI, DogLicenseApplicationJAXRSSerialization.Feature.values());
    }
    
    private static void updateFromExternalRepresentation(DogLicenseApplication toUpdate, Map<String, Object> external) {
        DogLicenseApplicationJAXRSSerialization.updateFromExternalRepresentation(toUpdate, external);
    }
}
