package br.com.casadocodigo.javacred.corretora.test;

import br.com.casadocodigo.javacred.testsupport.WildFlyContainer;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.utility.MountableFile;

public abstract class CorretoraTestBase {

    /**
     * O WAR é o output do próprio módulo. Failsafe roda na fase
     * {@code integration-test}, depois de {@code package}, então o WAR
     * está pronto em {@code target/}.
     */
    @RegisterExtension
    static final WildFlyContainer WILDFLY = new WildFlyContainer()
            .withDeployment("javacred-corretora.war",
                    MountableFile.forHostPath("target/javacred-corretora.war"));

    public static String corretoraBaseUri() {
        return WILDFLY.getHttpBaseUri() + "/javacred-corretora";
    }
}
