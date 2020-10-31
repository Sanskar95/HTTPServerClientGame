import IO.FileIOManager;
import IO.InputReader;
import IO.OutputWriter;
import model.ResponseMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class ClientManager implements Runnable {

    private final String NOT_FOUND = "<html>\n" + "<head><title>404 Not Found</title></head>\n" +
            "<body bgcolor=\"white\">\n" +
            "<center><h1>404 Not Found</h1></center>\n" +
            "<hr><center>:/</center>\n" +
            "</body>\n" +
            "</html>";
    private String name;
    private Socket client;
    private InputReader in;
    private OutputWriter out;
    private String receivedContents;

    public ClientManager( String name , Socket client ) {
        this.name = name;
        this.client = client;
    }

    ClientManager( Socket client ) {
        this.client = client;
        name = "Client";
    }

    void start() {
        new Thread( this , name ).start();
    }

    @Override
    public void run() {

        try {
            in = new InputReader( this.client.getInputStream() );


            out = new OutputWriter( this.client.getOutputStream() );
            String startLine = in.readNextLine();

            if (startLine != null) {
                receivedContents = startLine + "\r\n" + new String( in.read() ).trim();
            }

            if (startLine == null || startLine.isEmpty() || startLine.isBlank()) {
//				INVALID_Handler();
//				HTTP_Write( "400 BAD REQUEST",null,null );
                closeConnection();
            } else {
                StringTokenizer stk;
                stk = new StringTokenizer( startLine , " " );
                String req = stk.nextToken(), path = "", httpType = "";
                if (stk.hasMoreTokens()) path = stk.nextToken();
                if (stk.hasMoreTokens()) httpType = stk.nextToken();

                if (!httpType.equalsIgnoreCase( "HTTP/1.1" )) {
                    INVALID_Handler();
                } else if (req.equalsIgnoreCase( "GET" )) {
                    GET_Handler( path );
                } else if (req.equalsIgnoreCase( "POST" )) {

                    POST_Handler();
                } else {
                    INVALID_Handler();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void GET_Handler( String path ) throws IOException {

        if (path.equalsIgnoreCase( "/" )) path = "/index.html";

        File file = new File( path.substring( 1 ) );

        if (!file.canRead()) {
            HTTP_Write( "404 NOT FOUND" , "text/html" , NOT_FOUND.getBytes() );
        } else {
            HTTP_Write( "200 OK" , Files.probeContentType( file.toPath() ) , FileIOManager.readFileBytes( file.getPath() ) );
        }
    }

    private void POST_Handler() throws IOException {
        Boolean correctAnswerFlag = false;
        String data = receivedContents.substring( receivedContents.lastIndexOf( "number=" ) + "number=".length() );
        Path path = Paths.get("test.txt");

        Boolean doesFileExist =
                Files.exists(path,
                        new LinkOption[]{ LinkOption.NOFOLLOW_LINKS });
        if(doesFileExist){
            String filename= "test.txt";
            FileWriter fw = new FileWriter(filename,true); //the true will append the new data
            String responseMessage= NumberGuessService.guessNumber(Integer.parseInt(data)).label;

            if(NumberGuessService.guessNumber(Integer.parseInt(data)).equals(ResponseMessage.EQUAL)){
                responseMessage = responseMessage+ " " + Files.lines(Paths.get("test.txt"), Charset.defaultCharset()).count();
                correctAnswerFlag = true;
            }
            fw.write( responseMessage+ '\n');//appends the string to the file
            fw.close();

        }else
        {
            File file = new File( "test.txt");
            file.createNewFile();
        }


        String postReply = new String( FileIOManager.readFileBytes( "http_post.html" ) ).replaceFirst( "<h4> Result -> </h4>" , "<h4> Result ->" + new String(Files.readAllBytes(Paths.get("test.txt"))) + " </h4>" );
        if(correctAnswerFlag){
            File file = new File( "test.txt");
            file.delete();
        }
        HTTP_Write( "200 OK" , "text/html" , postReply.getBytes() );
    }

    private void INVALID_Handler() throws IOException {
        HTTP_Write( "400 BAD REQUEST" , null , null );
    }


    private void HTTP_Write( String status , String MMI , byte[] contents ) throws IOException {
        out.writeLine( "HTTP/1.1 " + status );
        if (MMI != null) {
            out.writeLine( "Content-Type: " + MMI );
            out.writeLine( "Content-Length: " + contents.length );
            out.writeLine( "Connection: close" );
        }
        out.writeLine();
        if (contents != null) out.write( contents );

        closeConnection();
    }

    void closeConnection() throws IOException {
        out.close();
        in.close();
        client.close();
    }
}