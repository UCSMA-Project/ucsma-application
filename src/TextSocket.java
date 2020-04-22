import hypercast.*;

import java.io.IOException;

public class TextSocket implements I_ReceiveCallback {

    private I_OverlaySocket socket;

    @Override
    public void ReceiveCallback(I_OverlayMessage i_overlayMessage) {
        byte[] data = i_overlayMessage.getPayload();
        System.out.println(new String(data));
    }

    public TextSocket(String configFile) {
        HyperCastConfig config = HyperCastConfig.createConfig(configFile);
        socket = config.createOverlaySocket(this);
        socket.joinOverlay();
    }

    public void sendToAll(String text) {
        byte[] data = text.getBytes();
        I_OverlayMessage message = socket.createMessage(data);
        socket.sendToAll(message);
    }

    public void sendTo(String text, I_LogicalAddress logicalAddress) {
        byte[] data = text.getBytes();
        I_OverlayMessage message = socket.createMessage(data);
        socket.sendToNode(message, logicalAddress);
    }
}
