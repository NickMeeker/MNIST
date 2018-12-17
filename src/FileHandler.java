import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;


public class FileHandler {
	private String fileName;
	private File file;
	private int height;
	private int width;
	private ArrayList<Data> data;
	private ArrayList<Data> testData;
	
	public FileHandler() {
		this.fileName = "mnist_train.csv";
		this.file = new File(fileName);
		this.height = 28;
		this.width = 28;
		this.data = new ArrayList<Data>();
		this.testData = new ArrayList<Data>();
	}
	
	public void parseCSV(boolean trainingMode) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    int c = 0;
		    System.out.print("Parsing csv... \n");
		    while ((line = br.readLine()) != null) {
		    	// get label from first integer of string
		    	int label = Integer.parseInt(line.substring(0, 1));
		    	SimpleMatrix inputs = new SimpleMatrix(Driver.INPUTS, 1);
		    	// remove label from string
		    	line = line.substring(2);
		    	
		    	// now collect inputs by scanning over csv
		    	Scanner scanInts = new Scanner(line);
		    	scanInts.useDelimiter(",");
		    	
		    	for(int i = 0; i < Driver.INPUTS; i++) {
		    		// add them to the matrix
		    		inputs.set(i, (double)scanInts.nextInt());
		    	}
		    
		    	
		    	
		    	if(trainingMode) {
			    	data.add(new Data(inputs, label));
			    	data.get(c).cleanData();
		    	} else {
		    		testData.add(new Data(inputs, label));
			    	testData.get(c).cleanData();
		    	}
		    	
		    	
		    	c++;
		    }
		    System.out.println(c);
		} catch(IOException e) {
			System.out.println(e);
			System.exit(-1);
		}
	}
	
	public ArrayList<Data> getData(){
		return this.data;
	}
	
	public ArrayList<Data> getTestData(){
		return this.testData;
	}
}
