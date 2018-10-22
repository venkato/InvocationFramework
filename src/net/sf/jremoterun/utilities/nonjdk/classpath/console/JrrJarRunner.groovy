package net.sf.jremoterun.utilities.nonjdk.classpath.console


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.ObjectWrapper;
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams;
import org.apache.commons.io.IOUtils;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
class JrrJarRunner implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static String mainClassMajicName = 'Main-Class:'

    @Override
    void run() {
        String argg = GroovyMethodRunnerParams.gmrp.args[0]
        File file1 = argg as File
        if (!file1.exists()) {
            throw new FileNotFoundException(file1.getAbsolutePath())
        }
        run3(file1, GroovyMethodRunnerParams.gmrp)

    }

    void run3(File f, GroovyMethodRunnerParams gmrp) {
        String manifestFromJar = extractManifestFromJar(f)
        String mainClass = findMainClassFromManifest(manifestFromJar)
        log.info "mainClass : ${mainClass}"
        gmrp.addFilesToClassLoader.addF f
        gmrp.args.remove(0)
        gmrp.args.add (0,'main')
        gmrp.args.add(0,mainClass)

    }


    String findMainClassFromManifest(String manifest) {
        List<String> lines = manifest.readLines()
        String linee = lines.find { it.startsWith(mainClassMajicName) }
        if (linee == null) {
            throw new Exception("${mainClassMajicName} not found in : ${manifest}")
        }
        linee = linee.replace(mainClassMajicName, '')
        return linee.trim()
    }

    String extractManifestFromJar(File f) {
        ObjectWrapper<String> res = new ObjectWrapper<>(null)
        ZipEntryCallback zec = new ZipEntryCallback() {
            @Override
            void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                if (zipEntry.getName() == 'META-INF/MANIFEST.MF') {
                    byte[] arrayRes = IOUtils.toByteArray(inputStream)
                    String s2 = new String(arrayRes)
                    if (res.getObject() != null) {
                        throw new Exception("Jar has 2 manifests : \n${res.object}\n 2nd: \n${s2}")
                    }
                    res.setObject(s2)
                }

            }
        };
        ZipUtil.iterate(f, zec)
        String object = res.getObject()
        if (object == null) {
            throw new Exception('manifest not found')
        }
//        log.info "${object}"
        return object.trim()
    }


}
