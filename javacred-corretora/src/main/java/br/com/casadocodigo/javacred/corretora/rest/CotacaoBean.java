package br.com.casadocodigo.javacred.corretora.rest;

import br.com.casadocodigo.javacred.corretora.entity.LogConsulta;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@Stateless
@Path("/cotacao")
@Produces(MediaType.APPLICATION_JSON)
public class CotacaoBean {

    @PersistenceContext
    private EntityManager entityManager;

    @GET
    @Path("{codigo}/v0")
    public double buscaCotacao(@PathParam("codigo") String codigoAtivo, @QueryParam("quantidade") double quantidade, @Context HttpServletRequest request){

        System.out.println("Buscando cotação no banco de dados...");
        double cotacao = new Random().nextDouble();

        entityManager.persist(new LogConsulta(codigoAtivo, cotacao, quantidade, request.getRemoteAddr()));

        return cotacao;
    }

    @GET
    @Path("{codigo}")
    public LogConsulta buscaCotacao0(@PathParam("codigo") String codigoAtivo, @QueryParam("quantidade") double quantidade, @Context HttpServletRequest request){

        System.out.println("Buscando cotação no banco de dados...");
        double cotacao = new Random().nextDouble();
        LogConsulta logConsulta = new LogConsulta(codigoAtivo, cotacao, quantidade, request.getRemoteAddr());

        entityManager.persist(logConsulta);

        return logConsulta;
    }
}
