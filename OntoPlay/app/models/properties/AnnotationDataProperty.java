package models.properties;

import org.semanticweb.owl.model.OWLDataType;

import models.ontologyModel.OntoProperty;

public class AnnotationDataProperty extends OwlDatatypeProperty{

	public AnnotationDataProperty(String namespace, String localName,String label) {
		this(namespace, localName, "http://www.w3.org/2001/XMLSchema#string",label);
	}
	

	public AnnotationDataProperty(String namespace, String localName, String datatype,String label) {
		super(namespace, localName, datatype,label);
		
	}

	
}