package resource.service_requests;

import service_requests.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;

import java.net.URI;


public class ServiceRequestJAXRSSerialization {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };

    public static enum Feature {
        Values,
        Links,
        ActionEnablement
    }

    public static Map<String, Object> toExternalRepresentation(ServiceRequest toRender, URI instancesURI, Feature... featureOptions) {
        EnumSet<Feature> features = featureOptions.length == 0 ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(Arrays.asList(featureOptions));
        Map<String, Object> result = new LinkedHashMap<>();
        boolean persisted = toRender.getId() != null;
        Function<String, String> stringEncoder = (it) -> it == null ? null : it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        if (features.contains(Feature.Values)) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("reportDate", dateFormat.format(toRender.getReportDate()));
            values.put("description", stringEncoder.apply(toRender.getDescription()));
            values.put("status", toRender.getStatus().name());
            values.put("picture", toRender.getPicture());
            values.put("location", toRender.getLocation());
            values.put("acceptanceDate", toRender.getAcceptanceDate() == null ? null : dateFormat.format(toRender.getAcceptanceDate()));
            values.put("completionDate", toRender.getCompletionDate() == null ? null : dateFormat.format(toRender.getCompletionDate()));
            values.put("staffComment", stringEncoder.apply(toRender.getStaffComment()));
            result.put("values", values);
        }
        if (features.contains(Feature.Links)) {
            Map<String, Object> links = new LinkedHashMap<>();
            // serviceRequestType is navigable: true
            Map<String, Object> serviceRequestTypeLink = null;
            if (toRender.getServiceRequestType() != null) {
                URI serviceRequestTypeInstancesURI = instancesURI.resolve("../..").resolve("service_requests.ServiceRequest/instances"); 
                serviceRequestTypeLink = ServiceRequestTypeJAXRSSerialization.toExternalRepresentation(toRender.getServiceRequestType(), serviceRequestTypeInstancesURI);
            }    
            links.put("serviceRequestType", serviceRequestTypeLink);
            
            // resident is navigable: true
            Map<String, Object> residentLink = null;
            if (toRender.getResident() != null) {
                URI residentInstancesURI = instancesURI.resolve("../..").resolve("service_requests.ServiceRequest/instances"); 
                residentLink = ResidentJAXRSSerialization.toExternalRepresentation(toRender.getResident(), residentInstancesURI);
            }    
            links.put("resident", residentLink);
            
            // staff is navigable: true
            Map<String, Object> staffLink = null;
            if (toRender.getStaff() != null) {
                URI cityOfficialInstancesURI = instancesURI.resolve("../..").resolve("service_requests.ServiceRequest/instances"); 
                staffLink = CityOfficialJAXRSSerialization.toExternalRepresentation(toRender.getStaff(), cityOfficialInstancesURI);
            }    
            links.put("staff", staffLink);
            result.put("links", links);
        }
        result.put("uri", instancesURI.resolve(persisted ? toRender.getId().toString() : "_template").toString());
        if (persisted) {
            result.put("objectId", toRender.getId().toString());
            result.put("shorthand", toRender.getServiceRequestType());
        }
        if (features.contains(Feature.ActionEnablement)) {
            Map<String, String> disabledActions = new LinkedHashMap<>();
            if (!toRender.isAcceptActionEnabled())
                disabledActions.put("accept", "");
            if (!toRender.isRejectActionEnabled())
                disabledActions.put("reject", "");
            if (!toRender.isCompleteActionEnabled())
                disabledActions.put("complete", "");
            result.put("disabledActions", disabledActions);
        }
        Map<String, Object> resultTypeRef = new LinkedHashMap<>();
        resultTypeRef.put("entityNamespace", "service_requests");
        resultTypeRef.put("kind", "Entity");
        resultTypeRef.put("typeName", "ServiceRequest");
        resultTypeRef.put("fullName", "service_requests.ServiceRequest");
        result.put("typeRef", resultTypeRef);   
        result.put("scopeName", "ServiceRequest");
        result.put("scopeNamespace", "service_requests");
        result.put("instanceCapabilityUri", instancesURI.toString() + "/capabilities");
        result.put("entityUri", instancesURI.resolve("../..").resolve("service_requests.ServiceRequest").toString());
        return result;                    
    }
    
    public static void updateFromExternalRepresentation(ServiceRequest toUpdate, Map<String, Object> external) {
        Map<String, Object> values = (Map<String, Object>) external.get("values");
        try {
            toUpdate.setReportDate(DateUtils.parseDate((String) values.get("reportDate"), DATE_FORMATS));
        } catch (ParseException e) {
            throw new ConversionException("Invalid format for date in 'reportDate': " + values.get("reportDate"));
        }
        
        if (values.get("description") != null)
            toUpdate.setDescription((String) values.get("description"));
        else
            toUpdate.setDescription("");
        toUpdate.setPicture((String) values.get("picture"));
        toUpdate.setLocation((String) values.get("location"));
        
        Map<String, Map<String, Object>> links = (Map<String, Map<String, Object>>) external.get("links");
        Map<String, Object> serviceRequestType = links.get("serviceRequestType");
        if (serviceRequestType != null) {
            ServiceRequestType newValue = Optional.ofNullable(serviceRequestType.get("objectId")).map(it -> new ServiceRequestTypeService().find(Long.parseLong((String) it))).orElse(null);
            toUpdate.setServiceRequestType(newValue);
        } else {
            toUpdate.setServiceRequestType(null);
        }    
        Map<String, Object> resident = links.get("resident");
        if (resident != null) {
            Resident newValue = Optional.ofNullable(resident.get("objectId")).map(it -> new ResidentService().find(Long.parseLong((String) it))).orElse(null);
            toUpdate.setResident(newValue);
        } else {
            toUpdate.setResident(null);
        }    
    }
}
