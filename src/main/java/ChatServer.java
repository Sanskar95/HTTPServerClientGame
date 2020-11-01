import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer
{
    public   static   List<ClientManager>  clientManagers = new ArrayList<>();
    public static void main(String[] args) throws IOException
   {
       ServerSocket serverConnect = new ServerSocket( 5000 );
       Socket clientSocket;
       System.out.println( "Server started.\nListening for connections on port : " + 5000 + " ...\n" );
       while (true){
           clientSocket = serverConnect.accept();
           ClientManager clientManager = new ClientManager(clientSocket);

           Thread thread = new Thread(clientManager);
           thread.start();
       }

}
}

