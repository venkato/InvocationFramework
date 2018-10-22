package net.sf.jremoterun.utilities.nonjdk.winutils

import com.github.tuupertunut.powershelllibjava.PowerShell
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult

import java.util.logging.Logger

@CompileStatic
class WinCmdUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static void stopService(String serviceName) {
        String cmd = " sc config ${serviceName} start= disabled"
        NativeProcessResult.runNativeProcessAndWait cmd
        cmd = " sc stop ${serviceName}"
        NativeProcessResult.runNativeProcessAndWait cmd

    }

    static void exportMountedDrives1(File exportFile) {
        File parentFile = exportFile.getParentFile()
        if (!parentFile.exists()) {
            throw new FileNotFoundException("${parentFile}")
        }
        if (!exportFile.getName().endsWith('.reg')) {
            throw new Exception("bad file extention ${exportFile}")
        }
        String cmd = "reg Export HKEY_CURRENT_USER\\Network ${exportFile.getCanonicalFile().getAbsolutePath()}"
        NativeProcessResult.runNativeProcessAndWait cmd
    }

    /**
     * https://superuser.com/questions/1105292/backup-mapped-drive-paths
     */
    static void exportMountedDrives3(File exportFile) {
        File parentFile = exportFile.getParentFile()
        assert parentFile.exists()
        exportFile.delete()
        assert !exportFile.exists()
        String cmd = exportcmd + exportFile.getAbsolutePath();
        PowerShell psSession = PowerShell.open();
        log.info psSession.executeCommands(createCommands(cmd));
        psSession.close()
        if (!exportFile.exists()) {
            throw new Exception("export failed with unknown reason : ${exportFile}")
        }
    }

    static String[] createCommands(String commands) {
        List<String> strings = commands.tokenize('\r\n')
        strings = strings.collect { it.trim() }
        strings = strings.findAll { !it.startsWith('#') }
        return strings.toArray(new String[0])
    }



    public static String exportcmd = '''
# Define array to hold identified mapped drives.
$mappedDrives = @()

# Get a list of the drives on the system, including only FileSystem type drives.
$drives = Get-PSDrive -PSProvider FileSystem

# Iterate the drive list
foreach ($drive in $drives) {
    # If the current drive has a DisplayRoot property, then it's a mapped drive.
    if ($drive.DisplayRoot) {
        # Exctract the drive's Name (the letter) and its DisplayRoot (the UNC path), and add then to the array.
        $mappedDrives += Select-Object Name,DisplayRoot -InputObject $drive
    }
}

# Take array of mapped drives and export it to a CSV file.
$mappedDrives | Export-Csv ''';


    private static String importCmd = '''
# Import drive list.
$mappedDrives = Import-Csv mappedDrives.csv

# Iterate over the drives in the list.
foreach ($drive in $mappedDrives) {
    # Create a new mapped drive for this entry.
    New-PSDrive -Name $drive.Name -PSProvider "FileSystem" -Root $drive.DisplayRoot -Persist -ErrorAction Continue 
}
''';
}
