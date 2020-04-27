import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public class Driver {
	public static final int EPOCHS = 3;
	public static final int INPUTS = 784;
	public static final int OUTPUTS = 10;
	// computed from mean INPUTS and OUTPUTS
	public static final int HIDDEN = 397;
	public static final int NUMPICTURES = 6000;
	public static final double LR = 0.2;
	
	public static void main(String[] args) {


		Config config = new Config(args[0]);

		if(args.length == 2) {
			if(args[1].equals("-pull")) {
				HttpUtils.pull(Integer.parseInt(config.get("modelId")), config.get("modelFilepath"), config);
				FileHandler.updateConfig(config);
			}
			return;
		}


		FileHandler fh = new FileHandler();
		fh.parseCSV(true);
		System.out.println("Parsed CSV Training");
		fh.parseCSV(false);
		System.out.println("Parsed CSV Test");
		ArrayList<Data> testData = fh.getTestData();

		ArrayList<Data> inputs = fh.getData();

		SimpleMatrix hiddenWeights = FileHandler.getWeights("hiddenWeights");
		SimpleMatrix outputWeights = FileHandler.getWeights("outputWeights");

		 Network n = new Network(inputs, testData, hiddenWeights, outputWeights);
		// Network n = new Network(inputs, testData);
		n.train();


	}
}
