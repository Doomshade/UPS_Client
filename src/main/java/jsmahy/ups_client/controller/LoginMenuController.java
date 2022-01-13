package jsmahy.ups_client.controller;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.out.just_connected.PacketJustConnectedOutHello;

public class LoginMenuController {

    public void login(String name) {
        PacketJustConnectedOutHello helloOut = new PacketJustConnectedOutHello(name);
        NetworkManager.getInstance().sendPacket(helloOut);
    }
}
