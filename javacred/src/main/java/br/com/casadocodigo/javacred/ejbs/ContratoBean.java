package br.com.casadocodigo.javacred.ejbs;

import br.com.casadocodigo.javacred.entidades.Contrato;
import br.com.casadocodigo.javacred.entidades.Indice;
import br.com.casadocodigo.javacred.entidades.IndiceValor;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.YearMonth;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;


@Stateless
@Path("/contrato")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContratoBean {

	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AjustadorContratoBean ajustadorContratoBean;
	
	@Resource
	private ManagedExecutorService executorService;

    @Resource(name="concurrent/tf/DefaultThreadFactory")
    private ManagedThreadFactory managedThreadFactory;

    private ExecutorService _executorService;
	
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	@GET
	@Path("/{identificador}")
	public Contrato buscarContrato(@PathParam("identificador") Integer identificador){

		return em.find(Contrato.class, identificador);
	}
	
	@POST
	public Contrato salvar(Contrato contrato){
		if(contrato.getSaldo() == null){
			contrato.setSaldo(contrato.getValor());
		}

		System.out.println("ContratoBean.salvar() >> " + contrato);

		return em.merge(contrato);
	}
	
	@GET
	@Path("/todos")
	public List<Contrato> listarTodos(){
		return em.createQuery("select c from Contrato c", Contrato.class).getResultList();
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void quitar(Contrato contrato) {
		contrato.setSaldo(0.0);
		em.merge(contrato);
		
	}

	public List<Indice> buscarIndices(){
	    return em.createQuery("select i from Indice i join fetch i.valores", Indice.class).getResultList();
    }

	private double calculaPercentualCorrecao(Contrato contrato, List<Indice> indices){

		Indice indiceAplicar = indices.stream()
									.filter(i -> i.equals(contrato.getIndiceCorrecao()))
									.findFirst().get();

		Stream<IndiceValor> valoresAplicar = indiceAplicar.getValores().stream()
				.filter(v -> contrato.getUltimaCorrecao() == null || v.getAnoMes().isAfter(contrato.getUltimaCorrecao()));

		Double somaIndice = valoresAplicar
				.mapToDouble(v -> v.getValor() + 1)
				.reduce(1, (i1, i2) -> i1 * i2);

		return somaIndice - 1;
	}

	public Long contaContratos() {
		return em.createQuery("select count(c) from Contrato c", Long.class).getSingleResult();
	}

	@TransactionTimeout(value = 5, unit = TimeUnit.MINUTES)
	public double corrigeContratos(List<Indice> indices) {
		
		
		double saldoGeral = 0.0;
		for(Contrato contrato : listarTodos()){
			Double novoSaldo = corrigeContrato(contrato, indices);
			saldoGeral += novoSaldo;
			
			try {
				System.out.println("ContratoBean.ajustaContratosPercentual() > SLEEP 5ms");
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}
		
		return saldoGeral;
	}

	private double corrigeContrato(Contrato contrato, List<Indice> indices){
        double percentual = calculaPercentualCorrecao(contrato, indices);
        Double novoSaldo = contrato.getSaldo() * (1 + percentual);
        contrato.setSaldo(novoSaldo);
        contrato.setUltimaCorrecao(YearMonth.now());
        return novoSaldo;
    }


	@Asynchronous
	public Future<Double> corrigeContratosAsync(List<Indice> indices) {
		
		double salgoGeral = corrigeContratos(indices);
		
		return new AsyncResult<>(salgoGeral);
	}

	public double corrigeContratosPar(List<Indice> indices) {
		
		int i = 0;
		List<Future<Double>> resultatos = new LinkedList<>();
		
		for(Contrato contrato : listarTodos()){

			System.out.printf(">>>>>> enfileirando: %d -> Thread: %s \n", ++i, Thread.currentThread());
			double percentual = calculaPercentualCorrecao(contrato, indices);
			em.detach(contrato);
			resultatos.add(ajustadorContratoBean.ajustaContratoPercentualAsync(contrato, percentual));
		}
		
		double saldoGeral = 0.0;
		
		for (Future<Double> resultado : resultatos) {
			
			try {
				
				saldoGeral += resultado.get();
				
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
		}
		
		return saldoGeral;
	}

	@Asynchronous
	public Future<Double> corrigeContratosAsyncPar(List<Indice> indices) throws Exception {
		
		return new AsyncResult<>(corrigeContratosPar(indices));
		
	}

	public double corrigeContratosViaMES1(List<Indice> indices) {
		
		int i = 0;
		List<Future<Double>> resultatos = new LinkedList<>();

		for(Contrato contrato : listarTodos()){
			
			System.out.printf(">>>>>> enfileirando: %d -> Thread: %s \n", ++i, Thread.currentThread());
			double percentual = calculaPercentualCorrecao(contrato, indices);
			em.detach(contrato);
			resultatos.add(getExecutor().submit(new AjustadorContratoCallable(contrato, percentual, ajustadorContratoBean)));
		}
		
		double saldoGeral = 0.0;
		
		try {
			
			for (Future<Double> resultado : resultatos) {
				saldoGeral += resultado.get();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return saldoGeral;
	}

	public double corrigeContratosViaMES2(List<Indice> indices) {
		
		int i = 0;
		List<Future<Double>> resultatos;
		List<Callable<Double>> tasks = new LinkedList<>();
		
		for(Contrato contrato : listarTodos()){
			
			System.out.printf(">>>>>> enfileirando: %d -> Thread: %s \n", ++i, Thread.currentThread());
			double percentual = calculaPercentualCorrecao(contrato, indices);
			em.detach(contrato);
			tasks.add(new AjustadorContratoCallable(contrato, percentual, ajustadorContratoBean));
		}
		
		double saldoGeral = 0.0;
		
		try {
			
			resultatos = executorService.invokeAll(tasks);
			
			for (Future<Double> resultado : resultatos) {
				saldoGeral += resultado.get();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return saldoGeral;
	}

    private ExecutorService getExecutor() {

        if (_executorService == null) {
            int corePoolSize = 5;
            int maximumPoolSize = 5;
            long keepAliveTime = 10;
            int tamanhoFila = 20_000;

            _executorService =  new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(tamanhoFila),
                    managedThreadFactory);
        }

        return _executorService;
    }
}
