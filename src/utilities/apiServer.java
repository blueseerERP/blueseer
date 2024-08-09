/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package utilities;

/**
 *
 * @author terryva
 */


import static bsmf.MainFrame.tags;
import com.blueseer.srv.AS2Serv;
import com.blueseer.srv.CustomerServ;
import com.blueseer.srv.ItemServ;
import com.blueseer.srv.SalesOrdServ;
import com.blueseer.srv.ShipperServ;
import com.blueseer.srv.WorkOrdServ;
import com.blueseer.srv.dataServ;
import static com.blueseer.utl.BlueSeerUtils.isParsableToInt;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Handler;


import org.eclipse.jetty.servlet.ServletContextHandler; 
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

 
     

public class apiServer {
    
    
    
    private static final String[] HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };
    
    public static void main(String[] args) throws Exception {
         
        
        
        bsmf.MainFrame.setConfig();
        tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
        
        
        
	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/bsapi"); 
        String webdir = "src/web/WEB-INF/";
        context.setResourceBase(webdir);
        
        int i = 0;
        int serverport = 8088;
        for (String s : args) {
            System.out.println(String.valueOf(i+1) + " argument passed: " + s);
             if (s.equalsIgnoreCase("-debug"))
                 context.setAttribute("debug", "true");
             if (s.equalsIgnoreCase("-port")) {
                 if (args[i+1] != null && ! args[i+1].isBlank() && isParsableToInt(args[i+1])) {
                     serverport = Integer.valueOf(args[i+1]);
                 }
             }
             i++;
        }
        
        Server server = new Server(serverport);
        
        context.addServlet(AS2Serv.class, "/as2/*");
        context.addServlet(WorkOrdServ.class, "/WorkOrder/*");
        context.addServlet(WorkOrdServ.class, "/WorkOrderList/*");
        context.addServlet(SalesOrdServ.class, "/SalesOrder/*");
        context.addServlet(SalesOrdServ.class, "/SalesOrderList/*");
        context.addServlet(ShipperServ.class, "/Shipper/*");
        context.addServlet(ShipperServ.class, "/ShipperList/*");
        context.addServlet(ItemServ.class, "/Item/*");
        context.addServlet(ItemServ.class, "/ItemList/*");
        context.addServlet(CustomerServ.class, "/Customer/*");
        context.addServlet(CustomerServ.class, "/CustomerList/*");
        context.addServlet(TestServlet.class, "/test/*");
        context.addServlet(dataServ.class, "/dataServ/*");
        //server.setHandler(context);
        
        
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/bsweb");
        webapp.setResourceBase("web/");
        webapp.setWelcomeFiles(new String[]{"index.html"});
       
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, webapp });
        server.setHandler(contexts);
       
        // failed JSP support
        /*
       Configuration.ClassList classlist = Configuration.ClassList
            .setServerDefault(server);
        classlist.addBefore(
            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
            "org.eclipse.jetty.annotations.AnnotationConfiguration");
	  */
        
        
	   server.start();
	   server.join(); 
	    
	       
	        
	 }      
     
     
    public static class TestServlet extends HttpServlet
{
    private String greeting="BlueSeer API server says hello!";
    public TestServlet(){}
    public TestServlet(String greeting)
    {
        this.greeting=greeting;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>"+greeting+"</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId() + "<br>");
        response.getWriter().println("RemoteAddr=" + request.getRemoteAddr() + "<br>");
        response.getWriter().println("RemoteHost=" + request.getRemoteHost() + "<br>");
        response.getWriter().println("RequestURI=" + request.getRequestURI() + "<br>");
        for (String header : HEADERS) {
        String ip = request.getHeader(header);
        if (ip != null && ip.length() != 0 && ! "unknown".equalsIgnoreCase(ip)) {
            response.getWriter().println("info: " + header + "=" + ip + "<br>");
        }
    }
        
    }
}
    
    
     
}
