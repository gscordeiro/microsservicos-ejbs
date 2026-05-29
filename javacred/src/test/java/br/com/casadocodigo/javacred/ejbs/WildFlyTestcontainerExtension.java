package br.com.casadocodigo.javacred.ejbs;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Sobe um WildFly em Docker via Testcontainers para o Arquillian Remote.
 * <p>
 * O usuário admin/admin é criado porque o Arquillian faz o deploy do WAR
 * pela API de management (porta 9990), que pede autenticação.
 */
public class WildFlyTestcontainerExtension implements BeforeAllCallback {

    static final GenericContainer<?> WILDFLY =
            new GenericContainer<>("quay.io/wildfly/wildfly:33.0.2.Final-jdk21")
                    .withExposedPorts(8080, 9990)
                    .withCommand("sh", "-c",
                            "$JBOSS_HOME/bin/add-user.sh admin admin --silent && " +
                            "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0")
                    .waitingFor(Wait.forLogMessage(".*WildFly.*started in.*\\n", 1));

    @Override
    public void beforeAll(ExtensionContext context) {
        WILDFLY.start();
        System.setProperty("wildfly.host", WILDFLY.getHost());
        System.setProperty("wildfly.management.port", WILDFLY.getMappedPort(9990).toString());
        System.setProperty("wildfly.http.port", WILDFLY.getMappedPort(8080).toString());
    }
}
