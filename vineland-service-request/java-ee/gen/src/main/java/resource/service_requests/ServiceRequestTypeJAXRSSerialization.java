package resource.service_requests;

import service_requests.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;

import java.net.URI;


public class ServiceRequestTypeJAXRSSerialization {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };

    public static enum Feature {
        Values,
        Links,
        ActionEnablement
    }

    public static Map<String, Object> toExternalRepresentation(ServiceRequestType toRender, URI instancesURI, Feature... featureOptions) {
        EnumSet<Feature> features = featureOptions.length == 0 ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(Arrays.asList(featureOptions));
        Map<String, Object> result = new LinkedHashMap<>();
        boolean persisted = toRender.getId() != null;
        Function<String, String> stringEncoder = (it) -> it == null ? null : it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        if (features.contains(Feature.Values)) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("name", stringEncoder.apply(toRender.getName()));
            result.put("values", values);
        }
        if (features.contains(Feature.Links)) {
            Map<String, Object> links = new LinkedHashMap<>();
            result.put("links", links);
        }
        result.put("uri", instancesURI.resolve(persisted ? toRender.getId().toString() : "_template").toString());
        if (persisted) {
            result.put("objectId", toRender.getId().toString());
            result.put("shorthand", stringEncoder.apply(toRender.getName()));
        }
        Map<String, Object> resultTypeRef = new LinkedHashMap<>();
        resultTypeRef.put("entityNamespace", "service_requests");
        resultTypeRef.put("kind", "Entity");
        resultTypeRef.put("typeName", "ServiceRequestType");
        resultTypeRef.put("fullName", "service_requests.ServiceRequestType");
        result.put("typeRef", resultTypeRef);   
        result.put("scopeName", "ServiceRequestType");
        result.put("scopeNamespace", "service_requests");
        result.put("instanceCapabilityUri", instancesURI.toString() + "/capabilities");
        result.put("entityUri", instancesURI.resolve("../..").resolve("service_requests.ServiceRequestType").toString());
        return result;                    
    }
    
    public static void updateFromExternalRepresentation(ServiceRequestType toUpdate, Map<String, Object> external) {
        Map<String, Object> values = (Map<String, Object>) external.get("values");
        if (values.get("name") != null)
            toUpdate.setName((String) values.get("name"));
        else
            toUpdate.setName("");
        
        Map<String, Map<String, Object>> links = (Map<String, Map<String, Object>>) external.get("links");
    }
}
