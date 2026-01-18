/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laucher;

import gui.MainFrame;
import core.Main;
import java.util.Scanner;

public class AppLauncher {

    public static void main(String[] args) {
       
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        //

        Scanner scanner = new Scanner(System.in);
        boolean valid = false;

        while (!valid) {

            System.out.println("==================================");
            System.out.println(" Movie Collection Manager ");
            System.out.println("==================================");
            System.out.println("1) Launch Graphical Interface (GUI)");
            System.out.println("2) Launch Text-Based Interface (CLI)");
            System.out.println("0) Exit");
            System.out.print("Choose option: ");

            String input = scanner.nextLine().trim();

            switch (input) {

                case "1" -> {
                    valid = true;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        new MainFrame().setVisible(true);
                    });
                }

                case "2" -> {
                    valid = true;
                    Main cli = new Main();
                    cli.startCLI();
                }

                case "0" -> {
                    valid = true;
                    System.out.println("Goodbye!");
                    System.exit(0);
                }

                default -> {
                    System.out.println();
                    System.out.println("Invalid selection. Please enter 1, 2, or 0.");
                    System.out.println();
                }
            }
        }

        scanner.close();
    }
}
