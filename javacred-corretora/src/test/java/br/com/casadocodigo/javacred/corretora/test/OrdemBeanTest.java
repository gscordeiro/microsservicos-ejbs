package br.com.casadocodigo.javacred.corretora.test;

import br.com.casadocodigo.javacred.corretora.test.bean.OrdemTO;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCacheFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Disabled
public class OrdemBeanTest {

    @Test
    public void testarGetCondicional() throws InterruptedException {
        Client client = ClientBuilder.newClient();

        client.register(new BrowserCacheFeature());

        WebTarget javacred = client.target("http://localhost:8080");

        WebTarget ordemBean = javacred.path("ordem");
        Response response = null;

        for(int i = 0; i < 6; i++){
            response = ordemBean.path("{id}")
                    .resolveTemplate("id", 1)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

//            String json = response.readEntity(String.class);
            OrdemTO ordemTO = response.readEntity(OrdemTO.class);

            System.out.println(new Date());
            System.out.println(response.getStatus());
            System.out.println(ordemTO);
            System.out.println(response.getHeaders());


            System.out.println("=========SLEEP 5=========");
            Thread.sleep(5_000);
        }

        response.close();
    }

    @Test
    public void testarPost(){
        Client client = ClientBuilder.newClient();

        OrdemTO novaOrdem  = new OrdemTO(OrdemTO.Tipo.COMPRA, "PETR4", 100, 14.0, "test");

        WebTarget ordemBean = client.target("http://localhost:8080/ordem");
        Response response = ordemBean.request()
                .post(Entity.json(novaOrdem));

        OrdemTO ordemSalva = response.readEntity(OrdemTO.class);

        Assertions.assertNotNull(ordemSalva.getId());
        Assertions.assertEquals(OrdemTO.Tipo.COMPRA, ordemSalva.getTipo());
        Assertions.assertEquals("PETR4", ordemSalva.getCodigoAtivo());
        Assertions.assertEquals(100, ordemSalva.getQuantidade(), 0.001);

        System.out.println(ordemSalva);

    }

    @Test
    public void testarPostCondicionalComAlteracao(){
        testarPostCondicional(true);
    }

    @Test
    public void testarPostCondicionalSemAlteracao(){
        testarPostCondicional(false);
    }

    public void testarPostCondicional(boolean alterarOrdem){
        Client client = ClientBuilder.newClient();
        client.register(new BrowserCacheFeature());

        WebTarget javacred = client.target("http://localhost:8080");

        WebTarget ordemBean = javacred.path("ordem");

        Response response = ordemBean.path("{id}")
                .resolveTemplate("id", 1)
                .request(MediaType.APPLICATION_JSON)
                .get();


        //aqui temos a ordem com os dados que vieram do servidor
        OrdemTO ordemTO = response.readEntity(OrdemTO.class);

        boolean objetoNovo = false;
        if(ordemTO == null){
            //se objeto não existe vamos criar
            ordemTO  = new OrdemTO(OrdemTO.Tipo.COMPRA, "PETR4", 100, 14.0, "test");
            objetoNovo = true;
        }
        else if(alterarOrdem){
            //se teste pedir, vamos alterá-lo
            ordemTO.setQuantidade(ordemTO.getQuantidade() + 100);
        }

        //vamos crar a etag lembrando de também definir como weak = true
        EntityTag etag = new EntityTag(String.valueOf(ordemTO.hashCode()), true);

        //enviamos o POST condicional
        response = ordemBean.path("/v2").request()
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .post(Entity.json(ordemTO));

        System.out.println(response.getStatus());
        System.out.println(response.getHeaders());
        System.out.println(response);

        if(objetoNovo){
            Assertions.assertEquals(200, response.getStatus());
        }
        else if(alterarOrdem){
            Assertions.assertEquals(204, response.getStatus());
        }
        else{
            Assertions.assertEquals(412, response.getStatus());
        }

        response.close();

    }
}
