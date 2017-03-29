package resource.applications;

import applications.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;

import java.net.URI;

import resource.alexandria_forms.*;
import resource.userprofile.*;

public class CityOfficialJAXRSSerialization {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };

    public static enum Feature {
        Values,
        Links,
        ActionEnablement
    }

    public static Map<String, Object> toExternalRepresentation(CityOfficial toRender, URI instancesURI, Feature... featureOptions) {
        EnumSet<Feature> features = featureOptions.length == 0 ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(Arrays.asList(featureOptions));
        Map<String, Object> result = new LinkedHashMap<>();
        boolean persisted = toRender.getId() != null;
        Function<String, String> stringEncoder = (it) -> it == null ? null : it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        if (features.contains(Feature.Values)) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("firstName", stringEncoder.apply(toRender.getFirstName()));
            values.put("lastName", stringEncoder.apply(toRender.getLastName()));
            if (persisted) {
                values.put("fullName", stringEncoder.apply(toRender.getFullName()));
            } else {
                values.put("fullName", "");
            }
            result.put("values", values);
        }
        if (features.contains(Feature.Links)) {
            Map<String, Object> links = new LinkedHashMap<>();
            // userProfile is navigable: true
            Map<String, Object> userProfileLink = null;
            if (toRender.getUserProfile() != null) {
                URI profileInstancesURI = instancesURI.resolve("../..").resolve("applications.CityOfficial/instances"); 
                userProfileLink = ProfileJAXRSSerialization.toExternalRepresentation(toRender.getUserProfile(), profileInstancesURI);
            }    
            links.put("userProfile", userProfileLink);
            result.put("links", links);
        }
        result.put("uri", instancesURI.resolve(persisted ? toRender.getId().toString() : "_template").toString());
        if (persisted) {
            result.put("objectId", toRender.getId().toString());
            result.put("shorthand", stringEncoder.apply(toRender.getFullName()));
        }
        Map<String, Object> resultTypeRef = new LinkedHashMap<>();
        resultTypeRef.put("entityNamespace", "applications");
        resultTypeRef.put("kind", "Entity");
        resultTypeRef.put("typeName", "CityOfficial");
        resultTypeRef.put("fullName", "applications.CityOfficial");
        result.put("typeRef", resultTypeRef);   
        result.put("scopeName", "CityOfficial");
        result.put("scopeNamespace", "applications");
        result.put("instanceCapabilityUri", instancesURI.toString() + "/capabilities");
        result.put("entityUri", instancesURI.resolve("../..").resolve("applications.CityOfficial").toString());
        return result;                    
    }
    
    public static void updateFromExternalRepresentation(CityOfficial toUpdate, Map<String, Object> external) {
        Map<String, Object> values = (Map<String, Object>) external.get("values");
        if (values.get("firstName") != null)
            toUpdate.setFirstName((String) values.get("firstName"));
        else
            toUpdate.setFirstName("");
        if (values.get("lastName") != null)
            toUpdate.setLastName((String) values.get("lastName"));
        else
            toUpdate.setLastName("");
        
        Map<String, Map<String, Object>> links = (Map<String, Map<String, Object>>) external.get("links");
    }
}
