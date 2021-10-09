package jsmahy.ups_client.net;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public enum PacketDirection {
    /**
     * Server -{@literal >}Client
     */
    CLIENT_BOUND, // out
    /**
     * Client -{@literal >}Server
     */
    SERVER_BOUND // in
}
