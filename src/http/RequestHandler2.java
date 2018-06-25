package http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class RequestHandler2 extends Thread {
	   private static final String DOCUMENT_ROOT = "./webapp";

	   private Socket socket;

	   public RequestHandler2(Socket socket) {
	      this.socket = socket;
	   }

	   @Override
	   public void run() { // 브라우저의 모든요청을 담당한다.
	      try {
	         // logging Remote Host IP Address & Port
	         InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
	         consoleLog("connected from " + inetSocketAddress.getAddress().getHostAddress() + ":"
	               + inetSocketAddress.getPort()); // head

	         // get IOStream
	         BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	         OutputStream os = socket.getOutputStream(); // body

	         String request = null;
	         while (true) {
	            String line = br.readLine();
	            if (line == null || "".equals(line)) {
	               break; // 라인이 비어있으면 읽지 않겟다는 조건
	            }

	            if (request == null) {
	               request = line;
	               break;
	            }
	         }
	         consoleLog(request);

	         // 예제 응답입니다.
	         // 서버 시작과 테스트를 마친 후, 주석 처리 합니다. 밑에 내용이 브라우저한테 간다.
	         os.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
	         os.write("Content-Type:text/html; charset=utf-8\r\n".getBytes("UTF-8"));
	         os.write("\r\n".getBytes());
	         os.write("<h1>이 페이지가 잘 보이면 실습과제 SimpleHttpServer를 시작할 준비가 된 것입니다.</h1>".getBytes("UTF-8"));
	      } catch (Exception ex) {
	         consoleLog("error:" + ex);
	      } finally {
	         // clean-up
	         try {
	            if (socket != null && socket.isClosed() == false) {
	               socket.close();
	            }
	         } catch (IOException ex) {
	            consoleLog("error:" + ex);
	         }
	      }
	   }
	   private void consoleLog(String message) {
	      System.out.println("[RequestHandler#" + getId() + "] " + message);
	   }
	}