package resource.userprofile;

import userprofile.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;

import java.net.URI;

import resource.service_requests.*;

public class ProfileJAXRSSerialization {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };

    public static enum Feature {
        Values,
        Links,
        ActionEnablement
    }

    public static Map<String, Object> toExternalRepresentation(Profile toRender, URI instancesURI, Feature... featureOptions) {
        EnumSet<Feature> features = featureOptions.length == 0 ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(Arrays.asList(featureOptions));
        Map<String, Object> result = new LinkedHashMap<>();
        boolean persisted = toRender.getId() != null;
        Function<String, String> stringEncoder = (it) -> it == null ? null : it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        if (features.contains(Feature.Values)) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("username", stringEncoder.apply(toRender.getUsername()));
            values.put("password", stringEncoder.apply(toRender.getPassword()));
            result.put("values", values);
        }
        if (features.contains(Feature.Links)) {
            Map<String, Object> links = new LinkedHashMap<>();
            // roleAsResident is navigable: true
            Map<String, Object> roleAsResidentLink = null;
            if (toRender.getRoleAsResident() != null) {
                URI residentInstancesURI = instancesURI.resolve("../..").resolve("userprofile.Profile/instances"); 
                roleAsResidentLink = ResidentJAXRSSerialization.toExternalRepresentation(toRender.getRoleAsResident(), residentInstancesURI);
            }    
            links.put("roleAsResident", roleAsResidentLink);
            
            // roleAsCityOfficial is navigable: true
            Map<String, Object> roleAsCityOfficialLink = null;
            if (toRender.getRoleAsCityOfficial() != null) {
                URI cityOfficialInstancesURI = instancesURI.resolve("../..").resolve("userprofile.Profile/instances"); 
                roleAsCityOfficialLink = CityOfficialJAXRSSerialization.toExternalRepresentation(toRender.getRoleAsCityOfficial(), cityOfficialInstancesURI);
            }    
            links.put("roleAsCityOfficial", roleAsCityOfficialLink);
            
            // roleAsSystemAdministrator is navigable: true
            Map<String, Object> roleAsSystemAdministratorLink = null;
            if (toRender.getRoleAsSystemAdministrator() != null) {
                URI systemAdministratorInstancesURI = instancesURI.resolve("../..").resolve("userprofile.Profile/instances"); 
                roleAsSystemAdministratorLink = SystemAdministratorJAXRSSerialization.toExternalRepresentation(toRender.getRoleAsSystemAdministrator(), systemAdministratorInstancesURI);
            }    
            links.put("roleAsSystemAdministrator", roleAsSystemAdministratorLink);
            result.put("links", links);
        }
        result.put("uri", instancesURI.resolve(persisted ? toRender.getId().toString() : "_template").toString());
        if (persisted) {
            result.put("objectId", toRender.getId().toString());
            result.put("shorthand", stringEncoder.apply(toRender.getUsername()));
        }
        Map<String, Object> resultTypeRef = new LinkedHashMap<>();
        resultTypeRef.put("entityNamespace", "userprofile");
        resultTypeRef.put("kind", "Entity");
        resultTypeRef.put("typeName", "Profile");
        resultTypeRef.put("fullName", "userprofile.Profile");
        result.put("typeRef", resultTypeRef);   
        result.put("scopeName", "Profile");
        result.put("scopeNamespace", "userprofile");
        result.put("instanceCapabilityUri", instancesURI.toString() + "/capabilities");
        result.put("entityUri", instancesURI.resolve("../..").resolve("userprofile.Profile").toString());
        return result;                    
    }
    
    public static void updateFromExternalRepresentation(Profile toUpdate, Map<String, Object> external) {
        Map<String, Object> values = (Map<String, Object>) external.get("values");
        toUpdate.setUsername((String) values.get("username"));
        if (values.get("password") != null)
            toUpdate.setPassword((String) values.get("password"));
        else
            toUpdate.setPassword("");
        
        Map<String, Map<String, Object>> links = (Map<String, Map<String, Object>>) external.get("links");
    }
}
