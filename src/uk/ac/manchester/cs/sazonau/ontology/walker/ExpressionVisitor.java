package uk.ac.manchester.cs.sazonau.ontology.walker;

import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

/** Visits existential restrictions and collects the properties which are
 * restricted */
public class ExpressionVisitor extends OWLClassExpressionVisitorAdapter {
	
	public int nObjectSomeValuesFrom;
	public int nObjectAllValuesFrom;
	public int nObjectMinCardinality;
	public int nObjectMaxCardinality;
	public int nObjectExactCardinality;	
	public int nObjectIntersectionOf;
	public int nObjectUnionOf;
	public int nDataExactCardinality;
	public int nDataAllValuesFrom;
	public int nDataSomeValuesFrom;
	public int nDataMinCardinality;
	public int nDataMaxCardinality;
			
	@Override
	public void visit(OWLObjectSomeValuesFrom desc) {		
		nObjectSomeValuesFrom++;
	}
	
	@Override
	public void visit(OWLObjectAllValuesFrom desc) {		
		nObjectAllValuesFrom++;	
	}
	
	@Override
	public void visit(OWLObjectMinCardinality desc) {		
		nObjectMinCardinality++;		
	}
	
	@Override
	public void visit(OWLObjectMaxCardinality desc) {		
		nObjectMaxCardinality++;		
	}
	
	@Override
	public void visit(OWLObjectExactCardinality desc) {		
		nObjectExactCardinality++;	
	}
	
	@Override
	public void visit(OWLObjectIntersectionOf desc) {		
		nObjectIntersectionOf++;		
	}
	
	@Override
	public void visit(OWLObjectUnionOf desc) {		
		nObjectUnionOf++;		
	}
	
	@Override
	public void visit(OWLDataExactCardinality desc) {	
		nDataExactCardinality++;		
	}
	
	@Override
	public void visit(OWLDataAllValuesFrom desc) {	
		nDataAllValuesFrom++;	
	}
	
	@Override
	public void visit(OWLDataSomeValuesFrom desc) {	
		nDataSomeValuesFrom++;	
	}
	
	@Override
	public void visit(OWLDataMinCardinality desc) {	
		nDataMinCardinality++;		
	}
	
	@Override
	public void visit(OWLDataMaxCardinality desc) {	
		nDataMaxCardinality++;	
	}	
		
	public void clear() {
		nObjectSomeValuesFrom = 0;
		nObjectAllValuesFrom = 0;
		nObjectMinCardinality = 0;
		nObjectMaxCardinality = 0;
		nObjectExactCardinality = 0;	
		nObjectIntersectionOf = 0;
		nObjectUnionOf = 0;
		nDataExactCardinality = 0;
		nDataAllValuesFrom = 0;
		nDataSomeValuesFrom = 0;
		nDataMinCardinality = 0;
		nDataMaxCardinality = 0;
	}
} 
