import java.util.Random;
import java.util.Scanner;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Test {

    public static final String ANSI_RESET ="\u001B[0m";
    public static final String ANSI_YELLOW ="\033[1;33m";
	
	public static int getRandomNumberUsingInts(int min, int max) {
	    Random random = new Random();
	    return random.ints(min, max)
	      .findFirst()
	      .getAsInt();
	}
	
	
	
    public static void main(String[] args) throws Exception{

        // Add File
        File folder = new File("DataStore"); 
        folder.mkdir();
        File file = new File(folder, "data.txt");
        file.createNewFile();

        System.out.println("Building an initial B+-Tree...");
        System.out.println("Launching B+-Tree test program…");

        // Input commands
        Scanner input = new Scanner(System.in);
        System.out.println("Waiting for your commands:");

        while (input.hasNextLine()) {
            String command = input.nextLine();

            // Insert
            if (command.contains("insert")) {
                if (command.substring(0, 6).equals("insert")) {
                    String[] strs = command.split(" ");
                    int minRandom = Integer.parseInt(strs[1]);
                    int maxRandom = Integer.parseInt(strs[2]);
                    int number = Integer.parseInt(strs[3]);

                    BPlusTree<Product, Integer> b = new BPlusTree<>(4);
                    for (int i = 0; i < number; i++) {
                        int randomNumber = getRandomNumberUsingInts(minRandom, maxRandom);
                        Product p = new Product(randomNumber, null);
                        b.insert(p, p.getId());
                        insertToDataFile(Integer.toString(randomNumber));
                    }
                
                    System.out.println(ANSI_YELLOW + number + " data entries with keys randomly chosen between [" + minRandom + ", " + maxRandom + "] are inserted!" + ANSI_RESET);
                }
            }

            // Quit the app
            if (command.equals("quit")) {
                System.out.println(ANSI_YELLOW + "  Thanks! Good Bye~" + ANSI_RESET);
                System.exit(0);
            }
        }
    }

    public static void insertToDataFile(String value) {
		String path = System.getProperty("user.dir") + "\\DataStore\\data.txt";
		String text = value + '\n';
        try {
			Files.write(Paths.get(path), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
	}
}

