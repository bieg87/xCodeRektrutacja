package pl.xCode.rekrutacja;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class Currency {

	public String currency;
	public boolean currencyValidation() {//funkcja sprawdzajaca poprawnosc nazwy waluty
		RestTemplate restTemplate=new RestTemplate();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
		ResponseEntity<String> rateEntity=restTemplate.exchange("http://api.nbp.pl/api/exchangerates/tables/A?format=json", HttpMethod.GET, new HttpEntity<Object>(headers),String.class);
		String ratesJSON=rateEntity.getBody();
		ratesJSON=ratesJSON.substring(1,ratesJSON.length()-1);
		DocumentContext rateJsonObject=JsonPath.parse(ratesJSON);
		List<String> rateCodes=rateJsonObject.read("$['rates'][*]['code']");
		for(String s : rateCodes)
			if(s.equals(currency))
				return true;
		return false;
	}
}
