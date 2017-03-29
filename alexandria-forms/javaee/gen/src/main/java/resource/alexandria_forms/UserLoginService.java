package resource.alexandria_forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.ws.rs.ext.Provider;

import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.util.security.Password;

import alexandria_forms.*;
import userprofile.*;
import util.PersistenceHelper;
import util.SecurityHelper;

@Provider
public class UserLoginService extends MappedLoginService {

	@Override
	protected UserIdentity loadUser(String username) {
		Context jettyContext = ContextHandler.getCurrentContext();
		ServletContext servletContext = jettyContext.getContext(jettyContext.getServletContextName());
		Profile user = new ProfileService().findByUsername(username);
		if (user == null)
			return null;
		List<String> roles = SecurityHelper.getRoles(user);
		return new DefaultUserIdentity(new Subject(), new KnownUser(username, new Password(user.getPassword())), roles.toArray(new String[0]));
	}

	@Override
	protected void loadUsers() throws IOException {
	}

}		
