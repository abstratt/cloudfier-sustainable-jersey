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

public class BreedJAXRSSerialization {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };

    public static enum Feature {
        Values,
        Links,
        ActionEnablement
    }

    public static Map<String, Object> toExternalRepresentation(Breed toRender, URI instancesURI, Feature... featureOptions) {
        EnumSet<Feature> features = featureOptions.length == 0 ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(Arrays.asList(featureOptions));
        Map<String, Object> result = new LinkedHashMap<>();
        boolean persisted = toRender.getId() != null;
        Function<String, String> stringEncoder = (it) -> it == null ? null : it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        if (features.contains(Feature.Values)) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("breed", stringEncoder.apply(toRender.getBreed()));
            result.put("values", values);
        }
        if (features.contains(Feature.Links)) {
            Map<String, Object> links = new LinkedHashMap<>();
            result.put("links", links);
        }
        result.put("uri", instancesURI.resolve(persisted ? toRender.getId().toString() : "_template").toString());
        if (persisted) {
            result.put("objectId", toRender.getId().toString());
            result.put("shorthand", stringEncoder.apply(toRender.getBreed()));
        }
        Map<String, Object> resultTypeRef = new LinkedHashMap<>();
        resultTypeRef.put("entityNamespace", "applications");
        resultTypeRef.put("kind", "Entity");
        resultTypeRef.put("typeName", "Breed");
        resultTypeRef.put("fullName", "applications.Breed");
        result.put("typeRef", resultTypeRef);   
        result.put("scopeName", "Breed");
        result.put("scopeNamespace", "applications");
        result.put("instanceCapabilityUri", instancesURI.toString() + "/capabilities");
        result.put("entityUri", instancesURI.resolve("../..").resolve("applications.Breed").toString());
        return result;                    
    }
    
    public static void updateFromExternalRepresentation(Breed toUpdate, Map<String, Object> external) {
        Map<String, Object> values = (Map<String, Object>) external.get("values");
        if (values.get("breed") != null)
            toUpdate.setBreed((String) values.get("breed"));
        else
            toUpdate.setBreed("");
        
        Map<String, Map<String, Object>> links = (Map<String, Map<String, Object>>) external.get("links");
    }
}
