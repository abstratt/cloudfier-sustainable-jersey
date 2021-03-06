package resource.alexandria_forms;

import java.io.IOException;
import java.net.URL;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;

import resource.alexandria_forms.UserLoginService;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import util.PersistenceHelper;

public class RESTServer extends AbstractHandler {
	
	private static boolean PRESERVE_SCHEMA = false;
	private static boolean PRESERVE_DATA = false;

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
    }

    public static void main(String[] args) throws Exception {
		List<String> commands = Arrays.asList(args);
		boolean createSchema = commands.contains("createSchema");
		boolean initData = commands.contains("initData");
		boolean run = commands.contains("run");
		if (initData) {
			System.out.println("*** initData");
			if (PRESERVE_DATA) {
				throw new IllegalArgumentException("initData not allowed");
			}
			PersistenceHelper.createSchemaAndInitData().close();
		} else if (createSchema) {
			System.out.println("*** createSchema");
			if (PRESERVE_SCHEMA) {
				throw new IllegalArgumentException("createSchema not allowed");
			}
			PersistenceHelper.createSchema().close();
		}
		if (run || (!createSchema && !initData)) {
			System.out.println("*** run");
			new RESTServer().run();
		} else {
			System.exit(0);
		}
    }

    public void run() throws Exception {
        int port = Integer.parseInt(System.getProperty("http.port", "8888"));
        String host = System.getProperty("http.address", "0.0.0.0");
        Server server = new Server(new InetSocketAddress(host, port));
        URL webXml = RESTServer.class.getResource("/WEB-INF/web.xml");
        WebAppContext context = new WebAppContext();
        context.setResourceBase(webXml.toURI().resolve("..").toString());
        context.setDescriptor(webXml.toString());
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        
		context.addEventListener(new ResteasyBootstrap());
		context.addEventListener(new ContextListener());
		UserLoginService loginService = new UserLoginService();
		context.getSecurityHandler().setLoginService(loginService);
        
        server.setHandler(context);
        server.start();
        server.join();
    }
}