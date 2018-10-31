package punchout;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.*;

public class MusicManager {

	// Music
	private static Clip currentMusic;
	private static Clip currentClip;

	// continuous sound and clips
	public static MusicManager single = new MusicManager();
	public static MusicManager soundClip = new MusicManager();

	public MusicManager() {
	} // MusicManager

	// returns the MusicManager Object
	public static MusicManager get() {
		return single;
	} // get
	
	public static MusicManager getClip() {
		return soundClip;
	} // get

	// pauses the music
	public void pauseMusic(int milli) {

		currentMusic.stop();
		//musicIsPaused = true;
	} // pauseMusic

	// stops the music
	public void stopMusic() {
		currentMusic.stop();

	} // stopMusic

	// plays the music repeatedly
	public void playMusic(Clip clip) {
		currentMusic = clip;
		currentMusic.setFramePosition(0);
		currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
	}// playMusic

	// plays the music once
	public void playOnce(Clip clip) {
		currentClip = clip;
		currentClip.setFramePosition(0);
		currentClip.loop(0);
	}// playMusic

	// loads a new clip from a url
	public Clip loadSound(String url) {

		try {

			// the clip to be returned
			Clip clip;

			// gets the clip
			clip = AudioSystem.getClip();

			// read audio data from whatever source (file/classloader/etc.)
			InputStream audioSrc = getClass().getResourceAsStream(url);

			// add buffer for mark/reset support
			InputStream bufferedIn = new BufferedInputStream(audioSrc);

			// loads into stream for jar file
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

			// loads in clip object
			clip.open(audioStream);

			// returns the clip
			return clip;

		} catch (Exception e) {

			// prints an error
			System.err.println("Audio clip not found at " + url);
			System.err.println("Exception below");
			System.err.println("--------------------------");
			System.err.println(e);
			System.exit(0);
			return null;

		} // catch
	} // loadSound
} // MusicManager
