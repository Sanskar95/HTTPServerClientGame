import IO.FileIOManager;
import IO.InputReader;
import IO.OutputWriter;
import model.ResponseMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientManager implements Runnable {

    private Socket client;
    private InputReader in;
    private OutputWriter out;
    private String receivedContents;
    Map<Integer, List<String>> map;
    private String methodType;

    ClientManager(Socket client, Map<Integer, List<String>> map) {
        this.client = client;
        this.map= map;
    }

    @Override
    public void run() {

        try {
            in = new InputReader( this.client.getInputStream() );


            out = new OutputWriter( this.client.getOutputStream() );
            String startLine = in.readNextLine();
            Integer cookieId = null;
            if (startLine != null) {
                receivedContents = startLine + "\r\n" + new String( in.read() ).trim();

                if(receivedContents.contains("Cookie")){
                    cookieId=Integer.parseInt(receivedContents.split("clientId=")[1].split("\r")[0]);
                }
                System.out.println(receivedContents);
            }

            if (startLine == null || startLine.isEmpty() || startLine.isBlank()) {
                closeConnection();
            } else {
                StringTokenizer stk;
                stk = new StringTokenizer( startLine , " " );
                String req = stk.nextToken(), path = "", httpType = "";
                if (stk.hasMoreTokens()) path = stk.nextToken();
                if (stk.hasMoreTokens()) httpType = stk.nextToken();

                if (!httpType.equalsIgnoreCase( "HTTP/1.1" )) {
                    invalidHandler();
                } else if (req.equalsIgnoreCase( "GET" )) {
                    this.setMethodType("GET");
                    GETHandler(cookieId );
                } else if (req.equalsIgnoreCase( "POST" )) {
                    this.setMethodType("POST");
                    POSTHandler(cookieId);
                } else {
                    invalidHandler();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void GETHandler(Integer id) throws IOException {
        List<String> responses = new ArrayList<>();
        if(receivedContents.contains("Cookie")) {
           responses= map.get(id);
        }else{
          GameServer.id= GameServer.id +1;
        }
        String getReply = new String( FileIOManager.readFileBytes( "http_post.html" ) ).replaceFirst( "<h4> Result -> </h4>" , "<h4> Result ->" + responses+" </h4>" );
        HTTPResponseHandler( "200 OK" , "text/html" , getReply.getBytes() );
    }

    private void POSTHandler(Integer id) throws IOException {
        String data = receivedContents.substring( receivedContents.lastIndexOf( "number=" ) + "number=".length() );


            String responseMessage= NumberGuessService.guessNumber(Integer.parseInt(data)).label;

            if(Objects.isNull(map.get(id))){
                List<String> responsesMessages = new ArrayList<>();
                map.put(id, responsesMessages);
            }

        if(NumberGuessService.guessNumber(Integer.parseInt(data)).equals(ResponseMessage.EQUAL)){
            responseMessage = responseMessage + map.get(id).size();
        }
            List<String> responsesMessages = map.get(id);
             responsesMessages.add(responseMessage);
             map.put(id, responsesMessages);
        String postReply = new String( FileIOManager.readFileBytes( "http_post.html" ) ).replaceFirst( "<h4> Result -> </h4>" , "<h4> Result ->" + map.get(id).toString() + " </h4>" );
        HTTPResponseHandler( "200 OK" , "text/html" , postReply.getBytes() );
    }

    private void invalidHandler() throws IOException {
        HTTPResponseHandler( "400 BAD REQUEST" , null , null );
    }


    private void HTTPResponseHandler(String status , String MMI , byte[] contents ) throws IOException {
        out.writeLine( "HTTP/1.1 " + status );
        if (MMI != null) {
            out.writeLine( "Content-Type: " + MMI );
            out.writeLine( "Content-Length: " + contents.length );
            out.writeLine( "Connection:  keep-alive");
            if(this.methodType.equals("GET")){
                out.writeLine("Set-Cookie: clientId="+ GameServer.id+"; expires=Wednesday,31-Dec-20 21:00:00 GMT");
            }

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

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}