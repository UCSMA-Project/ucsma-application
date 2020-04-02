import hypercast.HCastInputStream;
import hypercast.HyperCastConfig;
import hypercast.I_OverlaySocket;
import hypercast.StreamManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class VideoInputStream extends Thread{

    private HCastInputStream in;
    private JFrame frame;

    public VideoInputStream(String configFile, int streamId) {
        HyperCastConfig config = HyperCastConfig.createConfig(configFile);
        I_OverlaySocket socket = config.createOverlaySocket(null);
        socket.joinOverlay();
        StreamManager streamManager = socket.getStreamManager();
        in = streamManager.acceptInputStream(streamId);

        frame = new JFrame();
        frame.setSize(640, 360);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        JLabel label = new JLabel();
        label.setSize(640, 360);
        label.setVisible(true);

        frame.add(label);
        frame.setVisible(true);
    }

    @Override
    public void run() {
        while (true) {
            byte[] bytes = new byte[8192];
            try {
                in.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int length;
            length = bytes[3] + (bytes[2] << 8) + (bytes[1] << 16) + (bytes[0] << 24);
            byte[] imageBytes = new byte[length];
            for (int i = 4; i < length + 4; i++) {
                imageBytes[i - 4] = bytes[i];
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage recvImg = null;
            try {
                recvImg = ImageIO.read(bis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Graphics graphics;
            graphics = frame.getGraphics();

            graphics.drawImage(recvImg, recvImg.getWidth(), recvImg.getHeight(), null);
        }
    }
}
