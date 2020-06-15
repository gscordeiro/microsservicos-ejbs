package br.com.casadocodigo.javacred;

import br.com.casadocodigo.javacred.ejbs.ContratoBean;
import br.com.casadocodigo.javacred.entidades.Cliente;
import br.com.casadocodigo.javacred.entidades.Contrato;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.faces.annotation.FacesConfig;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
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
