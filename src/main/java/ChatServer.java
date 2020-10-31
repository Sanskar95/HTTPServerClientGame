import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer
{

    public static void main(String[] args) throws IOException
   {
       ServerSocket serverConnect = new ServerSocket( 5000 );
       System.out.println( "Server started.\nListening for connections on port : " + 5000 + " ...\n" );
       while (true) new ClientManager( serverConnect.accept() ).start();

}
}

