package br.com.casadocodigo.javacred.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinition(
        name = "java:/datasources/JavacredDS",
        className = "org.h2.jdbcx.JdbcDataSource",
        url = "jdbc:h2:file:~/temp/javacred_db;AUTO_SERVER=TRUE",
        user = "sa",
        password = "sa"
)
@ApplicationScoped
public class DatasourceConfig {
}
