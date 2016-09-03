package ontoplay.controllers.webservices;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;

import com.google.gson.GsonBuilder;

import controllers.OntologyController;
import controllers.configuration.OntologyHelper;
import models.ClassCondition;
import models.ConditionDeserializer;
import models.angular.IndividualDTO;
import models.angular.update.IndividualUpdateModel;
import models.ontologyModel.OntoClass;
import models.ontologyModel.OwlIndividual;
import models.ontologyReading.OntologyReader;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

public class Individuals extends OntologyController {

	public static Result getIndividualsByClassName(String className) {
		try {
			OntoClass owlClass = getOwlClass(className);
			List<OwlIndividual> individuals = ontologyReader.getIndividuals(owlClass);

			List<IndividualDTO> individualDTOs = new ArrayList<IndividualDTO>();

			for (OwlIndividual owlIndividual : individuals) {
				individualDTOs.add(new IndividualDTO(owlIndividual));
			}
			return ok(new GsonBuilder().create().toJson(individualDTOs));
		} catch (Exception e) {
			System.out.println(e.toString());
			return badRequest();
		}
	}

	public static Result addIndividual() {
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String conditionJson = dynamicForm.get("conditionJson");
		String individualName = dynamicForm.get("name");

		try {
			ClassCondition condition = ConditionDeserializer.deserializeCondition(ontologyReader, conditionJson);
			OWLOntology generatedOntology = ontologyGenerator
					.convertToOwlIndividualOntology(OntologyHelper.nameSpace + individualName, condition);

			try {
				OwlIndividual individual = ontologyReader.getIndividual(OntologyHelper.nameSpace + individualName);
				if (individual != null)
					return ok("Indvidual name is already used");
			} catch (Exception e) {
			}

			if (generatedOntology == null)
				return ok("Ontology is null");

			OntologyHelper.checkOntology(generatedOntology);
			OntologyReader checkOntologyReader = OntologyHelper.checkOwlReader();
			OntoClass owlClass = checkOntologyReader.getOwlClass(OntologyHelper.nameSpace + "Offer");
			OntologyHelper.saveOntology(generatedOntology);
			// Fix nested individuals

			return ok("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return ok("Error");
		}
	}

	public static Result updateIndividual(String individualName) {
	//	try {
			OwlIndividual individual = OntologyReader.getGlobalInstance()
					.getIndividual(OntologyHelper.nameSpace + individualName);
			if (individual == null || individual.getIndividual() == null) {
				return ok("Individual Not Found");
			}
			IndividualUpdateModel ind = new IndividualUpdateModel(individual.getIndividual());

			return ok(new GsonBuilder().create().toJson(ind.getUpdateIndividual()));
//		} catch (Exception e) {
	//		System.out.println("Error in getting datat to update " + e.toString());
		//	return ok("Error");
//		}
	}

}