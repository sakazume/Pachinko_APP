package pachinko;

import org.glassfish.grizzly.http.server.*;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String APP_NAME = "myapp";
    public static final String ASSET_URI = "/" + APP_NAME + "/asset";
    public static final String BASE_URI = "http://localhost:8080/" + APP_NAME + "/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        //JAX-RSの登録
        final ResourceConfig rc = new ResourceConfig().packages("pachinko");
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);


        //HTTPサーバーの登録
        String path = Main.class.getClassLoader().getResource(".").toExternalForm();
        String staticFilePath = path.replace("file:","");
        StaticHttpHandler staticHttpHandler = new StaticHttpHandler(staticFilePath);
        httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler, "/asset");


        return httpServer;
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //css url http://localhost:8080/asset/css/common.css

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdown();
    }
}

