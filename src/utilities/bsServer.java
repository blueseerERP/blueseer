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
import com.blueseer.srv.authServ;
import com.blueseer.srv.dataServ;
import com.blueseer.srv.dataServFIN;
import com.blueseer.srv.webServ;
import static com.blueseer.utl.BlueSeerUtils.isParsableToInt;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.AsyncRequestLogWriter;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.SecureRequestCustomizer;


import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;




public class bsServer {

    public static void main(String[] args) throws Exception {

        boolean isDebug = false;
        String configfile = "";
        if (args != null && args.length > 0) {
            int i = 0;
            for (String s : args) {
                if (s.equals("-config")) {
                    configfile = args[i+1];
                }
                i++;
            }
        }

        bsmf.MainFrame.setConfig(configfile);
        tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());




        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        context.setContextPath("/bsapi");
        String webdir = "src/web/WEB-INF/";
        context.setResourceBase(webdir);

        int i = 0;
        int serverport = ServerConfig.DEFAULT_HTTP_PORT;
        for (String s : args) {
            System.out.println(String.valueOf(i+1) + " argument passed: " + s);
            if (s.equalsIgnoreCase("-debug")) {
                context.setAttribute("debug", "true");
                isDebug = true;
            }
            if (s.equalsIgnoreCase("-port")) {
                if (args[i+1] != null && ! args[i+1].isBlank() && isParsableToInt(args[i+1])) {
                    serverport = Integer.valueOf(args[i+1]);
                }
            }
            i++;
        }

        Properties prop = new Properties();
        try( FileInputStream fis = new FileInputStream("conf/web.properties")) {
            prop.load(fis);
            if (isDebug) {
                System.out.println("debug:  ...loading web.properties");
            }
        }
        catch(Exception e) {
            System.out.println("Unable to find the specified web.properties file");
            e.printStackTrace();
            return;
        }



        Server server = createServerBS(serverport, ServerConfig.DEFAULT_HTTPS_PORT, true, prop);

        // Extra options
        server.setDumpAfterStart(true);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        // Register API servlets
        registerApiServlets(context);

        // Setup web application
        WebAppContext webapp = createWebAppContext();

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, webapp });
        server.setHandler(contexts);

        server.start();
        server.join();



    }

    /**
     * Registers all API servlets with the given context handler.
     * @param context the servlet context handler to register servlets with
     */
    private static void registerApiServlets(ServletContextHandler context) {
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
        context.addServlet(dataServFIN.class, "/dataServFIN/*");
        context.addServlet(authServ.class, "/authServ/*");
        context.addServlet(authServ.class, "/dataServOV/*");
    }

    /**
     * Creates and configures the web application context.
     * @return configured WebAppContext
     */
    private static WebAppContext createWebAppContext() {
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/bsweb");
        webapp.setResourceBase("web/");
        webapp.addServlet(webServ.class, "/webServ/*");
        webapp.setWelcomeFiles(new String[]{"login.html"});
        return webapp;
    }

    public static Server createServerBS(int port, int securePort, boolean addDebugListener, Properties prop) throws Exception {


        // === jetty.xml ===
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(ServerConfig.MAX_THREADS);

        // Server
        Server server = new Server(threadPool);

        // Scheduler
        server.addBean(new ScheduledExecutorScheduler(null, false));

        // HTTP Configuration
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(securePort);
        httpConfig.setOutputBufferSize(ServerConfig.OUTPUT_BUFFER_SIZE);
        httpConfig.setRequestHeaderSize(ServerConfig.REQUEST_HEADER_SIZE);
        httpConfig.setResponseHeaderSize(ServerConfig.RESPONSE_HEADER_SIZE);
        httpConfig.setSendServerVersion(true);
        httpConfig.setSendDateHeader(false);
        // httpConfig.addCustomizer(new ForwardedRequestCustomizer());

        // Handler Structure
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
        server.setHandler(handlers);



        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(httpConfig));
        http.setPort(port);
        http.setIdleTimeout(ServerConfig.IDLE_TIMEOUT_MS);
        server.addConnector(http);

        // === jetty-https.xml ===
        // SSL Context Factory
        Path keystorePath = Paths.get(prop.getProperty("keystore")).toAbsolutePath();
        if (!Files.exists(keystorePath))
            throw new FileNotFoundException(keystorePath.toString());
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(keystorePath.toString());
        //  sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        //  sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        sslContextFactory.setKeyStorePassword(prop.getProperty("storepass"));
        sslContextFactory.setKeyManagerPassword(prop.getProperty("keypass"));
        sslContextFactory.setTrustStorePath(keystorePath.toString());
        //  sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setTrustStorePassword(prop.getProperty("storepass"));

        // SSL HTTP Configuration
        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfig));
        sslConnector.setPort(securePort);
        server.addConnector(sslConnector);




        // === jetty-requestlog.xml ===
        AsyncRequestLogWriter logWriter = new AsyncRequestLogWriter("logs/yyyy_mm_dd.request.log");
        CustomRequestLog requestLog = new CustomRequestLog(logWriter, CustomRequestLog.EXTENDED_NCSA_FORMAT + " \"%C\"");
        logWriter.setFilenameDateFormat("yyyy_MM_dd");
        logWriter.setRetainDays(90);
        logWriter.setTimeZone("GMT");
        server.setRequestLog(requestLog);

        // === jetty-lowresources.xml ===
        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);



        return server;
    }

}