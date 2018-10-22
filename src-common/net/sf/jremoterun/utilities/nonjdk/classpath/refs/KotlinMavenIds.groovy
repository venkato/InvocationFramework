package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum KotlinMavenIds  implements MavenIdContains, ToFileRef2, EnumNameProvider {
    allopen,
    android_extensions,
    android_extensions_runtime,
    annotation_processing,
    annotation_processing_embeddable,
    annotation_processing_gradle,
    annotation_processing_maven,
    annotation_processing_runtime,
    annotations_android,
    annotations_jvm,
    build_common,
    compiler,
    compiler_client_embeddable,
    compiler_embeddable,
    compiler_runner,
    daemon,
    daemon_client,
    daemon_client_new,
    daemon_embeddable,
    gradle_plugin,
    gradle_plugin_api,
    gradle_plugin_model,
    main_kts,
    maven_allopen,
    maven_noarg,
    maven_sam_with_receiver,
    maven_serialization,
    //native_library_reader,
    native_utils,
    noarg,
    osgi_bundle,
    reflect,
    sam_with_receiver,
    sam_with_receiver_compiler_plugin,
    script_runtime,
    script_util,
    scripting_common,
    scripting_compiler,
    scripting_compiler_embeddable,
    scripting_compiler_impl,
    scripting_compiler_impl_embeddable,
    scripting_intellij,
    scripting_jsr223,
    //scripting_jsr223_embeddable,
    scripting_jvm,
    scripting_jvm_host,
    //scripting_jvm_host_embeddable,
    serialization,
    serialization_unshaded,
    //source_sections_compiler_plugin,
    stdlib,
    stdlib_common,
    stdlib_jdk7,
    stdlib_jdk8,
    stdlib_js,
    test,
    test_annotations_common,
    test_common,
    test_js,
    test_junit,
    test_junit5,
    //test_nodejs_runner,
    test_testng,
    util_io,
    util_klib,
    ;


    MavenId m;

    KotlinMavenIds() {
        String artifactId = 'kotlin-'+name().replace('_','-')
        m = new MavenId('org.jetbrains.kotlin', artifactId, '1.4.32');
    }

    public static List<KotlinMavenIds> all = values().toList()

    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

    @Override
    String getCustomName() {
        return m.artifactId;
    }



}
