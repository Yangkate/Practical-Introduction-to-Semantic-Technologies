/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.jyu.it.Main;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 *
 * @author Administrator
 */
public class Run {
    
public static void main(String[] args) throws Exception {
		
	ServletHandler handler = new ServletHandler();
		//add all servlets you want to use to the handler, the second argument is the path
        
        handler.addServletWithMapping(jena.class, "/jena");
        // make the SearchTeacher Servlet accessible from http://localhost:8080/search
       
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/resources");
        resourceHandler.setDirectoriesListed(true);
        HandlerList l = new HandlerList();
        l.addHandler(resourceHandler);

        l.addHandler(handler);
        //Create a new Server, add the handler to it and start
        Server server = new Server(8030);
        server.setHandler(l);

        server.start();
        //this dumps a lot of debug output to the console. 
        server.dumpStdErr();
        server.join();
    }
}

