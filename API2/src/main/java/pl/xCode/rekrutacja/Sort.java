package pl.xCode.rekrutacja;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sort {
	private String[] numbers;
	private String order;
	private Sorted afterSort;
	private int[] numbersInt;
	public String status;
	private void stringToInt() {
		numbersInt=new int[numbers.length];
		for(int i=0;i<numbers.length;i++) {
			numbersInt[i]=Integer.parseInt(numbers[i]);
		}
	}
	private void intToString() {
		afterSort.numbers=new String[numbersInt.length];
		for(int i=0;i<numbersInt.length;i++) {
			numbers[i]=Integer.toString(numbersInt[i]);
		}
	}
	private void quicksort(int a, int b) {
		int x=numbersInt[(a+b)/2];
		int i=a;
		int j=b;
		while(i<=j){
			if(order.equals("ASC")){
		        while(numbersInt[i]<x)i++;
		        while(numbersInt[j]>x)j--;
			}
			else if(order.equals("DESC")){
				for(;numbersInt[i]>x;i++);
				for(;numbersInt[j]<x;j--);
			}
			if(i<=j){
				int t=numbersInt[i];
				numbersInt[i]=numbersInt[j];
				numbersInt[j]=t;
				i++;
				j--;
			}
		}
		if(a<j)quicksort(a, j);
		if(b>i)quicksort(i, b);
	}
		
	public void setOrder(String order) {
		this.order = order;
	}
	public String[] getNumbers() {
		return numbers;
	}
	public void setNumbers(String[] numbers) {
		this.numbers = numbers;
	}
	public String getOrder() {
		return order;
	}
	
	public Sort(String[] numbers, String order)
	{
		setNumbers(numbers);
		setOrder(order);
		afterSort=new Sorted();
		status=null;
	}
	public String sort()
	{
		Logger log=LoggerFactory.getLogger(Sort.class);
		try{
			stringToInt();
		}
		catch (NumberFormatException e) {
			log.error("NumberFormatException while convertion int to String", e);
			status="BAD_REQUEST";
		}
		if(!(order.equals("ASC") || order.equals("DESC")))status="BAD_REQUEST";
		quicksort(0,numbersInt.length-1);
		if(status == null)status="OK";
		intToString();
		afterSort.numbers=this.numbers;		
		try {
			ObjectMapper mapper=new ObjectMapper();
	        return mapper.writeValueAsString(afterSort.passNumbers());
	    } catch (JsonProcessingException e) {
	        log.error("JsonProcessingException while converting Entity into string", e);
	        status="Internal Error";
	    }
	    return null;
	}
}
