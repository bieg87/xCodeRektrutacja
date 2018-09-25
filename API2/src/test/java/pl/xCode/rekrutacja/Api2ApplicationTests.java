package pl.xCode.rekrutacja;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Api2ApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test//Test wadomosci GET "ping"
	public void pingTest() {
		String body = this.restTemplate.getForObject("/status/ping", String.class);
		assertThat(body).isEqualTo("pong");
	}
	//Test sortowania losowych liczb
	@Test
	public void sortTest() {
		Random rand = new Random(); 
		//losowanie liczb
		int length=rand.nextInt(1000);
		int[] testNumbers=new int[length];
		for(int i=0;i<length;i++) {
			testNumbers[i]=rand.nextInt(1000);
		}
		SortRequest sortRequest=new SortRequest();
		sortRequest.numbers=new String[length];
		int j=0;
		for(int n:testNumbers) {
			sortRequest.numbers[j]=Integer.toString(n);
			j++;
		}
		int orderInt=rand.nextInt(2);
		if(orderInt==0)
			sortRequest.order="ASC";
		else
			sortRequest.order="DESC";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<SortRequest> request = new HttpEntity<SortRequest>(sortRequest, headers);
		String sortResult=this.restTemplate.postForObject("/numbers/sort-command", request, String.class);
		DocumentContext sortResultObject=JsonPath.parse(sortResult);
		List<String> stringList =sortResultObject.read("$['numbers']");
		List<Integer> intList=new ArrayList<Integer>();
		for(String s : stringList) intList.add(Integer.valueOf(s));
		//sprawdzenie sortowania:
		if(orderInt==0) {
			int i=1;
			for(;i<length;i++) {
				if(intList.get(i-1)>intList.get(i))
					fail("not sorted");
			}
			assertTrue(i==length);
		}
		else {
			int k=1;
			for(;k<length;k++) {
				if(intList.get(k-1)<intList.get(k))
					fail("not sorted");
			}
			assertTrue(k==length);
		}
	}
	//test pobierania waluty z NBP
	@Test
	public void currencyTest() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        Currency currency=new Currency();
        currency.currency="EUR";
		ResponseEntity<String> rateEntity=restTemplate.exchange("http://api.nbp.pl/api/exchangerates/rates/a/"+currency.currency, HttpMethod.GET, new HttpEntity<Object>(headers),String.class);
		String rateString=rateEntity.getBody();
		DocumentContext rateJsonObject=JsonPath.parse(rateString);
		Rate rate=new Rate();
		rate.currentRate=Double.toString(rateJsonObject.read("$.rates[0].mid"));
		assertTrue(Double.parseDouble(rate.currentRate)>0);
		assertNotNull(rate.currentRate);
	}
	
}
