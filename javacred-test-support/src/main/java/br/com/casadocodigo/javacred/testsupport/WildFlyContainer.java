package br.com.casadocodigo.javacred.testsupport;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WildFly em Docker via Testcontainers, pronto para testes de integração.
 *
 * <p>Dois modos de uso:
 * <ul>
 *   <li><b>Auto-deploy:</b> chame {@link #withDeployment(String, MountableFile)} para
 *       copiar WAR(s) em {@code standalone/deployments/} antes do startup; o WildFly
 *       fará o deploy durante o boot.</li>
 *   <li><b>Deploy via management API (Arquillian remote):</b> chame
 *       {@link #withAdminUser(String, String)} para criar o usuário de management e
 *       deixe o Arquillian fazer o deploy em runtime.</li>
 * </ul>
 *
 * <p>Quando registrado como JUnit 5 Extension (via {@code @RegisterExtension static}),
 * também exporta {@code wildfly.host}, {@code wildfly.http.port} e
 * {@code wildfly.management.port} como system properties — convenientes para o
 * {@code arquillian.xml} que já usamos.
 */
public class WildFlyContainer extends GenericContainer<WildFlyContainer>
        implements BeforeAllCallback, AfterAllCallback {

    private static final String DEFAULT_IMAGE = "quay.io/wildfly/wildfly:33.0.2.Final-jdk21";
    private static final int HTTP_PORT = 8080;
    private static final int MANAGEMENT_PORT = 9990;
    private static final String DEPLOYMENTS_DIR = "/opt/server/standalone/deployments/";

    private final Map<String, MountableFile> deployments = new LinkedHashMap<>();
    private String adminUser;
    private String adminPassword;
    private String systemPropertyPrefix = "wildfly";

    public WildFlyContainer() {
        this(DEFAULT_IMAGE);
    }

    public WildFlyContainer(String image) {
        super(image);
        withExposedPorts(HTTP_PORT, MANAGEMENT_PORT);
        waitingFor(Wait.forLogMessage(".*WildFly.*started in.*\\n", 1));
    }

    /** Copia o WAR para {@code standalone/deployments/} antes do start. */
    public WildFlyContainer withDeployment(String warName, MountableFile war) {
        deployments.put(warName, war);
        return self();
    }

    /** Cria um usuário de management (necessário para Arquillian remote). */
    public WildFlyContainer withAdminUser(String user, String password) {
        this.adminUser = user;
        this.adminPassword = password;
        return self();
    }

    /** Atalho para {@code withAdminUser("admin", "admin")}. */
    public WildFlyContainer withAdminUser() {
        return withAdminUser("admin", "admin");
    }

    /**
     * Prefixo das system properties exportadas no {@code beforeAll}.
     * Default: {@code "wildfly"} (gera {@code wildfly.host}, {@code wildfly.http.port}, etc.).
     */
    public WildFlyContainer withSystemPropertyPrefix(String prefix) {
        this.systemPropertyPrefix = prefix;
        return self();
    }

    @Override
    protected void configure() {
        deployments.forEach((name, file) ->
                withCopyFileToContainer(file, DEPLOYMENTS_DIR + name));

        if (adminUser != null) {
            withCommand("sh", "-c",
                    "$JBOSS_HOME/bin/add-user.sh " + adminUser + " " + adminPassword + " --silent && "
                            + "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0");
        } else {
            withCommand("sh", "-c",
                    "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0");
        }
    }

    public int getHttpPort() {
        return getMappedPort(HTTP_PORT);
    }

    public int getManagementPort() {
        return getMappedPort(MANAGEMENT_PORT);
    }

    public URI getHttpBaseUri() {
        return URI.create("http://" + getHost() + ":" + getHttpPort());
    }

    /**
     * Inicia o container imediatamente e devolve {@code this} — para uso em
     * inicializadores estáticos quando outra extensão JUnit (ex.: Arquillian)
     * precisa enxergar as system properties antes do {@code beforeAll} ser
     * invocado.
     */
    public WildFlyContainer started() {
        start();
        return this;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty(systemPropertyPrefix + ".host", getHost());
        System.setProperty(systemPropertyPrefix + ".http.port", String.valueOf(getHttpPort()));
        System.setProperty(systemPropertyPrefix + ".management.port", String.valueOf(getManagementPort()));
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        stop();
    }
}
