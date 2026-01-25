package launcher;

// Import the main GUI window class
import gui.MainFrame;

// Import the core application logic for the CLI
import core.Main;

// Import Scanner for reading user input from the console
import java.util.Scanner;

public class AppLauncher {

    public static void main(String[] args) {

        // Attempt to set the Nimbus Look and Feel for the GUI
        try {
            // Loop through all installed Look and Feel options
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                // Check if Nimbus is available
                if ("Nimbus".equals(info.getName())) {
                    // Apply Nimbus Look and Feel
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Print stack trace if Look and Feel setup fails
            e.printStackTrace();
        }
        //

        // Scanner object to read user input from the console
        Scanner scanner = new Scanner(System.in);

        // Flag to validate user selection and control the menu loop
        boolean valid = false;

        // Loop until the user enters a valid menu option
        while (!valid) {

            // Display application launch menu
            System.out.println("==================================");
            System.out.println(" Movie Collection Manager ");
            System.out.println("==================================");
            System.out.println("1) Launch Graphical Interface (GUI)");
            System.out.println("2) Launch Text-Based Interface (TBI)");
            System.out.println("0) Exit");
            System.out.print("Choose option: ");

            // Read and trim user input
            String input = scanner.nextLine().trim();

            // Handle user menu selection
            switch (input) {

                // Launch the graphical user interface
                case "1" -> {
                    valid = true; // Mark input as valid to exit loop
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        // Create and display the main GUI window on the Event Dispatch Thread
                        new MainFrame().setVisible(true);
                    });
                }

                // Launch the command-line interface
                case "2" -> {
                    valid = true; // Mark input as valid to exit loop
                    Main cli = new Main(); // Create core application instance
                    cli.startCLI();        // Start the CLI workflow
                }

                // Exit the application
                case "0" -> {
                    valid = true; // Mark input as valid to exit loop
                    System.out.println("Goodbye!");
                    System.exit(0); // Terminate the JVM
                }

                // Handle invalid menu input
                default -> {
                    System.out.println();
                    System.out.println("Invalid selection. Please enter 1, 2, or 0.");
                    System.out.println();
                }
            }
        }

        // Close the scanner to free system resources
        scanner.close();
    }
}
