import hypercast.*;
import javax.sound.sampled.*;
import java.io.*;

public class HAudioInputStream {
    HCastInputStream in; //ByteArrayInputStream byteArrayInputStream;
    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;

    public HAudioInputStream(String configFile, int streamId) throws InterruptedException {
        HyperCastConfig ConfObj = HyperCastConfig.createConfig(configFile);
        I_OverlaySocket MySocket = ConfObj.createOverlaySocket(null);
        MySocket.joinOverlay();
        Thread.sleep(4000); // InterruptedException addressed
        StreamManager streamManager = MySocket.getStreamManager();

        //String MyLogicalAddress = MySocket.getLogicalAddress().toString();
        //System.out.println("Logical address is " + MyLogicalAddress + ".");

        //playback audio
        try {
            in = streamManager.acceptInputStream(streamId); //8192 bytes
            audioFormat = getAudioFormat();

            // create an audio stream based on the input byte stream, byteArrayInputStream
            audioInputStream = new AudioInputStream(in, audioFormat, 8192 / audioFormat.getFrameSize());
            //the stream's length is measured in 'number of sample frames'

            // get a SourceDataLine object from AudioSystem
            // the getLine method returns a Line object of the specified Line.Info object's type
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo); //Downcast the Line object

            // prepare sourceDataLine for use (open & start)
            sourceDataLine.open(audioFormat); //opens the line with specified format for acquiring relevant system resources
            sourceDataLine.start();

            // start a thread, which will run until all the previously captured data's played back
            Thread playThread = new Thread(new PlayThread()); //declares additional instance variable tempBuffer to Thread object, see inner class below
            playThread.start(); //causes the object's run method to be executed

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

    class PlayThread extends Thread {
        byte[] tempBuffer = new byte[8192]; //can specify other values for byte array length, according to available memory of the hardware

        public void run() {
            try {
                // fill up tempBuffer with data from the audio stream
                int cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length); //counter for the number of bytes that will be later read into the buffer; the read method reads up to a specified maximum number of bytes of data (the length of the tempBuffer) putting them into the specified byte array (tempBuffer), beginning at the specified byte index (0)
                while (cnt > 0) {
                    // writes 8192 bytes to sourceDataLine, which is automatically delivered to the speaker of the hardware
                    sourceDataLine.write(tempBuffer, 0, cnt);
                    cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length);
                }
                // after all data read from the audio stream, finishing off
                sourceDataLine.drain(); //causes the program to block and wait for the internal buffer of the SourceDataLine to empty (all data delivered to speaker)
                sourceDataLine.close(); //releases resources

            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        HAudioInputStream hAudioInputStream = new HAudioInputStream("./hypercast.xml", 1111);
    }
}
