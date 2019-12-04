package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.Document;
import dev.elysion.fwa.services.DocumentService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/documents")
@RequestScoped
public class DocumentsResource {
	private DocumentService service;

	protected DocumentsResource() {
		//Benötigt für Proxy
	}

	@Inject
	public DocumentsResource(DocumentService service) {
		this.service = service;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocuments() {

		//public documents resource returns all INFO documents
		List<String> documentTypes = new ArrayList<>();
		documentTypes.add("INFO");
		List<Document> payload = service.readFiltered(documentTypes);

		GenericEntity<List<Document>> genericEntity = new GenericEntity<List<Document>>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@GET
	@Path("/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentById(@PathParam("documentId") int documentId) {
		Document payload = service.getDocumentById(documentId);
		GenericEntity<Document> genericEntity = new GenericEntity<Document>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	// TODO: POST secured ADMIN ROLE
}
