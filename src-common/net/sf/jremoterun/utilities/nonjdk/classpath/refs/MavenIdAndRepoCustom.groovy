package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum;

import java.util.logging.Logger;

@CompileStatic
enum MavenIdAndRepoCustom implements MavenIdAndRepoContains, ToFileRef2{
    eclipseGitHubApi(new MavenId('org.eclipse.mylyn.github:org.eclipse.egit.github.core:5.7.0.202003110725-r'), MavenRepositoriesEnum.eclipse),
    ;

    MavenId m;
    IBiblioRepository repo;

    MavenIdAndRepoCustom(MavenId m, IBiblioRepository repo) {
        this.m = m
        this.repo = repo
    }

    @Override
    MavenIdAndRepo getMavenIdAndRepo() {
        return new MavenIdAndRepo(m,repo)
    }

    @Override
    File resolveToFile() {
        return getMavenIdAndRepo().resolveToFile()
    }
}
