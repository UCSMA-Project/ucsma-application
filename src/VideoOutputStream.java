import com.github.sarxos.webcam.Webcam;
import hypercast.HCastOutputStream;
import hypercast.HyperCastConfig;
import hypercast.I_OverlaySocket;
import hypercast.StreamManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VideoOutputStream extends Thread{
    Webcam webcam;
    private HCastOutputStream out;

    public VideoOutputStream(String configFile, int streamId) throws InterruptedException {
        HyperCastConfig config = HyperCastConfig.createConfig(configFile);
        I_OverlaySocket socket = config.createOverlaySocket(null);
        socket.joinOverlay();
        Thread.sleep(4000);

        StreamManager streamManager = socket.getStreamManager();
        webcam = Webcam.getDefault();
        webcam.open();
        out = streamManager.getOutputStream(streamId);
    }

    @Override
    public void run() {
        while(true) {
            BufferedImage bufferedImage = webcam.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, "jpg", baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = baos.toByteArray();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(bytes.length);

            int length = bytes.length;
            byte[] lengthBytes;
            lengthBytes = BytesUtil.toBytes(length);
            byte[] result = new byte[8192];
            System.arraycopy(lengthBytes, 0, result, 0, lengthBytes.length);
            System.arraycopy(bytes, 0, result, lengthBytes.length, bytes.length);
            try {
                out.write(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
