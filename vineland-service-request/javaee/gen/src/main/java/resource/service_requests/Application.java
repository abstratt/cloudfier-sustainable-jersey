package resource.service_requests;

import java.util.HashSet;
import java.util.Set;

import resource.userprofile.ProfileResource;
import resource.util.LoginLogoutResource;

import resource.userprofile.*;
import resource.service_requests.*;

public class Application extends javax.ws.rs.core.Application {
    private Set<Object> services = new HashSet<>();

    public Application() {
        services.add(new ProfileResource());
        services.add(new ServiceRequestResource());
        services.add(new PersonResource());
        services.add(new ResidentResource());
        services.add(new CityOfficialResource());
        services.add(new SystemAdministratorResource());
        services.add(new ServiceRequestTypeResource());
        services.add(new LoginLogoutResource());
        services.add(new resource.util.StandaloneRequestResponseFilter());
        services.add(new IndexResource());
        services.add(new EntityResource());
        services.add(new ConstraintViolationExceptionMapper());
        services.add(new RestEasyFailureMapper());
        services.add(new WebApplicationExceptionMapper());
        services.add(new ThrowableMapper());
    }

    @Override
    public Set<Object> getSingletons() {
        return services;
    }
}
