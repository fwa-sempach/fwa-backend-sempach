package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.DocumentDao;
import dev.elysion.fwa.dto.Document;
import dev.elysion.fwa.enumeration.DocumentType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class DocumentService {
	private DocumentDao documentDao;

	protected DocumentService() {
		//Used for proxy
	}

	@Inject
	public DocumentService(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public List<Document> readAll() {
		return documentDao.readAll()
						  .stream()
						  .map(Converter::convert)
						  .collect(Collectors.toList());
	}

	public List<Document> readFiltered(List<String> documentTypes) {
		//Parse DocumentType from String to enum for filtering
		List<DocumentType> documentTypesEnum = new ArrayList<>();
		for (String documentType : documentTypes) {
			for (DocumentType documentTypeEnum : DocumentType.values()) {
				if (documentTypeEnum.name()
									.equals(documentType)) {
					documentTypesEnum.add(DocumentType.valueOf(documentType));
					break;
				}
			}
		}

		return documentDao.readFiltered(documentTypesEnum)
						  .stream()
						  .map(Converter::convert)
						  .collect(Collectors.toList());
	}

	public Document getDocumentById(int id) {
		return Converter.convert(documentDao.readById(id));
	}

	public Document saveDocument(Document document) {
		return Converter.convert(documentDao.merge(Converter.convert(document)));
	}

}
