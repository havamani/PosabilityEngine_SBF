package com.fss;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class TomcatConfiguration {
	@Value("${tomcat.ajp.port}")
    int ajpPort;

    @Value("${tomcat.ajp.remoteauthentication}")
    String remoteAuthentication;

    @Value("${tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;

	/*
	 * @Bean public TomcatServletWebServerFactory servletContainer() {
	 * 
	 * TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	 * if (tomcatAjpEnabled) { Connector ajpConnector = new Connector("AJP/1.3");
	 * ajpConnector.setPort(ajpPort); ajpConnector.setSecure(false);
	 * ajpConnector.setAllowTrace(false); ajpConnector.setScheme("http");
	 * ((AbstractAjpProtocol)
	 * ajpConnector.getProtocolHandler()).setSecretRequired(false);
	 * tomcat.addAdditionalTomcatConnectors(ajpConnector); }
	 * 
	 * return tomcat; }
	 */
    
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
      return server -> {
        if (server instanceof TomcatServletWebServerFactory) {
            ((TomcatServletWebServerFactory) server).addAdditionalTomcatConnectors(redirectConnector());
        }
      };
    }

    private Connector redirectConnector() {
       Connector connector = new Connector("AJP/1.3");
       connector.setScheme("http");
       connector.setAttribute("address", "10.44.51.11");
       connector.setPort(ajpPort);	
       //connector.setSecure(false);
       connector.setAllowTrace(false);
       final AbstractAjpProtocol protocol = (AbstractAjpProtocol) connector.getProtocolHandler();
       connector.setSecure(false);
       protocol.setSecretRequired(false);
       return connector;
    }
}
