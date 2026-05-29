package br.com.casadocodigo.javacred.restclient;


import br.com.casadocodigo.javacred.testsupport.WildFlyContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.utility.MountableFile;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

public abstract class JavacredTestBase {

    @RegisterExtension
    static final WildFlyContainer WILDFLY = new WildFlyContainer()
            .withDeployment("javacred.war",
                    MountableFile.forHostPath("target/test-deployments/javacred.war"));

    static WebTarget javacred;

    public static String javacredBaseUri() {
        return WILDFLY.getHttpBaseUri() + "/javacred/rest";
    }

    @BeforeAll
    public static void criaCliente() {
        Client client = ClientBuilder.newBuilder().build();
        javacred = client.target(javacredBaseUri());
    }

}
