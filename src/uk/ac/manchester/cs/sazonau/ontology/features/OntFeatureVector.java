package uk.ac.manchester.cs.sazonau.ontology.features;

import uk.ac.manchester.cs.sazonau.ontology.walker.AxiomVisitor;
import uk.ac.manchester.cs.sazonau.ontology.walker.ExpressionVisitor;


public class OntFeatureVector {
	
	// Atom Signature features
	public double nClasses;
	public double nObjectProperties;
	public double nDataProperties;	
	public double nDistinctClasses;
	public double nDistinctObjectProperties;
	public double nDistinctDataProperties;
	
	// Class Expression features 
	public double nObjectSomeValuesFrom;
	public double nObjectAllValuesFrom;
	public double nObjectMinCardinality;
	public double nObjectMaxCardinality;
	public double nObjectExactCardinality;	
	public double nObjectIntersectionOf;
	public double nObjectUnionOf;
	public double nDataExactCardinality;
	public double nDataAllValuesFrom;
	public double nDataSomeValuesFrom;
	public double nDataMinCardinality;
	public double nDataMaxCardinality;
	
	// Axiom features
	public double nSubClassOfAxioms;
	public double nDisjointClassesAxioms;
	public double nEquivalentClassesAxioms;
	public double nAsymmetricObjectPropertyAxioms;
	public double nSymmetricObjectPropertyAxioms;
	public double nInverseObjectPropertiesAxioms;
	public double nInverseFunctionalObjectPropertyAxioms;
	public double nFunctionalObjectPropertyAxioms;
	public double nTransitiveObjectPropertyAxioms;
	public double nFunctionalDataPropertyAxioms;
	public double nDataPropertyRangeAxioms;
	public double nDataPropertyDomainAxioms;
	public double nObjectPropertyDomainAxioms;
	public double nObjectPropertyRangeAxioms;
	public double nSubObjectPropertyOfAxioms; 
	public double nSubDataPropertyOfAxioms;
	public double nReflexiveObjectPropertyAxioms;
	public double nEquivalentObjectPropertiesAxioms;
	public double nEquivalentDataPropertiesAxioms;
	public double nDisjointObjectPropertiesAxioms;
	public double nDisjointDataPropertiesAxioms;
	
	public double nDifferentIndividualsAxioms;
	public double nSameIndividualAxioms;
	
	public double nClassAssertionAxioms;
	public double nObjectPropertyAssertionAxioms;
	public double nDataPropertyAssertionAxioms;
	
	public double nDisjointUnionAxioms;
	public double nHasKeyAxioms;
	public double nIrreflexiveObjectPropertyAxioms;
	public double nNegativeDataPropertyAssertionAxioms;
	public double nNegativeObjectPropertyAssertionAxioms;
	public double nSubPropertyChainOfAxioms;
	public double nSWRLRules;
	
	// Atom Structural features
//	public int nDependents;
	public double parsingDepth;
//	public double signatureSize;
	
	// EL, QL, RL profiles
	public double profileEL;
	public double profileQL;
	public double profileRL;
	
	// Asserted class hierarchy
	// |E|/|N|
	public double ENR;
	// |subClassOf|-|N|+1
	public double TIP;
	// -Sum(pi*log(pi)), pi - a prob. of having i edges (in + out)
	public double EOG;
	// #children
	public double NOC;
	// #parents
	public double NOP;
	// max path to the root class
	public double DIT;
	
	
	public OntFeatureVector() {
		super();
	}
	
