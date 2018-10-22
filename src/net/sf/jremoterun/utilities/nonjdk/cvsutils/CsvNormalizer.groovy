package net.sf.jremoterun.utilities.nonjdk.cvsutils

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
abstract class CsvNormalizer extends CsvComparator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File inF;
    public File outF;
    public Reader reader
    public Writer writer
    public static String fileSuffixNormalize = '.normalize';

    /**
     * main method
     */
    void normalizeFile(File inF) {
        assert inF.exists()
        this.inF = inF
        assignOutFile()
        processLines()
        csvReader.close()
        writer.flush()
        writer.close()
    }

    void assignOutFile() {
        assert !inF.getName().endsWith(fileSuffixNormalize)
        outF = new File(inF.getAbsolutePath() + fileSuffixNormalize);
        if(outF.exists()) {
            assert outF.canWrite()
        }else {
            File getParentFile = outF.getParentFile();
            assert getParentFile.canWrite()
        }
    }

    @Override
    void createReader() {
        reader = inF.newReader()
        csvReader = createReaderBuilder().build();
        writer = outF.newWriter()
    }


    CSVReaderBuilder createReaderBuilder() {
        return new CSVReaderBuilder(reader).withCSVParser(createParserBuilder().build())
    }


    /**
     * Set separator here
     */
    CSVParserBuilder createParserBuilder() {
        return new CSVParserBuilder();
    }

    String getOutSeparator() {
        return Character.toString(csvReader.getParser().getSeparator())
    }

    @Override
    void writeLine(List<String> line) {
        writer.println(line.join(getOutSeparator()));
    }
}
