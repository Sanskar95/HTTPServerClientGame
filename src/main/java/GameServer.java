import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer
{
    public static Integer id =0;
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverConnect = new ServerSocket( 5000 );
        Map<Integer, List<String>> map= new HashMap<>();

        Socket clientSocket;
        System.out.println( "Server started.\nListening for connections on port : " + 5000 + " ...\n" );
        while (true){
            clientSocket = serverConnect.accept();
            ClientManager clientManager = new ClientManager(clientSocket, map);
            Thread thread = new Thread(clientManager);
            thread.start();
        }

    }
}