	public Double[] toArray() {
		return new Double[]{
				nClasses,
				nObjectProperties,
				nDataProperties,
				nDistinctClasses,
				nDistinctObjectProperties,
				nDistinctDataProperties,
				nObjectSomeValuesFrom,
				nObjectAllValuesFrom,
				nObjectMinCardinality,
				nObjectMaxCardinality,
				nObjectExactCardinality,
				nObjectIntersectionOf,
				nObjectUnionOf,
				nDataExactCardinality,
				nDataAllValuesFrom,
				nDataSomeValuesFrom,
				nDataMinCardinality,
				nDataMaxCardinality,
				nSubClassOfAxioms,
				nDisjointClassesAxioms,
				nEquivalentClassesAxioms,
				nAsymmetricObjectPropertyAxioms,
				nSymmetricObjectPropertyAxioms,
				nInverseObjectPropertiesAxioms,
				nInverseFunctionalObjectPropertyAxioms,
				nFunctionalObjectPropertyAxioms,
				nTransitiveObjectPropertyAxioms,
				nFunctionalDataPropertyAxioms,
				nDataPropertyRangeAxioms,
				nDataPropertyDomainAxioms,
				nObjectPropertyDomainAxioms,
				nObjectPropertyRangeAxioms,
				nSubObjectPropertyOfAxioms, 
				nSubDataPropertyOfAxioms,
				nReflexiveObjectPropertyAxioms,
				nEquivalentObjectPropertiesAxioms,
				nEquivalentDataPropertiesAxioms,
				nDisjointObjectPropertiesAxioms,
				nDisjointDataPropertiesAxioms,
				nDifferentIndividualsAxioms,
				nSameIndividualAxioms,
				nClassAssertionAxioms,
				nObjectPropertyAssertionAxioms,
				nDataPropertyAssertionAxioms,
				nDisjointUnionAxioms,
				nHasKeyAxioms,
				nIrreflexiveObjectPropertyAxioms,
				nNegativeDataPropertyAssertionAxioms,
				nNegativeObjectPropertyAssertionAxioms,
				nSubPropertyChainOfAxioms,
				nSWRLRules,
				parsingDepth,
//				signatureSize
		};
	}
	
	public void clear() {
		nClasses = 0;
		nObjectProperties = 0;
		nDataProperties = 0;
		nDistinctClasses = 0;
		nDistinctObjectProperties = 0;
		nDistinctDataProperties = 0;
		
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
		
		nSubClassOfAxioms = 0;
		nDisjointClassesAxioms = 0;
		nEquivalentClassesAxioms = 0;
		nAsymmetricObjectPropertyAxioms = 0;
		nSymmetricObjectPropertyAxioms = 0;
		nInverseObjectPropertiesAxioms = 0;
		nInverseFunctionalObjectPropertyAxioms = 0;
		nFunctionalObjectPropertyAxioms = 0;
		nTransitiveObjectPropertyAxioms = 0;
		nFunctionalDataPropertyAxioms = 0;
		nDataPropertyRangeAxioms = 0;
		nDataPropertyDomainAxioms = 0;
		nObjectPropertyDomainAxioms = 0;
		nObjectPropertyRangeAxioms = 0;
		nSubObjectPropertyOfAxioms = 0;
		nSubDataPropertyOfAxioms = 0;
		nReflexiveObjectPropertyAxioms = 0;
		nEquivalentObjectPropertiesAxioms = 0;
		nEquivalentDataPropertiesAxioms = 0;
		nDisjointObjectPropertiesAxioms = 0;
		nDisjointDataPropertiesAxioms = 0;
		
		nDifferentIndividualsAxioms = 0;
		nSameIndividualAxioms = 0;
		
		nClassAssertionAxioms = 0;
		nObjectPropertyAssertionAxioms = 0;
		nDataPropertyAssertionAxioms = 0;
		
		nDisjointUnionAxioms = 0;
		nHasKeyAxioms = 0;
		nIrreflexiveObjectPropertyAxioms = 0;
		nNegativeDataPropertyAssertionAxioms = 0;
		nNegativeObjectPropertyAssertionAxioms = 0;
		nSubPropertyChainOfAxioms = 0;
		nSWRLRules = 0;
		
//		nDependents = 0;
		parsingDepth = 0;
//		signatureSize = 0;
	}
	
	public void addExpressionVisitorData(ExpressionVisitor visitor) {
		nObjectSomeValuesFrom += visitor.nObjectSomeValuesFrom;
		nObjectAllValuesFrom += visitor.nObjectAllValuesFrom;
		nObjectMinCardinality += visitor.nObjectMinCardinality;
		nObjectMaxCardinality += visitor.nObjectMaxCardinality;
		nObjectExactCardinality += visitor.nObjectExactCardinality;	
		nObjectIntersectionOf += visitor.nObjectIntersectionOf;
		nObjectUnionOf += visitor.nObjectUnionOf;
		nDataExactCardinality += visitor.nDataExactCardinality;
		nDataAllValuesFrom += visitor.nDataAllValuesFrom;
		nDataSomeValuesFrom += visitor.nDataSomeValuesFrom;
		nDataMinCardinality += visitor.nDataMinCardinality;
		nDataMaxCardinality += visitor.nDataMaxCardinality;
	}
	
