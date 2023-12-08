package br.com.casadocodigo.javacred.corretora.rest;

import br.com.casadocodigo.javacred.corretora.entity.LogConsulta;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
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
