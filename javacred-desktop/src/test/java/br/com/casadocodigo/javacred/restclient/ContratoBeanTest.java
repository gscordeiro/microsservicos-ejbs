package br.com.casadocodigo.javacred.restclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.casadocodigo.javacred.restclient.bean.MeuContrato;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class ContratoBeanTest extends JavacredTestBase {


    private Response response;

    @BeforeEach
    public void fazRequisicao(){
        WebTarget contratoBean = javacred.path("contrato")
                .path("{id}")
                .resolveTemplate("id", 1);

        Invocation.Builder requestBuilder = contratoBean
                .request(MediaType.APPLICATION_JSON);

        response = requestBuilder.get();
    }

    @AfterEach
    public void fechaResposta(){
        response.close();
    }

    @Test
    public void deveBuscarContratoPorId() {

        //código duplicado apenas para ficar igual ao exemplo do livro.
        Client client = ClientBuilder.newClient();
        WebTarget javacred =
                client.target("http://localhost:8080/javacred/rest");

        WebTarget contratoBean = javacred.path("contrato");

        Response response = contratoBean.path("{id}")
                .resolveTemplate("id", 1)
                .request(MediaType.APPLICATION_JSON)
                .get();

        String json = response.readEntity(String.class);

        Assertions.assertEquals(200, response.getStatus());

        System.out.println(json);

        response.close();

    }

    @Test
    public void deveRecuperarDescricaoENome() throws IOException {

        String json = response.readEntity(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);


        String descricao = rootNode.path("descricao").asText();
        String nomeCliente = rootNode.path("cliente").path("nome").asText();

        Assertions.assertNotNull(descricao);
        System.out.println(descricao);

        Assertions.assertNotNull(nomeCliente);
        System.out.println(nomeCliente);

    }

    @Test
    public void deveConverterRespostaEmObjetosJava(){

        MeuContrato contrato = response.readEntity(MeuContrato.class);
        String descricao = contrato.getDescricao();
        String nomeCliente = contrato.getCliente().getNome();

        Assertions.assertNotNull(descricao);
        System.out.println(descricao);

        Assertions.assertNotNull(nomeCliente);
        System.out.println(nomeCliente);

    }

    @Test
    public void deveLidarComColecoes() throws Exception {

        //recria o javacred porque não usa o mesmo response que os outros testes
        criaCliente();

        WebTarget contratoBean = javacred.path("contrato");

        response = contratoBean.path("todos").request(MediaType.APPLICATION_JSON).get();

        List<MeuContrato> contratos = response.readEntity(new GenericType<List<MeuContrato>>() {});

        //escrevendo resultado antes do Java 8
        for(MeuContrato contrato : contratos){
            Assertions.assertNotNull(contrato);
                System.out.println(contrato);
        }

        //ou com Java 8...
        contratos.forEach(Assertions::assertNotNull);
        contratos.forEach(System.out::println);

    }


}
