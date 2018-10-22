package net.sf.jremoterun.utilities.nonjdk.cvsutils

import com.opencsv.CSVReader
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger


@CompileStatic
abstract class CsvComparator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * Columns id starts with 0
     */
    List<Integer> skipColumns = []


    /**
     * Columns id starts with 0
     */
    List<Integer> columnsIds = []

    CSVReader csvReader
    List<String> header
    List<String> currentLine;

    abstract void createReader()

    abstract void headerRead()

    abstract void writeLine(List<String> line)

    abstract List<String> customLineProcessing(List<String> line2)


    /**
     * Main method
     */
    void processLines() {
        createReader()
        assert csvReader != null
        readHeader()
        headerRead()
        skipColumns = skipColumns.sort().reverse()
        doChecks()
        currentLine = header
        processLine()
        while (true) {
            String[] next = csvReader.readNext()
            if (next == null) {
                break;
            }
            List<String> line = next.toList()
            currentLine = line
            if (line.size() != header.size()) {
                onDiffColumnCount()
            }
            printProgressIfNeeded()
            try {
                processLine()
            } catch (Throwable e) {
                log.info "failed process ${currentLine} : ${e}"
                throw e
            }
        }
    }

    void readHeader() {
        String[] header2 = csvReader.readNext()
        if (header2 == null) {
            throw new IllegalStateException("no headers")
        }
        header = header2.toList()
    }

    int findColumn(String columnName) {
        int i = header.indexOf(columnName)
        if (i == -1) {
            throw new Exception("Column ${columnName} not found, available : ${header}")
        }
        return i
    }

    List<Integer> findColumns(List<String> columns) {
        return columns.collect { findColumn(it) }
    }


    void doChecks() {
        assert columnsIds.size() > 0
        List<Integer> badColumns = skipColumns.findAll { columnsIds.contains(it) }
        if (badColumns.size() > 0) {
            throw new Exception("skip columns contains id columns : ${badColumns}")
        }
    }

    void onDiffColumnCount() {
        throw new Exception("Diff ${csvReader.getLinesRead()} line size ${currentLine.size()}, header size = ${header.size()}")
    }

    void processLine() {
        final List<String> line = currentLine
        List<String> line2 = new ArrayList<>(line)
        line2 = customLineProcessing(line2)
        List<String> idColumns = columnsIds.collect { line.get(it) }
        skipColumns.collect { line2.remove(it) }
        line2.addAll(0, idColumns)
        writeLine(line2)
    }

    void printProgressIfNeeded() {
        long linesRead = csvReader.getLinesRead()
        boolean needPrint = isNeedPrintProgress(linesRead)
        if (needPrint) {
            log.info "processing line : ${linesRead}"
        }
    }

    boolean isNeedPrintProgress(long linesRead) {
        if (linesRead < 10) {
            return true
        }
        if (linesRead < 100 && linesRead % 10 == 0) {
            return true
        }
        if (linesRead % 100000 == 0) {
            return true
        }
        return false
    }


}
