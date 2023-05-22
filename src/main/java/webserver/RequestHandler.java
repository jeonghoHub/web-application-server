package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = bufferedReader.readLine();
            log.debug("request line : {}", line);
            log.debug("first line : {}", line);
            String[] s = line.split(" ");
            String s2 = s[1];

            System.out.println(s2);

            if (line == null) {
                return;
            }

            while (!line.equals("")) {
                line = bufferedReader.readLine();
                log.debug("header : {}", line);
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World".getBytes();
            if (s2.equals("/index.html")) {
                System.out.println("index 파일 입니까?");
                body = Files.readAllBytes(new File("./webapp" + s2).toPath());
                System.out.println(body);
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
