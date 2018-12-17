import java.util.ArrayList;

public class Driver {
	public static final int EPOCHS = 5;
	public static final int INPUTS = 784;
	public static final int OUTPUTS = 10;
	// computed from mean INPUTS and OUTPUTS
	public static final int HIDDEN = 397;
	public static final int NUMPICTURES = 60000;
	public static final double e = 2.71828;
	public static final double LR = 0.2;
	
	public static void main(String[] args) {
		FileHandler fh = new FileHandler();
		fh.parseCSV(true);
		System.out.println("Parsed CSV Training");
		fh.parseCSV(false);
		System.out.println("Parsed CSV Test");
		ArrayList<Data> testData = fh.getTestData();
		
		ArrayList<Data> inputs = fh.getData();
		Network n = new Network(inputs, testData);
		n.train();

	}
}
