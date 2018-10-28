package nz.ac.auckland.concert.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import nz.ac.auckland.concert.services.ConcertResource;
import nz.ac.auckland.concert.services.PersistenceManager;

@ApplicationPath("/services")
public class ConcertApplication extends Application{
	private Set<Object> _singletons = new HashSet<Object>();
	private Set<Class<?>> _classes = new HashSet<Class<?>>();
	
	public ConcertApplication() {
		_singletons.add(new PersistenceManager());
		_classes.add(ConcertResource.class);
	}
	
	@Override
	public Set<Object> getSingletons(){
		return _singletons;
	}
	
	@Override
	public Set<Class<?>> getClasses(){
		return _classes;
	}
	
}
