import java.util.*;
import java.io.*;

public class SplitCSV {
  public static final String BASE_FILE_NAME = "data";

  public static void writeStringToFile(String input, String filename) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter((filename)));
      bw.write(input);
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void split(String filename) throws Exception {

    File file = new File(BASE_FILE_NAME + ".csv");
    int fileCounter = 1, lineCounter = 0;
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    StringBuilder output = new StringBuilder();
    while ((line = br.readLine()) != null) {
      output.append(line + "\n");
      lineCounter++;

      if (lineCounter % 6000 == 0) {
        writeStringToFile(output.toString(), (BASE_FILE_NAME) + fileCounter + ".csv");
        fileCounter++;
        output = new StringBuilder();
      }

    }
  }

  public static void main(String[] args) {
    String filename = args[0];
    try {
      split(filename);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}