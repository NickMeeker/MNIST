import java.util.ArrayList;
import java.util.Random;

import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;

public class Network {
	ArrayList<Data> inputs;
	ArrayList<Data> testData;
	SimpleMatrix hiddenWeights;
	SimpleMatrix outputs;
	SimpleMatrix outputWeights;

	public Network(ArrayList<Data> inputs, ArrayList<Data> testData) {
		this.inputs = inputs;
		this.testData = testData;
		this.hiddenWeights = new SimpleMatrix(Driver.HIDDEN, Driver.INPUTS);
		this.outputWeights = new SimpleMatrix(Driver.OUTPUTS, Driver.HIDDEN);
		this.outputs = new SimpleMatrix(Driver.OUTPUTS, 1);
		
		// need to initialize weights to random values 0-1
		Random rand = new Random();
		for(int i = 0; i < this.hiddenWeights.numRows(); i++) {
			for(int j = 0; j < this.hiddenWeights.numCols(); j++) {
				this.hiddenWeights.set(i, j, rand.nextGaussian()*-0.5);
			}
		}
		
		for(int i = 0; i < this.outputWeights.numRows(); i++) {
			for(int j = 0; j < this.outputWeights.numCols(); j++) {
				this.outputWeights.set(i, j, rand.nextGaussian()*-0.5);
			}
		}
	}

	public Network(ArrayList<Data> inputs, ArrayList<Data> testData, SimpleMatrix hiddenWeights, SimpleMatrix outputWeights) {
		this.inputs = inputs;
		this.testData = testData;
		this.outputs = new SimpleMatrix(Driver.OUTPUTS, 1);
		this.hiddenWeights = hiddenWeights;
		this.outputWeights = outputWeights;
	}
	
	public double sigmoid(double x) {
		return 1.0 / (double)(1 + Math.pow(Math.E, -x));
	}
	
	public SimpleMatrix calcDelta(SimpleMatrix E, SimpleMatrix O, SimpleMatrix hy) {
		SimpleMatrix negO = O;
		negO = negO.scale(-1);
		SimpleMatrix output = O;
		
		
		output = output.elementMult(negO.plus(1));
		output = output.elementMult(E);
		output = output.scale(Driver.LR);
		output = output.mult(hy.transpose());

		return output;
	}
	
	public SimpleMatrix feedforward(Data input, boolean training) {
		// Step 1: Calculate hidden X:
		SimpleMatrix hx = hiddenWeights.mult(input.getInputs());
		
		// Step 2: Calculate sigmoids
		SimpleMatrix hy = new SimpleMatrix(Driver.HIDDEN, 1);
		for(int j = 0; j < hy.numRows(); j++) {
			hy.set(j, 0, sigmoid(hx.get(j, 0)));
		}
		
		// Step 3: Calculate output x:
		SimpleMatrix ox = outputWeights.mult(hy);
		
		// Step 4: Calculate output sigmoids:
		SimpleMatrix oy = new SimpleMatrix(Driver.OUTPUTS, 1);
		for(int j = 0; j < oy.numRows(); j++) {
			oy.set(j, 0, sigmoid(ox.get(j, 0)));
		}
		if(!training)
			return oy;
		
		backpropogate(input, oy, hy);
		return null;
	}
	
	public void backpropogate(Data input, SimpleMatrix oy, SimpleMatrix hy) {
		// Step 5: Evaluate:
		SimpleMatrix E = input.getTarget().minus(oy);

		// Step 6: Calculate hidden layer errors and adjust:
		SimpleMatrix hE = outputWeights.transpose().mult(E);
		SimpleMatrix dho = calcDelta(E, oy, hy);
		SimpleMatrix dih = calcDelta(hE, hy, input.getInputs());
		outputWeights = outputWeights.plus(dho);
		hiddenWeights = hiddenWeights.plus(dih);
	}
	public int determineNum(SimpleMatrix oy) {
		int maxIndex = 0;
		double max = -100000000;
		for(int i = 0; i < oy.numRows(); i++) {
			if(oy.get(i, 0) > max) {
				max = oy.get(i, 0);
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	public void train() {
		int trainCounter = 0;
		
		
		while(trainCounter < Driver.EPOCHS) {
			System.out.println("Epoch: " + (trainCounter+1));
			for(int i = 0; i < Driver.NUMPICTURES; i++) {
				feedforward(inputs.get(i), true);
			}
			test();
			trainCounter++;
		}
		
		StringBuilder hiddenWeightsStr = new StringBuilder("[");
		StringBuilder outputWeightsStr = new StringBuilder("[");

		for(int i = 0; i < hiddenWeights.numRows(); i++) {
			for(int j = 0; j < hiddenWeights.numCols(); j++) {
				hiddenWeightsStr.append(hiddenWeights.get(i, j) + ",");
				if(j == this.hiddenWeights.numCols() - 1)
				  hiddenWeightsStr.deleteCharAt(hiddenWeightsStr.length() - 1);
			}
			hiddenWeightsStr.append("],");
			if(i == this.hiddenWeights.numRows() - 1)
				hiddenWeightsStr.deleteCharAt(hiddenWeightsStr.length() - 1);
		}
		hiddenWeightsStr.append("]");

		for(int i = 0; i < outputWeights.numRows(); i++) {
			for(int j = 0; j < outputWeights.numCols(); j++) {
				outputWeightsStr.append(outputWeights.get(i, j) + ",");
				if(j == this.outputWeights.numCols() - 1)
					outputWeightsStr.deleteCharAt(outputWeightsStr.length() - 1);
			}
			outputWeightsStr.append("],");
			if(i == this.outputWeights.numRows() - 1)
				outputWeightsStr.deleteCharAt(outputWeightsStr.length() - 1);
		}
		outputWeightsStr.append("]");

		FileHandler.writeStringToFile(hiddenWeightsStr.toString(), "hiddenweights.txt");
		FileHandler.writeStringToFile(outputWeightsStr.toString(), "outputweights.txt");

		Double[][] hiddenWeightsArray = MatrixUtils.matrixToDouble2dArray(hiddenWeights);
		Double[][] outputWeightsArray = MatrixUtils.matrixToDouble2dArray(outputWeights);

		HttpUtils.push(hiddenWeightsArray, outputWeightsArray);
	}
	
	public void test() {
		int numCorrect = 0;
		for(Data data : testData) {
			SimpleMatrix oy = feedforward(data, false);
			int guess = determineNum(oy);
			if(guess == data.getLabel()) {
				numCorrect++;
			} 
		}
		double percentCorrect = (double)numCorrect/Driver.NUMPICTURES * 100.0;
		System.out.println("Test results: " + percentCorrect + "%");
	}
	
}
