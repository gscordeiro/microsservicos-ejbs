package br.com.casadocodigo.javacred.corretora.rest;

import br.com.casadocodigo.javacred.corretora.entity.LogConsulta;
import br.com.casadocodigo.javacred.corretora.cliente.Consulta;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Stateless
@Path("/cotacao/v2")
@Produces(MediaType.APPLICATION_JSON)
public class CotacaoBeanV2 {

    @PersistenceContext
    private EntityManager entityManager;

    @GET
    @Path("{codigo}")
    public Response buscaCotacao(@PathParam("codigo") String codigoAtivo, @QueryParam("quantidade") double quantidade, @Context HttpServletRequest request){

        System.out.println("Buscando cotação no banco de dados...");
        double cotacao = new Random().nextDouble();
        LogConsulta logConsulta = new LogConsulta(codigoAtivo, cotacao, quantidade, request.getRemoteAddr());

        entityManager.persist(logConsulta);

        CacheControl cc = new CacheControl();
        cc.setMaxAge(((int) TimeUnit.MINUTES.toSeconds(15))); //delay padrão de serviços gratuitos
//        cc.setMaxAge(((int) TimeUnit.SECONDS.toSeconds(10)));

        return Response.ok(logConsulta).cacheControl(cc).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response buscaCotacaoForm(@FormParam("codigo") String codigoAtivo, @FormParam("quantidade") double quantidade, @Context HttpServletRequest request){

        System.out.println("CotacaoBeanV2.simulaFinanciamentoForm");

        return buscaCotacao(codigoAtivo, quantidade, request);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response buscaCotacaoJson(Consulta consulta, @Context HttpServletRequest request){

        System.out.println("CotacaoBeanV2.simulaFinanciamentoJson");

        return buscaCotacao(consulta.getCodigoAtivo(), consulta.getQuantidade(), request);
    }
}
