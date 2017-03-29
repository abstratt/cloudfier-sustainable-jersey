package util;

import java.util.List;
import java.util.ArrayList;

import userprofile.Profile;
import userprofile.ProfileService;
import applications.CityOfficial;
import applications.CityOfficialService;
import applications.SystemAdministrator;
import applications.SystemAdministratorService;
import applications.Resident;
import applications.ResidentService;

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
		if (asCityOfficial(user) != null) {
			roles.add(CityOfficial.ROLE_ID);
		}
		if (asSystemAdministrator(user) != null) {
			roles.add(SystemAdministrator.ROLE_ID);
		}
		if (asResident(user) != null) {
			roles.add(Resident.ROLE_ID);
		}
		return roles;
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
}
