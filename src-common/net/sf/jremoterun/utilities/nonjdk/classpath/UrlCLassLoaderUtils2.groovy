package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.UrlToFileConverter

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@CompileStatic
class UrlCLassLoaderUtils2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static byte[] createJarForLoadingClasses(List<File> files) throws Exception {
        String classPath = files.collect {
            String res = 'file:/' + it.absolutePath.replace('\\', '/');
            if (it.isDirectory()) {
                res += '/'
            }
            return res;
        }.join(' ')
        classPath = "Class-Path: ${classPath}"
        List<String> r = []
        int pos = 0
        while (true) {
            int next = pos + 70
            if (next >= classPath.length()) {
                r.add(classPath.substring(pos));
                break
            } else {
                r.add(classPath.substring(pos, next))
                pos = next
            }
        }
        classPath = r.join('\n ')

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ZipOutputStream outZip = new ZipOutputStream(out);

        final ZipEntry outZipEntry = new ZipEntry("META-INF/MANIFEST.MF");
        outZip.putNextEntry(outZipEntry);
        String content =
                """Manifest-Version: 1.0
${classPath}
""".replace('\r\n', '\n').replace('\n', '\r\n')
        outZip.write(content.getBytes());
        outZip.closeEntry();
        outZip.finish();
        outZip.flush();
        out.close();
        return out.toByteArray()
    }

    /**
     * return without tail
     */
    static List<File> getClassLocationAll2(final Class clazz) {
        final String tailJava = clazz.getName().replace('.', '/') + ".class";
        final String tailGroovy = clazz.getName().replace('.', '/') + ".groovy";
        List<URL> urls = getClassLocationAll(clazz.getName(), clazz.getClassLoader())
        // log.info "urls = ${urls}"
        List<File> files = urls.collect {
            final String asString1 = it.toString();
            String asString;
            if (asString1.endsWith('.class')) {
                asString = asString1.substring(0, asString1.length() - tailJava.length());
            } else if (asString1.endsWith('.groovy')) {
                asString = asString1.substring(0, asString1.length() - tailGroovy.length());
            } else {
                throw new UnsupportedOperationException("strange file : ${it}")
            }
            if (asString.startsWith("jar:")) {
                asString = asString.substring(4, asString.length() - 2);
            }
            URL url = new URL(asString)
            File res = UrlToFileConverter.c.convert(url)
            return res
        }
        return files
    }

    /**
     * return without tail
     */
    static File getClassLocationFirst(final Class clazz) {
        List<File> all = getClassLocationAll2(clazz);
        if (all.size() == 0) {
            throw new FileNotFoundException("${clazz.name}")
        }
        return all.first();
    }

    static File convertFullPathToRootDotClass(final URL pathFull, String className) {
//        int dotCount = className.count('.')
        String asString = pathFull.toString();
        String suffix = UrlCLassLoaderUtils.buildClassNameSuffix(className)
        asString = asString.substring(0, asString.length() - suffix.length());
//        asString.findLastIndexOf {}
        if (asString.startsWith("jar:")) {
            asString = asString.substring(4, asString.length() - 2);

        }
        URL url = new URL(asString)
        File file3 = UrlToFileConverter.c.convert url
        return file3

    }


    static File convertFullPathToRootDotGroovy(final URL pathFull, String className) {
//        int dotCount = className.count('.')
        String asString = pathFull.toString();
        String suffix = UrlCLassLoaderUtils.buildClassNameSuffixGroovy(className)
        asString = asString.substring(0, asString.length() - suffix.length());
//        asString.findLastIndexOf {}
        if (asString.startsWith("jar:")) {
            asString = asString.substring(4, asString.length() - 2);

        }
        URL url = new URL(asString)
        File file3 = UrlToFileConverter.c.convert url
        return file3

    }

    /**
     * return without tail
     */
    static File convertClassLocationToPathToJar(URL urlRes, final String tail) {
        if (urlRes == null) {
            throw new NullPointerException("class location is null for ${tail}")
        }
        String url = urlRes.toString();
        url = url.substring(0, url.length() - tail.length());
        if (url.startsWith("jar:")) {
            url = url.substring(4, url.length() - 2);
        }
        URL url2 = new URL(url)
        File file3 = UrlToFileConverter.c.convert url2
        return file3
    }

    /**
     * Return locations with tail
     */
    static List<URL> getClassLocationAll(final String className, ClassLoader classLoader)
            throws MalformedURLException {
        if(className == Class.getName()){
            throw new IllegalArgumentException("Strange class name : ${className}")
        }
        final String tail = UrlCLassLoaderUtils.buildClassNameSuffix(className);
        if (classLoader == null) {
            log.info("class loaded by boot class loader : ${className}, finding any resource instead of all")
            URL res = JrrUtils.getClassLocation(classLoader.loadClass(className))
            return [res]
        }
        Enumeration<URL> resources = classLoader.getResources(tail);
        List<URL> list = resources.toList()
        final String tailGroovy = UrlCLassLoaderUtils.buildClassNameSuffixGroovy(className);
        list.addAll(classLoader.getResources(tailGroovy).toList());
        return list
    }


    static void printSpi(Class spi) {
        printSpi(spi.getName())
    }

    static void printSpi(String spi) {
        Enumeration<URL> services = JrrClassUtils.currentClassLoader.getResources("META-INF/services/${spi}")
        List<URL> list = services.toList()
        log.info "found ${list.size()} spi : ${list}"
        list.each {
            log.info "${it}:\n${it.text}"
        }

    }

}
