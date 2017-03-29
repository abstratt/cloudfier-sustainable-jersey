package resource.applications;

import applications.*;

import java.util.*;
import java.util.stream.*;
import java.text.*;
import java.util.function.Function;
import java.io.IOException;

import org.apache.commons.lang3.time.DateUtils;

import java.net.URI;


public class DogLicenseApplicationJAXRSSerialization {
    private static final String[] DATE_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd", "yyyy/MM/dd" };

    public static enum Feature {
        Values,
        Links,
        ActionEnablement
    }

    public static Map<String, Object> toExternalRepresentation(DogLicenseApplication toRender, URI instancesURI, Feature... featureOptions) {
        EnumSet<Feature> features = featureOptions.length == 0 ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(Arrays.asList(featureOptions));
        Map<String, Object> result = new LinkedHashMap<>();
        boolean persisted = toRender.getId() != null;
        Function<String, String> stringEncoder = (it) -> it == null ? null : it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        if (features.contains(Feature.Values)) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("status", toRender.getStatus().name());
            values.put("petName", stringEncoder.apply(toRender.getPetName()));
            values.put("datePaid", toRender.getDatePaid() == null ? null : dateFormat.format(toRender.getDatePaid()));
            values.put("hair", toRender.getHair().name());
            values.put("coloring", stringEncoder.apply(toRender.getColoring()));
            values.put("sex", toRender.getSex().name());
            values.put("neutered", toRender.isNeutered());
            values.put("birthDate", dateFormat.format(toRender.getBirthDate()));
            values.put("rabiesCertificate", toRender.getRabiesCertificate());
            values.put("paymentReceived", toRender.getPaymentReceived());
            if (persisted) {
                values.put("internalNumber", toRender.getInternalNumber());
                values.put("number", stringEncoder.apply(toRender.getNumber()));
                values.put("referenceDate", toRender.getReferenceDate() == null ? null : dateFormat.format(toRender.getReferenceDate()));
                values.put("ageInYears", toRender.getAgeInYears());
                values.put("partialAgeInMonths", toRender.getPartialAgeInMonths());
                values.put("age", stringEncoder.apply(toRender.getAge()));
                values.put("lateFeeApplies", toRender.isLateFeeApplies());
                values.put("lateFee", toRender.getLateFee());
                values.put("baseFee", toRender.getBaseFee());
                values.put("requiredFee", toRender.getRequiredFee());
            } else {
                values.put("internalNumber", 0L);
                values.put("number", "");
                values.put("referenceDate", new Date());
                values.put("ageInYears", 0L);
                values.put("partialAgeInMonths", 0L);
                values.put("age", "");
                values.put("lateFeeApplies", false);
                values.put("lateFee", 0.0);
                values.put("baseFee", 0.0);
                values.put("requiredFee", 0.0);
            }
            result.put("values", values);
        }
        if (features.contains(Feature.Links)) {
            Map<String, Object> links = new LinkedHashMap<>();
            // dogOwner is navigable: true
            Map<String, Object> dogOwnerLink = null;
            if (toRender.getDogOwner() != null) {
                URI residentInstancesURI = instancesURI.resolve("../..").resolve("applications.DogLicenseApplication/instances"); 
                dogOwnerLink = ResidentJAXRSSerialization.toExternalRepresentation(toRender.getDogOwner(), residentInstancesURI);
            }    
            links.put("dogOwner", dogOwnerLink);
            
            // breed is navigable: true
            Map<String, Object> breedLink = null;
            if (toRender.getBreed() != null) {
                URI breedInstancesURI = instancesURI.resolve("../..").resolve("applications.DogLicenseApplication/instances"); 
                breedLink = BreedJAXRSSerialization.toExternalRepresentation(toRender.getBreed(), breedInstancesURI);
            }    
            links.put("breed", breedLink);
            result.put("links", links);
        }
        result.put("uri", instancesURI.resolve(persisted ? toRender.getId().toString() : "_template").toString());
        if (persisted) {
            result.put("objectId", toRender.getId().toString());
            result.put("shorthand", stringEncoder.apply(toRender.getNumber()));
        }
        if (features.contains(Feature.ActionEnablement)) {
            Map<String, String> disabledActions = new LinkedHashMap<>();
            if (!toRender.isSubmitActionEnabled())
                disabledActions.put("submit", "");
            if (!toRender.isReceivePaymentActionEnabled())
                disabledActions.put("receivePayment", "");
            if (!toRender.isRejectActionEnabled())
                disabledActions.put("reject", "");
            if (!toRender.isApproveActionEnabled())
                disabledActions.put("approve", "");
            if (!toRender.isCancelActionEnabled())
                disabledActions.put("cancel", "");
            result.put("disabledActions", disabledActions);
        }
        Map<String, Object> resultTypeRef = new LinkedHashMap<>();
        resultTypeRef.put("entityNamespace", "applications");
        resultTypeRef.put("kind", "Entity");
        resultTypeRef.put("typeName", "DogLicenseApplication");
        resultTypeRef.put("fullName", "applications.DogLicenseApplication");
        result.put("typeRef", resultTypeRef);   
        result.put("scopeName", "DogLicenseApplication");
        result.put("scopeNamespace", "applications");
        result.put("instanceCapabilityUri", instancesURI.toString() + "/capabilities");
        result.put("entityUri", instancesURI.resolve("../..").resolve("applications.DogLicenseApplication").toString());
        return result;                    
    }
    
    public static void updateFromExternalRepresentation(DogLicenseApplication toUpdate, Map<String, Object> external) {
        Map<String, Object> values = (Map<String, Object>) external.get("values");
        if (values.get("petName") != null)
            toUpdate.setPetName((String) values.get("petName"));
        else
            toUpdate.setPetName("");
        if (values.get("hair") != null)
            toUpdate.setHair(HairType.valueOf((String) values.get("hair")));
        else
            toUpdate.setHair(HairType.Short);
        if (values.get("coloring") != null)
            toUpdate.setColoring((String) values.get("coloring"));
        else
            toUpdate.setColoring("");
        if (values.get("sex") != null)
            toUpdate.setSex(Gender.valueOf((String) values.get("sex")));
        else
            toUpdate.setSex(Gender.Male);
        if (values.get("neutered") != null)
            toUpdate.setNeutered((boolean) values.get("neutered"));
        else
            toUpdate.setNeutered(false);
        try {
            if (values.get("birthDate") != null)
                toUpdate.setBirthDate(DateUtils.parseDate((String) values.get("birthDate"), DATE_FORMATS));
            else
                toUpdate.setBirthDate(new Date());
        } catch (ParseException e) {
            throw new ConversionException("Invalid format for date in 'birthDate': " + values.get("birthDate"));
        }
        
        toUpdate.setRabiesCertificate((String) values.get("rabiesCertificate"));
        toUpdate.setPaymentReceived(Double.parseDouble(values.get("paymentReceived").toString()));
        
        Map<String, Map<String, Object>> links = (Map<String, Map<String, Object>>) external.get("links");
        Map<String, Object> dogOwner = links.get("dogOwner");
        if (dogOwner != null) {
            Resident newValue = Optional.ofNullable(dogOwner.get("objectId")).map(it -> new ResidentService().find(Long.parseLong((String) it))).orElse(null);
            toUpdate.setDogOwner(newValue);
        } else {
            toUpdate.setDogOwner(null);
        }    
        Map<String, Object> breed = links.get("breed");
        if (breed != null) {
            Breed newValue = Optional.ofNullable(breed.get("objectId")).map(it -> new BreedService().find(Long.parseLong((String) it))).orElse(null);
            toUpdate.setBreed(newValue);
        } else {
            toUpdate.setBreed(null);
        }    
    }
}
