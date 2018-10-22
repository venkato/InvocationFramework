package net.sf.jremoterun.utilities.nonjdk.disktest

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

// https://github.com/philiprodriguez/DiskTester
@CompileStatic
class DiskPerformanceTesterWrapper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    // Arguments to be filled in before starting
    private static int fileCount;
    private static int fileSize;
    private static String dirPath = "./";

    // Other stuff
    private static final Scanner scan = new Scanner(System.in);

    static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            boolean fileCountSatisfied = false;
            boolean fileSizeSatisfied = false;

            for (int a = 0; a < args.length; a++) {
                if (args[a].equals("-fileSize")) {
                    if (isPositiveIntegerString(args[a+1])) {
                        fileSize = Integer.parseInt(args[a+1]);
                        a++;
                        fileSizeSatisfied = true;
                    } else {
                        System.out.println("File size must be a positive integer.");
                    }
                } else if (args[a].equals("-fileCount")) {
                    if (isPositiveIntegerString(args[a+1])) {
                        fileCount = Integer.parseInt(args[a+1]);
                        a++;
                        fileCountSatisfied = true;
                    } else {
                        System.out.println("File count must be a positive integer.");
                    }
                } else if (args[a].equals("-dirPath")) {
                    dirPath = args[a+1];
                    a++;
                    if (!dirPath.endsWith("/"))
                        dirPath += "/";
                } else {
                    System.out.println("Illegal argument at position " + a + ": " + args[0]);
                    printUsage();
                    System.exit(1);
                }
            }

            if (!(fileCountSatisfied && fileSizeSatisfied)) {
                System.out.println("One or more required arguments unsatisfied.");
                printUsage();
                System.exit(1);
            }
        } else {
            printUsage();

            System.out.println("Enter the desired file size to test with:");
            fileSize = scan.nextInt();
            System.out.println("Enter the desired file count to test with:");
            fileCount = scan.nextInt();
            System.out.println("Enter the desired working directory:");
            scan.nextLine();
            dirPath = scan.nextLine();
        }
        new DiskPerformanceTester(fileCount,fileSize,new File(dirPath));
    }

    private static boolean isPositiveIntegerString(String s) {
        return s.matches("[0-9]+") && s.charAt(0) != '0';
    }

    private static void printUsage() {
        System.out.println("USAGE:");
        System.out.println("java DiskTester -fileSize (size in bytes) -fileCount (count integer) [-dirPath (path to working directory)]");
    }

    private static void prepareArguments(String[] args) {

    }

}