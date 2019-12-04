package dev.elysion.fwa.converter;

import org.apache.commons.codec.binary.Base64;

public class ImageDataConverter {

	private ImageDataConverter() {
		//Constructor to hide the implicit public constructor
	}

	//this converter was tested for: .jpeg, .png, .gif - does not work with .bmp and presumably other formats
	public static String convert(byte[] data) {
		return Base64.encodeBase64String(data);
	}

	public static byte[] convert(String data) {
		return Base64.decodeBase64(getData(data));
	}

	public static String getDataType(String data) {
		return data.substring(0, data.indexOf(',') + 1);
	}

	public static String getContentType(String dataUrl) {
		return dataUrl.substring(dataUrl.indexOf(":") + 1, dataUrl.indexOf(";"));
	}

	public static String getExtenstion(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'), fileName.length())
					   .toLowerCase();
	}

	protected static String getData(String data) {
		return data.substring(data.indexOf(',') + 1, data.length());
	}
}
