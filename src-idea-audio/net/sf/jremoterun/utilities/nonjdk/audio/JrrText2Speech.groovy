package net.sf.jremoterun.utilities.nonjdk.audio

import groovy.transform.CompileStatic
import marytts.LocalMaryInterface;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.Mixer;
import java.util.logging.Logger;

@CompileStatic
class JrrText2Speech {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<String> devicesToPlayDefault = []
    public static JrrText2Speech defaultTts = new JrrText2Speech()
    public static Date lastPlayed;

    List<Mixer.Info> getAllDevicesToPlay() {
        Mixer.Info[] info1 = AudioSystem.getMixerInfo();
        List<Mixer.Info> list1 = info1.toList()
        list1 = list1.findAll { isSupported(it) };
        return list1
    }

    static void playTextOnAllDevicesS(String text) {
        defaultTts.playTextOnAllDevices(text)
    }

    void playTextOnAllDevices(String text) {
        List<Mixer.Info> info1 = getAllDevicesToPlay()
        if (info1.size() == 0) {
            throw new Exception('No devices to play')
        }
        info1.each {
            playTextOnDevice(text, it)
        }
    }

    boolean isSupported(Mixer.Info info) {
        if (!info.getClass().getName().endsWith('DirectAudioDeviceInfo')) {
            return false
        }
        String name = info.getName()
        String find1 = devicesToPlayDefault.find { it.length() > 1 && name.contains(it) }
        return find1 != null
    }

    Clip playTextOnDevice(String text, Mixer.Info device) {
        AudioInputStream audio = createAudioStream(text);
        Clip audioclip = AudioSystem.getClip(device);
        log.info "class name = ${audioclip.getClass().getName()}"
        audioclip.open(audio);
        audioclip.start();
        lastPlayed = new Date()
        return audioclip;
    }

    AudioInputStream createAudioStream(String text) {
        LocalMaryInterface mary = new LocalMaryInterface();
        AudioInputStream audio = mary.generateAudio(text);
        return audio
    }

}
