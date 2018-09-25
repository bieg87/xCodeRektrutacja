package pl.xCode.rekrutacja;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.zip.DataFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sort {//klasa odpowiedzialna za sortowanie liczby
	private String[] numbers;
	private String order;
	private Sorted afterSort;
	private int[] numbersInt;
	public String status;
	private void stringToInt() throws NumberFormatException{//zmiana wartosci String na liczby
		numbersInt=new int[numbers.length];
		for(int i=0;i<numbers.length;i++) {
			numbersInt[i]=Integer.parseInt(numbers[i]);
		}
	}
	private void intToString() throws NumberFormatException{//zmiana wartości liczbowych na String
		afterSort.numbers=new String[numbersInt.length];
		for(int i=0;i<numbersInt.length;i++) {
			numbers[i]=Integer.toString(numbersInt[i]);
		}
	}
	private void quicksort(int a, int b) throws ArrayIndexOutOfBoundsException{//sortowanie szybkie
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
	
	public Sort(String[] numbers, String order)//konstruktor
	{
		setNumbers(numbers);
		setOrder(order);
		afterSort=new Sorted();
		status="OK";
	}
	public String sort()//metoda sortujaca
	{
		Logger log=LoggerFactory.getLogger(Sort.class);
		try{
			stringToInt();
		if((this.order.equals("ASC") || this.order.equals("DESC")) == false) throw new DataFormatException();//status="BAD_REQUEST";
		quicksort(0,numbersInt.length-1);
		intToString();
		afterSort.numbers=this.numbers;		
		ObjectMapper mapper=new ObjectMapper();//mapowanie i zwrocenie JSON'a
	    return mapper.writeValueAsString(afterSort.returnNumbers());
	    } 
		catch (JsonProcessingException e) {
	        log.error("JsonProcessingException while converting Entity into string", e);
	        status="JSON parse error";
	        return "{\"numbers\":[]}";
	    }
		catch (NumberFormatException e) {
			log.error("NumberFormatException while convertion  String and int", e);
			status="BAD_REQUEST";
			return "{\"numbers\":[]}";
		}
		catch (DataFormatException e) {
			log.error("Wrong order format", e);
			status="BAD_REQUEST";
			return "{\"numbers\":[]}";
		}
		catch( ArrayIndexOutOfBoundsException e) {
			log.error("Sorting array index of out bound", e);
			status="INTERNAL_SERVER_ERROR";
			return "{\"numbers\":[]}";
		}
		catch( Exception e) {
			log.error("Not determined error", e);
			status="INTERNAL_SERVER_ERROR";
			return "{\"numbers\":[]}";
		}
	}
}
