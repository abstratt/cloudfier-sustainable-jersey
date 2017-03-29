package util;

import java.util.List;
import java.util.ArrayList;

import userprofile.Profile;
import userprofile.ProfileService;
import service_requests.Resident;
import service_requests.ResidentService;
import service_requests.CityOfficial;
import service_requests.CityOfficialService;
import service_requests.SystemAdministrator;
import service_requests.SystemAdministratorService;

public class SecurityHelper {
    public static ThreadLocal<String> currentUsername = new ThreadLocal<>();
    
    public static Profile getCurrentProfile() {
        return new ProfileService().findByUsername(getCurrentUsername());
    }

    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    public static void setCurrentUsername(String username) {
    	currentUsername.set(username);
    }
    
    public static List<String> getRoles(Profile user) {
		List<String> roles = new ArrayList<>();
		if (asResident(user) != null) {
			roles.add(Resident.ROLE_ID);
		}
		if (asCityOfficial(user) != null) {
			roles.add(CityOfficial.ROLE_ID);
		}
		if (asSystemAdministrator(user) != null) {
			roles.add(SystemAdministrator.ROLE_ID);
		}
		return roles;
    }
    
    public static Resident asResident(Profile userProfile) {
    	if (userProfile == null) {
    		return null;
        }
    	Resident asRole = new ResidentService().findRoleAsResidentByUserProfile(userProfile);
        return asRole;
    }
    
    public static Resident getCurrentResident() {
        return asResident(getCurrentProfile());
    }
    public static CityOfficial asCityOfficial(Profile userProfile) {
    	if (userProfile == null) {
    		return null;
        }
    	CityOfficial asRole = new CityOfficialService().findRoleAsCityOfficialByUserProfile(userProfile);
        return asRole;
    }
    
    public static CityOfficial getCurrentCityOfficial() {
        return asCityOfficial(getCurrentProfile());
    }
    public static SystemAdministrator asSystemAdministrator(Profile userProfile) {
    	if (userProfile == null) {
    		return null;
        }
    	SystemAdministrator asRole = new SystemAdministratorService().findRoleAsSystemAdministratorByUserProfile(userProfile);
        return asRole;
    }
    
    public static SystemAdministrator getCurrentSystemAdministrator() {
        return asSystemAdministrator(getCurrentProfile());
    }
}
