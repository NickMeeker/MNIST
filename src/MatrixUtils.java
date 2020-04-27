import org.ejml.simple.SimpleMatrix;

public class MatrixUtils {
    public static Double[][] matrixToDouble2dArray(SimpleMatrix matrix) {
        Double[][] array = new Double[matrix.numRows()][matrix.numCols()];

        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++) {
                array[i][j] = matrix.get(i, j);
            }
        }

        return array;
    }

    public static SimpleMatrix double2dArrayToMatrix(double[][] array) {
        SimpleMatrix matrix = new SimpleMatrix(array.length, array[0].length);

        for(int i = 0; i < matrix.numRows(); i++) {
            for(int j = 0; j < matrix.numCols(); j++) {
                matrix.set(i, j, array[i][j]);
            }
        }

        return matrix;
    }

    public static void printMatrix(SimpleMatrix m) {
        for(int i = 0; i < m.numRows(); i++) {
            for(int j = 0; j < m.numCols(); j++) {
                System.out.print(m.get(i, j) + " ");
            }
            System.out.println();
        }
    }
}
