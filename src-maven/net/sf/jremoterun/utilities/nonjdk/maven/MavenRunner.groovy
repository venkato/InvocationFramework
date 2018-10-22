package net.sf.jremoterun.utilities.nonjdk.maven

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import org.codehaus.plexus.classworlds.launcher.Launcher
import org.codehaus.plexus.classworlds.realm.ClassRealm
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException

import java.lang.reflect.InvocationTargetException
import java.util.logging.Logger

@CompileStatic
class MavenRunner  extends org.codehaus.plexus.classworlds.launcher.Launcher{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    ClRef mavenCli = new ClRef('org.apache.maven.cli.MavenCli')


    void run(List<String> args2) {
        log.info "running maven ..."
        String[] args = args2.toArray(new String[0]);
        int code = mainWithExitCodeJrr(args)
        log.info "code = ${code}"
    }

    @Override
    protected void launchEnhanced(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        log.info "launching enhanced"
//        ClassRealm mainRealm = getMainRealm();

//        Class<?> mainClass = getMainClass();
//        log.info "${mainClass.getName()}"
//        log.info2  JrrUtils.getClassLocation(mainClass)
//        log.info "cl ${mainClass.getClassLoader()}"
//        log.info "parent cl = ${mainClass.getClassLoader().getParent()}"
//        log.info "cl class = ${mainClass.getClassLoader().getClass().getName()}"
        super.launchEnhanced(args)
    }

    @Override
    protected void launchStandard(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        log.info "launching standard"
        super.launchStandard(args)
    }

    int mainWithExitCodeJrr( String[] args )
            throws Exception
    {
        MavenRunner launcher = this
        String classworldsConf = System.getProperty( launcher.CLASSWORLDS_CONF );

        InputStream is;



        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        launcher.setSystemClassLoader( cl );

        if ( classworldsConf != null )
        {
            is = new FileInputStream( classworldsConf );
        }
        else
        {
            if ( "true".equals( System.getProperty( "classworlds.bootstrapped" ) ) )
            {
                is = cl.getResourceAsStream( launcher.UBERJAR_CONF_DIR + launcher.CLASSWORLDS_CONF );
            }
            else
            {
                is = cl.getResourceAsStream( launcher.CLASSWORLDS_CONF );
            }
        }

        if ( is == null )
        {
            throw new Exception( "classworlds configuration not specified nor found in the classpath" );
        }

        launcher.configure( is );

        is.close();

        try
        {
            launcher.launch( args );
        }
        catch ( InvocationTargetException e )
        {
            log.info("failed via one launcher",e)
            ClassRealm realm = launcher.getWorld().getRealm( launcher.getMainRealmName() );

            URL[] constituents = realm.getURLs();

            System.out.println( "---------------------------------------------------" );

            for ( int i = 0; i < constituents.length; i++ )
            {
                System.out.println( "constituent[" + i + "]: " + constituents[i] );
            }

            System.out.println( "---------------------------------------------------" );

            // Decode ITE (if we can)
            Throwable t = e.getTargetException();

            if ( t instanceof Exception )
            {
                throw (Exception) t;
            }
            if ( t instanceof Error )
            {
                throw (Error) t;
            }

            // Else just toss the ITE
            throw e;
        }

        return launcher.getExitCode();
    }
}
