package br.com.casadocodigo.javacred.corretora.test;

import br.com.casadocodigo.javacred.corretora.test.bean.OrdemTO;
import br.com.casadocodigo.javacred.corretora.test.proxy.OrdemBeanProxy;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

@Disabled
public class OrdemBeanProxyTest {

    private OrdemBeanProxy ordemBean;

    @BeforeEach
    public void configuraProxy(){
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080");
        ResteasyWebTarget rtarget = (ResteasyWebTarget) target;

        ordemBean = rtarget.proxy(OrdemBeanProxy.class);
    }

    @Test
    public void testaBuscaComProxy(){

        OrdemTO ordem = ordemBean.buscar(1);

        Assertions.assertNotNull(ordem);

        System.out.println(ordem);
    }

    @Test
    public void testaPostComProxy(){

        OrdemTO novaOrdem  = new OrdemTO(OrdemTO.Tipo.VENDA, "BBAS3", 200, 35.0, "test");


        OrdemTO ordemSalva = ordemBean.salvar(novaOrdem);

        Assertions.assertNotNull(ordemSalva.getId());
        Assertions.assertEquals(OrdemTO.Tipo.VENDA, ordemSalva.getTipo());
        Assertions.assertEquals("BBAS3", ordemSalva.getCodigoAtivo());
        Assertions.assertEquals(200, ordemSalva.getQuantidade(), 0.001);

        System.out.println(ordemSalva);
    }


    @Test
    public void testaPostCondicionalComProxy(){

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080");
        ResteasyWebTarget rtarget = (ResteasyWebTarget) target;

        OrdemBeanProxy ordemBean = rtarget.proxy(OrdemBeanProxy.class);

        OrdemTO ordem = ordemBean.buscar(1);//.readEntity(OrdemTO.class);

        EntityTag etag = new EntityTag(String.valueOf(ordem.hashCode()), true);

        Response response = ordemBean.salvar(ordem, etag);

        System.out.println(response.getStatus());
        System.out.println(response.getEntity());
    }
}
