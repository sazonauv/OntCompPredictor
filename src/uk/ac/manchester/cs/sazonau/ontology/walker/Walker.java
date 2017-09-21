package uk.ac.manchester.cs.sazonau.ontology.walker;

public interface Walker {
	
	public static final String FULL_MEM = "full-memory";
	
	public static final String SHORT_MEM = "short-memory";
	
	public static final String MODULES = "modules";
	
	public Object next();	
	
}