	public void addAxiomVisitorData(AxiomVisitor visitor) {
		nSubClassOfAxioms += visitor.nSubClassOfAxioms;
		nDisjointClassesAxioms += visitor.nDisjointClassesAxioms;
		nEquivalentClassesAxioms += visitor.nEquivalentClassesAxioms;
		nAsymmetricObjectPropertyAxioms += visitor.nAsymmetricObjectPropertyAxioms;
		nSymmetricObjectPropertyAxioms += visitor.nSymmetricObjectPropertyAxioms;
		nInverseObjectPropertiesAxioms += visitor.nInverseObjectPropertiesAxioms;
		nInverseFunctionalObjectPropertyAxioms += visitor.nInverseFunctionalObjectPropertyAxioms;
		nFunctionalObjectPropertyAxioms += visitor.nFunctionalObjectPropertyAxioms;
		nTransitiveObjectPropertyAxioms += visitor.nTransitiveObjectPropertyAxioms;
		nFunctionalDataPropertyAxioms += visitor.nFunctionalDataPropertyAxioms;
		nDataPropertyRangeAxioms += visitor.nDataPropertyRangeAxioms;
		nDataPropertyDomainAxioms += visitor.nDataPropertyDomainAxioms;
		nObjectPropertyDomainAxioms += visitor.nObjectPropertyDomainAxioms;
		nObjectPropertyRangeAxioms += visitor.nObjectPropertyRangeAxioms;
		nSubObjectPropertyOfAxioms += visitor.nSubObjectPropertyOfAxioms;
		nSubDataPropertyOfAxioms += visitor.nSubDataPropertyOfAxioms;
		nReflexiveObjectPropertyAxioms += visitor.nReflexiveObjectPropertyAxioms;
		nEquivalentObjectPropertiesAxioms += visitor.nEquivalentObjectPropertiesAxioms;
		nEquivalentDataPropertiesAxioms += visitor.nEquivalentDataPropertiesAxioms;
		nDisjointObjectPropertiesAxioms += visitor.nDisjointObjectPropertiesAxioms;
		nDisjointDataPropertiesAxioms += visitor.nDisjointDataPropertiesAxioms;
		
		nDifferentIndividualsAxioms += visitor.nDifferentIndividualsAxioms;
		nSameIndividualAxioms += visitor.nSameIndividualAxioms;
		
		nClassAssertionAxioms += visitor.nClassAssertionAxioms;
		nObjectPropertyAssertionAxioms += visitor.nObjectPropertyAssertionAxioms;
		nDataPropertyAssertionAxioms += visitor.nDataPropertyAssertionAxioms;
		
		nDisjointUnionAxioms += visitor.nDisjointUnionAxioms;
		nHasKeyAxioms += visitor.nHasKeyAxioms;
		nIrreflexiveObjectPropertyAxioms += visitor.nIrreflexiveObjectPropertyAxioms;
		nNegativeDataPropertyAssertionAxioms += visitor.nNegativeDataPropertyAssertionAxioms;
		nNegativeObjectPropertyAssertionAxioms += visitor.nNegativeObjectPropertyAssertionAxioms;
		nSubPropertyChainOfAxioms += visitor.nSubPropertyChainOfAxioms;
		nSWRLRules += visitor.nSWRLRules;
	}
	
