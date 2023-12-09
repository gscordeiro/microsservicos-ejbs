package br.com.casadocodigo.javacred;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;
//import org.hibernate.Session;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.faces.annotation.FacesConfig;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
@FacesConfig
public class StartupConfig {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ContratoBean contratoBean;

    @PostConstruct
    public void startup(){

        if(contratoBean.buscarContrato(1) == null){

            em.createNativeQuery("truncate table Contrato").executeUpdate();
//            em.createNativeQuery("truncate table Cliente").executeUpdate();

            Contrato contrato = new Contrato(new Cliente("Fulano", true));
            contrato.setDescricao("Contrato teste");
            contrato.setValor(12_000.0);
            contratoBean.salvar(contrato);

        }
    }
}
