package dev.elysion.fwa.entity;

import dev.elysion.fwa.enumeration.DocumentType;

import javax.persistence.*;

@Entity
@Table(name = "document")
@NamedQueries({@NamedQuery(name = DocumentEntity.QUERY_READ_ALL, query = "select d from DocumentEntity d")})
public class DocumentEntity {

	public static final String QUERY_READ_ALL = "documents.readAll";

	private int id;
	private byte[] data;
	private String filename;
	private DocumentType documentType;
	private String dataType;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "document_data")
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Basic
	@Column(name = "filename", length = 100)
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "document_type", length = 20)
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	@Basic
	@Column(name = "data_type", length = 50)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