	/** No internal array use due to performance considerations
	 * @param vector
	 */
	public void append(OntFeatureVector vector) {		
		nClasses += vector.nClasses;
		nObjectProperties += vector.nObjectProperties;
		nDataProperties += vector.nDataProperties;
		nDistinctClasses += vector.nDistinctClasses;
		nDistinctObjectProperties += vector.nDistinctObjectProperties;
		nDistinctDataProperties += vector.nDistinctDataProperties;
		
		nObjectSomeValuesFrom += vector.nObjectSomeValuesFrom;
		nObjectAllValuesFrom += vector.nObjectAllValuesFrom;
		nObjectMinCardinality += vector.nObjectMinCardinality;
		nObjectMaxCardinality += vector.nObjectMaxCardinality;
		nObjectExactCardinality += vector.nObjectExactCardinality;	
		nObjectIntersectionOf += vector.nObjectIntersectionOf;
		nObjectUnionOf += vector.nObjectUnionOf;
		nDataExactCardinality += vector.nDataExactCardinality;
		nDataAllValuesFrom += vector.nDataAllValuesFrom;
		nDataSomeValuesFrom += vector.nDataSomeValuesFrom;
		nDataMinCardinality += vector.nDataMinCardinality;
		nDataMaxCardinality += vector.nDataMaxCardinality;
		
		nSubClassOfAxioms += vector.nSubClassOfAxioms;
		nDisjointClassesAxioms += vector.nDisjointClassesAxioms;
		nEquivalentClassesAxioms += vector.nEquivalentClassesAxioms;
		nAsymmetricObjectPropertyAxioms += vector.nAsymmetricObjectPropertyAxioms;
		nSymmetricObjectPropertyAxioms += vector.nSymmetricObjectPropertyAxioms;
		nInverseObjectPropertiesAxioms += vector.nInverseObjectPropertiesAxioms;
		nInverseFunctionalObjectPropertyAxioms += vector.nInverseFunctionalObjectPropertyAxioms;
		nFunctionalObjectPropertyAxioms += vector.nFunctionalObjectPropertyAxioms;
		nTransitiveObjectPropertyAxioms += vector.nTransitiveObjectPropertyAxioms;
		nFunctionalDataPropertyAxioms += vector.nFunctionalDataPropertyAxioms;
		nDataPropertyRangeAxioms += vector.nDataPropertyRangeAxioms;
		nDataPropertyDomainAxioms += vector.nDataPropertyDomainAxioms;
		nObjectPropertyDomainAxioms += vector.nObjectPropertyDomainAxioms;
		nObjectPropertyRangeAxioms += vector.nObjectPropertyRangeAxioms;
		nSubObjectPropertyOfAxioms += vector.nSubObjectPropertyOfAxioms;
		nSubDataPropertyOfAxioms += vector.nSubDataPropertyOfAxioms;
		nReflexiveObjectPropertyAxioms += vector.nReflexiveObjectPropertyAxioms;
		nEquivalentObjectPropertiesAxioms += vector.nEquivalentObjectPropertiesAxioms;
		nEquivalentDataPropertiesAxioms += vector.nEquivalentDataPropertiesAxioms;
		nDisjointObjectPropertiesAxioms += vector.nDisjointObjectPropertiesAxioms;
		nDisjointDataPropertiesAxioms += vector.nDisjointDataPropertiesAxioms;
		
		nDifferentIndividualsAxioms += vector.nDifferentIndividualsAxioms;
		nSameIndividualAxioms += vector.nSameIndividualAxioms;
		
		nClassAssertionAxioms += vector.nClassAssertionAxioms;
		nObjectPropertyAssertionAxioms += vector.nObjectPropertyAssertionAxioms;
		nDataPropertyAssertionAxioms += vector.nDataPropertyAssertionAxioms;
		
		nDisjointUnionAxioms += vector.nDisjointUnionAxioms;
		nHasKeyAxioms += vector.nHasKeyAxioms;
		nIrreflexiveObjectPropertyAxioms += vector.nIrreflexiveObjectPropertyAxioms;
		nNegativeDataPropertyAssertionAxioms += vector.nNegativeDataPropertyAssertionAxioms;
		nNegativeObjectPropertyAssertionAxioms += vector.nNegativeObjectPropertyAssertionAxioms;
		nSubPropertyChainOfAxioms += vector.nSubPropertyChainOfAxioms;
		nSWRLRules += vector.nSWRLRules;
		
//		nDependents += vector.nDependents;
		parsingDepth += vector.parsingDepth;
//		signatureSize += vector.signatureSize;
	}
	
