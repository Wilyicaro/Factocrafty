package wily.factocrafty.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import java.io.File;

public class SoundUtil {

    public static double getSoundDurationInSeconds(SoundEvent soundEvent) {
        ResourceLocation location = Sound.SOUND_LISTER.idToFile(soundEvent.getLocation());
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(manager.soundEngine.soundBuffers.resourceManager.open(location));
            AudioFormat audioFormat = audioInputStream.getFormat();

            long audioFileLength = audioInputStream.getFrameLength();
            float frameRate = audioFormat.getFrameRate();
            double durationInSeconds = (audioFileLength + 0.0) / frameRate;

            return durationInSeconds;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}