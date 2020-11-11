import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer {

    public static Integer highestClientId = 0;

    public static void main(String[] args) throws IOException {

        if(args.length != 1) {
            System.out.println("One argument required: <port-number>");
            System.out.println("Syntax: java " + GameServer.class.getSimpleName() + " <port-number>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = new ServerSocket(port);
        Map<Integer, List<String>> clientGuessesMap = new HashMap<>();

        Socket clientSocket;
        System.out.println( "Server started.\nListening for connections on port: " + port + " ...\n" );

        while (true){
            clientSocket = serverSocket.accept();
            ClientManager clientManager = new ClientManager(clientSocket, clientGuessesMap);
            Thread thread = new Thread(clientManager);
            thread.start();
        }
    }
}