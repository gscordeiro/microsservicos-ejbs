package br.com.casadocodigo.javacred.corretora.test.proxy;

import br.com.casadocodigo.javacred.corretora.test.bean.OrdemTO;

import javax.ws.rs.*;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ordem")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OrdemBeanProxy {

    @GET
    @Path("/{id}")
    OrdemTO buscar(@PathParam("id") Integer id);

    @POST
    OrdemTO salvar(OrdemTO ordem);

    @POST
    @Path("/v2")
    Response salvar(OrdemTO ordem, @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag etag);
}
