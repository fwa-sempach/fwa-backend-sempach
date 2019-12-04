package dev.elysion.fwa.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImageDataConverterTest {

	@Test
	public void getDataType_success() {
		String imageData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAB4AAAA" + "" + "" + "" + "" + "==";
		String dataType = ImageDataConverter.getDataType(imageData);
		assertEquals("data:image/png;base64,", dataType);
	}

	@Test
	public void getData_success() {
		String imageData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAB4AAAA";
		String data = ImageDataConverter.getData(imageData);
		assertEquals("iVBORw0KGgoAAAANSUhEUgAAB4AAAA", data);
	}
}
