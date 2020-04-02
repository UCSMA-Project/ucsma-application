import hypercast.*;
import javax.sound.sampled.*;
import java.io.*;

public class HAudioOutputStream {
    //ByteArrayOutputStream byteArrayOutputStream;
    HCastOutputStream out;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    boolean stopCapture; //controls the data capture duration; for now, leave it always capturing

    public HAudioOutputStream(String configFile, int streamId) throws InterruptedException, IOException {
        HyperCastConfig ConfObj = HyperCastConfig.createConfig(configFile);
        I_OverlaySocket MySocket = ConfObj.createOverlaySocket(null);
        MySocket.joinOverlay();
        Thread.sleep(4000); // InterruptedException addressed
        StreamManager streamManager = MySocket.getStreamManager();

        //String MyLogicalAddress = MySocket.getLogicalAddress().toString();
        //System.out.println("Logical address is " + MyLogicalAddress + ".");

        // capture from microphone and save audio data in byteArrayOutputStream
        try {
            //byteArrayOutputStream = new ByteArrayOutputStream();
            out = streamManager.getOutputStream(streamId);
            stopCapture = false;

            // set up for capturing
            audioFormat = getAudioFormat(); //see getAudioFormat class

            // get a TargetDataLine object from AudioSystem
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo); //gets the available mixer; could exist other mixers (microphones) for selection

            targetDataLine.open(audioFormat);
            targetDataLine.start(); //allows the line to engage in data input i.e. begins to capture data from the mic, store in internal buffer & available to program
            //note that program must continue to read data from the internal buffer at a rate that avoids overflow

            // create a thread which continues capturing and saving the audio data until input to stop
            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();

        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    //AudioFormat specifies the sampled audio data's arrangement of the bytes including properties such as
    // the number of channels, sample size & rate, and the encoding technique
    private AudioFormat getAudioFormat() {
        //the following parameter values are for linear PCM encoding, the default data encoding
        float sampleRate = 8000.0F; //samples per second: 8000, 11025, 16000, 22050 or 44100 are allowed
        int sampleSizeInBits = 16; //bits per sample: 8 and 16 are allowed
        int channels = 1; //1 channel for mono and 2 channels for stereo
        boolean signed = true; //true for signed data or false for unsigned data
        boolean bigEndian = false; //order of the data bytes stored in memory: true for big-endian or false for little-endian.
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    class CaptureThread extends Thread {
        byte[] tempBuffer = new byte[8192]; //can change the size but must match with the field in configFile

        public void run() {
            try {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                while (!stopCapture & cnt > 0) {
                    out.write(tempBuffer, 0, cnt);
                    out.flush();
                    cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                }
                out.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HAudioOutputStream hAudioOutputStream = new HAudioOutputStream("/hypercast.xml", 1111);
    }

}
