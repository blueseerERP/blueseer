/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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


import com.blueseer.srv.SalesOrdServ;
import com.blueseer.srv.WorkOrdServ;
import java.io.IOException;


import org.eclipse.jetty.servlet.ServletContextHandler; 
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.Server;


public class apiServer {
     public static void main(String[] args) throws Exception {
         
        bsmf.MainFrame.setConfig();
         
        Server server = new Server(8080);
	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/bsapi"); 
        String webdir = "src/web/WEB-INF/";
        context.setResourceBase(webdir);
        server.setHandler(context);
        context.addServlet(WorkOrdServ.class, "/WorkOrder");
        context.addServlet(SalesOrdServ.class, "/SalesOrder");
        // add hello servlet
        //context.addServlet(HelloServlet.class, "/hello/*");
	        
	   server.start();
	   server.join(); 
	    
	       
	        
	 }        
}
