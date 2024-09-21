package com.Greer.StockList;

import com.Greer.StockList.controller.APIController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class StockListApplicationTests {


	@Test
	void testThatJSONParsingParsesCorrectly() throws IOException, URISyntaxException {

		// Read JSON data from the file
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("mockData.txt");

		if (resource == null) {
			throw new IllegalArgumentException("File not found!");
		}

		// Convert URL to Path
		String filePath = Paths.get(resource.toURI()).toString();

		// Read the file content
		String mockJsonData = new String(Files.readAllBytes(Paths.get(filePath)));

		// Mock the HttpResponse object
		HttpResponse<String> mockResponse = mock(HttpResponse.class);
		when(mockResponse.body()).thenReturn(mockJsonData);

		APIController apiController = new APIController();

		List<Double> result = apiController.parseAlphaVantageResponse(mockResponse,4);
		System.out.println(result);
	}

}
