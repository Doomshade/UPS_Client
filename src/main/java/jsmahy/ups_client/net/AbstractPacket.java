package jsmahy.ups_client.net;

import java.io.Serializable;

abstract class AbstractPacket implements Packet {
    private final int id;

    public AbstractPacket(int id){
        this.id = id;
    }


    @Override
    public final int getId() {
        return id;
    }
}
