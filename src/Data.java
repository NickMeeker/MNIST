import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;

public class Data {
	private SimpleMatrix inputs;
	private SimpleMatrix target;
	private int label;
	
	public Data(SimpleMatrix inputs, int label) {
		this.inputs = inputs;
		this.label = label;
		this.target = new SimpleMatrix(Driver.OUTPUTS, 1);
		
		target.fill(0.0);
		target.set(label, 0, 1);
	}
	
	public void cleanData() {
		inputs = inputs.divide(255.0);
	}
	
	public SimpleMatrix getInputs() {
		return this.inputs;
	}
	
	public SimpleMatrix getTarget() {
		return this.target;
	}
	
	public int getLabel() {
		return this.label;
	}
}
