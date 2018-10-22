package net.sf.jremoterun.utilities.nonjdk.maven.pluginext;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.maven.model.Model;
import org.apache.maven.project.validation.DefaultModelValidator;
import org.apache.maven.project.validation.ModelValidationResult;

import java.util.logging.Logger;

@CompileStatic
class NoopMavenModelValidator extends DefaultModelValidator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    public ModelValidationResult validate(Model model) {
        return new ModelValidationResult();
    }
}
