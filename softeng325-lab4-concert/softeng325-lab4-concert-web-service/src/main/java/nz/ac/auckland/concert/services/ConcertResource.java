package nz.ac.auckland.concert.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.domain.Concert;
import nz.ac.auckland.concert.domain.Performer;

@Path("/concerts")
public class ConcertResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	EntityManager em = PersistenceManager.instance().createEntityManager();
	private AtomicLong _idCounter = new AtomicLong();

	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_XML})
	public Response retrieveConcert(@PathParam("id") long id) {
		em.getTransaction().begin();
		Concert concert = em.find(Concert.class, id);

		if(concert == null) {
			return Response.status(404).build();
		}

		
		_logger.info("concert ID: "+concert.getId()+" retrieved");
		ResponseBuilder builder = Response.ok(concert).status(200);

		return builder.build();
	}


	@POST
	@Consumes({MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_XML})
	public Response createConcert(Concert concert) {
		
		String title = concert.getTitle();
		LocalDateTime date = concert.getDate();
		Performer performer = concert.getPerformer(); 
		em.getTransaction().begin();
		
		Concert newConcert = new Concert(_idCounter.incrementAndGet(),title,date,performer);
		em.persist(newConcert);
		_logger.info("concert ID: "+concert.getId()+" created");
		em.getTransaction().commit();
		return Response.created(URI.create("/concerts/"+concert.getId())).status(201).build();
		
	}

	@PUT
	@Consumes({MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_XML})
	public Response updateConcert(Concert concert) {
		em.getTransaction().begin();
		Concert concrt = em.find(Concert.class, concert.getId());
		if(concrt == null) {
			return Response.status(404).build();
		}
		em.merge(concert);
		em.getTransaction().commit();
		_logger.info("concert ID: "+concert.getId()+" updated");
		return Response.status(204).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteConcert(@PathParam("id") long id) {
		em.getTransaction().begin();
		Concert concert = em.find(Concert.class, id);
		if(concert == null) {
			return Response.status(404).build();
		}
		em.remove(concert);
		_logger.info("concerts id:" +concert.getId()+" cleared");
		em.getTransaction().commit();
		return Response.status(204).build();
	}

	@DELETE
	public Response deleteAllConcert() {
		em.getTransaction().begin();
		TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c",Concert.class);
		List<Concert> concerts = concertQuery.getResultList();
		for(int i = 0; i < concerts.size();i++) {
			em.remove(concerts.get(i));
		}
		_logger.info("all concerts cleared");
		_idCounter = new AtomicLong();
		em.getTransaction().commit();
		return Response.status(204).build();
	}

}
