import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.IOUtils;


public class FileHandler {
	private String fileName;
	private File file;
	private int height;
	private int width;
	private ArrayList<Data> data;
	private ArrayList<Data> testData;
	
	public FileHandler() {
		this.fileName = "data1.csv";
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

	public static void writeStringToFile(String input, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter((filename)));
			bw.write(input);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> parseConfigFile(String filepath) {
		Map<String, String> config = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filepath)))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] mapping = line.split("=");
				config.put(mapping[0], mapping[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	public static SimpleMatrix getWeights(String key) {
		try {
			String content = new String(Files.readAllBytes(Paths.get("model.json")));
			//System.out.println(content);
			JSONObject model = new JSONObject(content);
			//System.out.println(model.toString());
			JSONArray array;
			if(key.equals("hiddenWeights"))
				array = model.getJSONArray(key).getJSONArray(0);
			else
				array = model.getJSONArray(key);
			System.out.println("****");
			System.out.println(array);
			SimpleMatrix matrix = new SimpleMatrix(array.length(), ((JSONArray) array.get(0)).length());
			for (int i = 0; i < array.length(); i++) {
				JSONArray row = (JSONArray) array.get(i);
				for (int j = 0; j < row.length(); j++) {
					matrix.set(i, j, Double.parseDouble(row.get(j).toString()));
				}
			}
			MatrixUtils.printMatrix(matrix);
			return matrix;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void updateConfig(Config config) {
		try(BufferedWriter bw = new BufferedWriter((new FileWriter(new File("config.conf"))))) {
			for(String key : config.getMap().keySet()) {
				bw.write(key + "=" + config.get(key) + "\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