	public void append(Integer[] vector) {
		nClasses += vector[0];
		nObjectProperties += vector[1];
		nDataProperties += vector[2];
		nDistinctClasses += vector[3];
		nDistinctObjectProperties += vector[4];
		nDistinctDataProperties += vector[5];
		
		nObjectSomeValuesFrom += vector[6];
		nObjectAllValuesFrom += vector[7];
		nObjectMinCardinality += vector[8];
		nObjectMaxCardinality += vector[9];
		nObjectExactCardinality += vector[10];	
		nObjectIntersectionOf += vector[11];
		nObjectUnionOf += vector[12];
		nDataExactCardinality += vector[13];
		nDataAllValuesFrom += vector[14];
		nDataSomeValuesFrom += vector[15];
		nDataMinCardinality += vector[16];
		nDataMaxCardinality += vector[17];
		
		nSubClassOfAxioms += vector[18];
		nDisjointClassesAxioms += vector[19];
		nEquivalentClassesAxioms += vector[20];
		nAsymmetricObjectPropertyAxioms += vector[21];
		nSymmetricObjectPropertyAxioms += vector[22];
		nInverseObjectPropertiesAxioms += vector[23];
		nInverseFunctionalObjectPropertyAxioms += vector[24];
		nFunctionalObjectPropertyAxioms += vector[25];
		nTransitiveObjectPropertyAxioms += vector[26];
		nFunctionalDataPropertyAxioms += vector[27];
		nDataPropertyRangeAxioms += vector[28];
		nDataPropertyDomainAxioms += vector[29];
		nObjectPropertyDomainAxioms += vector[30];
		nObjectPropertyRangeAxioms += vector[31];
		nSubObjectPropertyOfAxioms += vector[32];
		nSubDataPropertyOfAxioms += vector[33];
		nReflexiveObjectPropertyAxioms += vector[34];
		nEquivalentObjectPropertiesAxioms += vector[35];
		nEquivalentDataPropertiesAxioms += vector[36];
		nDisjointObjectPropertiesAxioms += vector[37];
		nDisjointDataPropertiesAxioms += vector[38];
		
		nDifferentIndividualsAxioms += vector[39];
		nSameIndividualAxioms += vector[40];
		
		nClassAssertionAxioms += vector[41];
		nObjectPropertyAssertionAxioms += vector[42];
		nDataPropertyAssertionAxioms += vector[43];
		
		nDisjointUnionAxioms += vector[44];
		nHasKeyAxioms += vector[45];
		nIrreflexiveObjectPropertyAxioms += vector[46];
		nNegativeDataPropertyAssertionAxioms += vector[47];
		nNegativeObjectPropertyAssertionAxioms += vector[48];
		nSubPropertyChainOfAxioms += vector[49];
		nSWRLRules += vector[50];
		
		parsingDepth += vector[51];
//		signatureSize += vector[52];
	}
	
	public void append(Double[] vector) {
		nClasses += vector[0];
		nObjectProperties += vector[1];
		nDataProperties += vector[2];
		nDistinctClasses += vector[3];
		nDistinctObjectProperties += vector[4];
		nDistinctDataProperties += vector[5];
		
		nObjectSomeValuesFrom += vector[6];
		nObjectAllValuesFrom += vector[7];
		nObjectMinCardinality += vector[8];
		nObjectMaxCardinality += vector[9];
		nObjectExactCardinality += vector[10];	
		nObjectIntersectionOf += vector[11];
		nObjectUnionOf += vector[12];
		nDataExactCardinality += vector[13];
		nDataAllValuesFrom += vector[14];
		nDataSomeValuesFrom += vector[15];
		nDataMinCardinality += vector[16];
		nDataMaxCardinality += vector[17];
		
		nSubClassOfAxioms += vector[18];
		nDisjointClassesAxioms += vector[19];
		nEquivalentClassesAxioms += vector[20];
		nAsymmetricObjectPropertyAxioms += vector[21];
		nSymmetricObjectPropertyAxioms += vector[22];
		nInverseObjectPropertiesAxioms += vector[23];
		nInverseFunctionalObjectPropertyAxioms += vector[24];
		nFunctionalObjectPropertyAxioms += vector[25];
		nTransitiveObjectPropertyAxioms += vector[26];
		nFunctionalDataPropertyAxioms += vector[27];
		nDataPropertyRangeAxioms += vector[28];
		nDataPropertyDomainAxioms += vector[29];
		nObjectPropertyDomainAxioms += vector[30];
		nObjectPropertyRangeAxioms += vector[31];
		nSubObjectPropertyOfAxioms += vector[32];
		nSubDataPropertyOfAxioms += vector[33];
		nReflexiveObjectPropertyAxioms += vector[34];
		nEquivalentObjectPropertiesAxioms += vector[35];
		nEquivalentDataPropertiesAxioms += vector[36];
		nDisjointObjectPropertiesAxioms += vector[37];
		nDisjointDataPropertiesAxioms += vector[38];

		nDifferentIndividualsAxioms += vector[39];
		nSameIndividualAxioms += vector[40];
		
		nClassAssertionAxioms += vector[41];
		nObjectPropertyAssertionAxioms += vector[42];
		nDataPropertyAssertionAxioms += vector[43];
		
		nDisjointUnionAxioms += vector[44];
		nHasKeyAxioms += vector[45];
		nIrreflexiveObjectPropertyAxioms += vector[46];
		nNegativeDataPropertyAssertionAxioms += vector[47];
		nNegativeObjectPropertyAssertionAxioms += vector[48];
		nSubPropertyChainOfAxioms += vector[49];
		nSWRLRules += vector[50];
		
		parsingDepth += vector[51];
//		signatureSize += vector[52];
	}
	
}
