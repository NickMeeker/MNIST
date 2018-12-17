import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.ejml.simple.SimpleMatrix;

public class Network {
	ArrayList<Data> inputs;
	ArrayList<Data> testData;
	SimpleMatrix weights;
	SimpleMatrix outputs;
	SimpleMatrix outputWeights;

	public Network(ArrayList<Data> inputs, ArrayList<Data> testData) {
		this.inputs = inputs;
		this.testData = testData;
		this.weights = new SimpleMatrix(Driver.HIDDEN, Driver.INPUTS);
		this.outputWeights = new SimpleMatrix(Driver.OUTPUTS, Driver.HIDDEN);
		this.outputs = new SimpleMatrix(Driver.OUTPUTS, 1);
		
		// need to initialize weights to random values 0-1
		Random rand = new Random();
		for(int i = 0; i < this.weights.numRows(); i++) {
			for(int j = 0; j < this.weights.numCols(); j++) {
				this.weights.set(i, j, rand.nextGaussian()*-0.5);
			}
		}
		
		for(int i = 0; i < this.outputWeights.numRows(); i++) {
			for(int j = 0; j < this.outputWeights.numCols(); j++) {
				this.outputWeights.set(i, j, rand.nextGaussian()*-0.5);
			}
		}
	}
	
	
	public double sigmoid(double x) {
		return 1.0 / (double)(1 + Math.pow(Math.E, -x));
	}
	
	public void printMatrix(SimpleMatrix m) {
		for(int i = 0; i < m.numRows(); i++) {
			for(int j = 0; j < m.numCols(); j++) {
				System.out.print(m.get(i, j) + " ");
			}
			System.out.println();
		}
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
	
	public SimpleMatrix feedforward(SimpleMatrix input) {
		// Step 1: Calculate hidden X:
		SimpleMatrix hx = weights.mult(input);
		
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
		return oy;
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
				// Step 1: Calculate hidden X:
				SimpleMatrix hx = weights.mult(inputs.get(i).getInputs());
				
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
				
				// Step 5: Evaluate:
				SimpleMatrix E = inputs.get(i).getTarget().minus(oy);

				// Step 6: Calculate hidden layer errors and adjust:
				SimpleMatrix hE = outputWeights.transpose().mult(E);
				SimpleMatrix dho = calcDelta(E, oy, hy);
				SimpleMatrix dih = calcDelta(hE, hy, inputs.get(i).getInputs());
				outputWeights = outputWeights.plus(dho);
				weights = weights.plus(dih);
			}
			test();
			trainCounter++;
		}
		
		
	}
	
	public void test() {
		int numCorrect = 0;
		for(Data data : testData) {
			SimpleMatrix oy = feedforward(data.getInputs());
			int guess = determineNum(oy);
			if(guess == data.getLabel()) {
				numCorrect++;
			} 
		}
		double percentCorrect = (double)numCorrect/Driver.NUMPICTURES * 100.0;
		System.out.println("Test results: " + percentCorrect + "%");
	}
	
}
