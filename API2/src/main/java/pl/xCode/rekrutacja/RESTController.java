package pl.xCode.rekrutacja;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class RESTController {
	
		@RequestMapping(value="/status/ping", method=RequestMethod.GET)
		public String pong() {
			return new Ping().getPing();
		}
		@CrossOrigin
		@RequestMapping(value="/numbers/sort-command", method=RequestMethod.POST,  headers = "Accept=application/json")
		@ResponseBody
		//public ResponseEntity<String> sortCommand(@RequestParam(name="numbers") String[] numbers, @RequestParam(name="order") String order) {
		public ResponseEntity<String> sortCommand(@RequestBody SortRequest sortRequest) {
			Sort toSort=new Sort(sortRequest.numbers,sortRequest.order);
			String result=null;
			HttpStatus httpStatus = null;
			if(sortRequest.numbers.length==0 || sortRequest.numbers[0].equals("null")) {
				result="";
				toSort.status="NO_CONTENT";
			}
			else
			{
				result=toSort.sort();
			}
			switch(toSort.status) {
				case "OK": {
					httpStatus=HttpStatus.OK;
					break;
				}
				case "BAD_REQUEST":{
					httpStatus=HttpStatus.BAD_REQUEST;
					break;
					}
				case "NO_CONTENT":{
					httpStatus=HttpStatus.NO_CONTENT;
					break;
					}
			}
			return new ResponseEntity<>(result, httpStatus);
		}
		@RequestMapping(value="/currencies/get-current-currency-value-command", method=RequestMethod.POST,  headers = "Accept=application/json")
		public Rate currencyRate(@RequestBody Currency currency) {
			RestTemplate restTemplate=new RestTemplate();
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
	        headers.add("Content-Type", "application/json");
			ResponseEntity<String> rateEntity=restTemplate.exchange("http://api.nbp.pl/api/exchangerates/rates/a/"+currency.currency, HttpMethod.GET, new HttpEntity<Object>(headers),String.class);
			String rateString=rateEntity.getBody();
			DocumentContext rateJsonObject=JsonPath.parse(rateString);
			Rate rate=new Rate();
			rate.currentRate=Double.toString(rateJsonObject.read("$.rates[0].mid"));
			return rate;
		}
}
