package br.com.casadocodigo.javacred.corretora.rest;

import br.com.casadocodigo.javacred.corretora.entity.Ordem;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Stateless
@Path("/ordem")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdemBean {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void configuracaoInicial(){
        if(em.find(Ordem.class, 1) == null){
            em.persist(new Ordem(Ordem.Tipo.COMPRA, "BBAS3", 34.5, 100, "localhost"));
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarOrdem(@PathParam("id") Integer id, @Context Request request, @Context HttpHeaders httpHeaders){

        System.out.println(httpHeaders.getRequestHeaders());

        Date ultimaAlteracaoOrdem = buscarUltimaAlteracaoOrdem(id);

        if(ultimaAlteracaoOrdem == null){
            return Response.noContent().build();
        }

        Response.ResponseBuilder builder = request.evaluatePreconditions(ultimaAlteracaoOrdem);

        //se foi alterado depois da útlima consulta, gerar nova resposta
        if(builder == null){
            Ordem contrato = em.find(Ordem.class, id);
//            aplicarCorrecaoMonetariaContrato(contrato, buscarIndices());
//            em.flush();
            ultimaAlteracaoOrdem = contrato.getUltimaModificacao();
            builder = Response.ok(contrato);
        }

        builder.lastModified(ultimaAlteracaoOrdem);

        CacheControl cc = new CacheControl();
//        cc.setMaxAge(((int) TimeUnit.MINUTES.toSeconds(30)));
        cc.setMaxAge(((int) TimeUnit.SECONDS.toSeconds(10)));
        builder.cacheControl(cc);
        return builder.build();
    }

    private Date buscarUltimaAlteracaoOrdem(Integer id) {
        try{
            return em.createNamedQuery(Ordem.BUSCAR_DATA_ULTIMA_ALTERACAO, Date.class).setParameter("id", id).getSingleResult();

        }catch (NoResultException e){
            return null;
        }
    }

    @POST
    public Ordem salvar(Ordem ordem){
        return em.merge(ordem);
    }

    @Path("/v2")
    @POST
    public Response salvar(Ordem ordem, @Context Request request, @Context HttpHeaders httpHeaders){

        System.out.println("OrdemBean.salvar() >> " + ordem + " Request: " + request);

        if(ordem.getId() != null){

            Integer hash = buscarHashOrdem(ordem.getId());
            System.out.println(ordem);
            System.out.println("Hash do ordem param: " + ordem.hashCode());
            System.out.println("Hash do ordem select: " + hash);

            System.out.println(httpHeaders.getRequestHeaders());

            EntityTag etag = new EntityTag(String.valueOf(hash), true);

            Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

            if(builder != null){
                return builder.build();
            }
            else{
                System.out.println("MERGE!!!!!!");
                em.merge(ordem);
                return Response.noContent().build();
            }
        }

        em.persist(ordem);

        return Response.ok(ordem).build();
    }

    private Integer buscarHashOrdem(Integer id) {
        try {
            return em.createNamedQuery(Ordem.BUSCAR_HASH, Integer.class).setParameter("id", id).getSingleResult();

        }catch (NoResultException e){
            return null;
        }

    }
}
