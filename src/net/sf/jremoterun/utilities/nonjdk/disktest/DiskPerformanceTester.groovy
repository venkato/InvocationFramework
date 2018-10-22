package net.sf.jremoterun.utilities.nonjdk.disktest

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class DiskPerformanceTester {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    // Arguments to be filled in before starting
    private final int fileCount;
    private final int fileSize;
    private final File dirPath;
    public Random r = new Random();

    private final byte[][] filesInMemory;
    private RandomAccessFile[] randomAccessFiles;

    public long totalReadFailures = 0;
    public long totalWriteFailures = 0;
    public long totalOtherFailures = 0;
    public long totalWrites = 0;
    public long totalReads = 0;

    public long sleepBetweenIterations = 5000;
    public long sleepOnFailure = 1000;
    public volatile boolean continueRun = true;
    public volatile int iteration = 1;
    public volatile int maxIteration = 0; // mean unlimited
    public HashSet<File> createdFiles= new HashSet<>();


    DiskPerformanceTester(int fileCount, int fileSize, File dirPath) {
        this.fileCount = fileCount
        this.fileSize = fileSize
        this.dirPath = dirPath
        println("Allocating memory...");
        filesInMemory = new byte[fileCount][fileSize];
    }


    void start() throws InterruptedException {
        println("OK");


        while (continueRun) {
            if (maxIteration > 0 && iteration > maxIteration) {
                break
            }
            onIteration(iteration);
            Thread.sleep(sleepBetweenIterations);
            iteration++;
        }
        closeAndDelete()
        log.info "finish"

    }

    void closeAndDelete() {
        randomAccessFiles.toList().each { it.close(); }
        createdFiles.each { it.delete() }
    }

    protected void onIteration(final int iteration) throws InterruptedException {
        printSummary();
        //TODO: cleanup previous iteration here

        println("Iteration " + iteration);

        println("Determining file content...");
        for (int i = 0; i < fileCount; i++) {
            r.nextBytes(filesInMemory[i]);
        }
        println("OK");

        print("Creating files for writing...");
        randomAccessFiles = new RandomAccessFile[fileCount];
        for (int i = 0; i < fileCount; i++) {
            try {
                File childFile = new File(dirPath, "DiskTester" + (i + 1) + ".dat");
                createdFiles.add(childFile)
                randomAccessFiles[i] = new RandomAccessFile(childFile, "rws");
            } catch (FileNotFoundException exc) {
                println("(!!!) FAILED to create file " + (i + 1) + "!");
                totalOtherFailures++;
                throw exc;
            }
        }
        println("OK");

        println("Writing file content...");
        long start = System.currentTimeMillis();
        for (int i = 0; i < fileCount; i++) {
            try {
                randomAccessFiles[i].seek(0);
            } catch (IOException exc) {
                println("(!!!) FAILED to seek to position 0 in file " + (i + 1) + "!");
                totalOtherFailures++;
                throw exc;
            }

            totalWrites += 1;
            try {
                randomAccessFiles[i].write(filesInMemory[i]);
                randomAccessFiles[i].getFD().sync();
                randomAccessFiles[i].close();
            } catch (IOException exc) {
                println("(!!!) FAILED to write to file " + (i + 1) + "!");
                totalWriteFailures++;
                throw exc;
            }
        }
        long end = System.currentTimeMillis();
        println("OK");
        println("Writing took " + (end - start) + "ms");

        println("Re-opening files for reading...");
        randomAccessFiles = new RandomAccessFile[fileCount];
        for (int i = 0; i < fileCount; i++) {
            try {
                File childFile = new File(dirPath, "DiskTester" + (i + 1) + ".dat");
                randomAccessFiles[i] = new RandomAccessFile(childFile, "r");
            } catch (FileNotFoundException exc) {
                println("(!!!) FAILED to open file " + (i + 1) + "!");
                totalOtherFailures++;
                throw exc;
            }
        }
        println("OK");

        // Read
        println("Reading file content...");
        start = System.currentTimeMillis();
        for (int i = 0; i < fileCount; i++) {
            try {
                randomAccessFiles[i].seek(0);
            } catch (IOException exc) {
//                println("(!!!) FAILED to seek to position 0 in file " + (i+1) + "!");
                totalOtherFailures++;
//                exc.printStackTrace();
                log.log(Level.SEVERE, "(!!!) FAILED to seek to position 0 in file " + (i + 1) + "!", exc)
                continue;
            }

            byte[] readBuffer = new byte[4096];
            int bytesRead = -1;
            int placeInFile = 0;
            try {
                while ((bytesRead = randomAccessFiles[i].read(readBuffer)) != -1) {
                    totalReads += bytesRead;

                    // Examine the bytes!
                    for (int b = 0; b < bytesRead; b++) {
                        if (filesInMemory[i][placeInFile] != readBuffer[b]) {
                            println("(!!!) FAILED! Should have been " + filesInMemory[i][placeInFile] + " but got " + readBuffer[b] + " instead!");
                            totalReadFailures++;
                            Thread.sleep(sleepOnFailure);
                        }
                        placeInFile++;
                    }
                }
            } catch (IOException exc) {
                totalReadFailures++;
                log.log(Level.SEVERE, "(!!!) FAILED! Could not read byte!", exc)
            }

            // End of file reached but was that correct?
            if (placeInFile < fileSize) {
                println("(!!!) FAILED! Premature end of file at " + placeInFile + " when it should have been at " + fileSize);
                totalReadFailures++;
                Thread.sleep(sleepOnFailure);
            }
        }
        end = System.currentTimeMillis();
        println("OK");
        println("Reading took " + (end - start) + "ms");
    }


    public void printSummary() {
        println("--------------------Summary--------------------");
        println("Total Reads = " + totalReads);
        println("Total Read Failures = " + totalReadFailures);
        println("Total Writes = " + totalWrites);
        println("Total Write Failures = " + totalWriteFailures);
        println("Total Other Failures = " + totalOtherFailures);
    }


}