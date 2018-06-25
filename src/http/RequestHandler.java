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

public class RequestHandler extends Thread { // 쓰레드가 하나 만들어짐
	private static final String DOCUMENT_ROOT = "./webapp";

	private Socket socket;

	public RequestHandler(Socket socket) {
		this.socket = socket; // 만들어진 소켓을 가지고 연결 진행 -> "GET/http://1.0~~" 등을 받음
	}

	@Override
	public void run() {
		try {

			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			consoleLog("connected from " + inetSocketAddress.getAddress().getHostAddress() + ":"
					+ inetSocketAddress.getPort()); // 포트번호 로그 출력

			// get IOStream
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); // 보조스트림.
			OutputStream os = socket.getOutputStream();
			String request = null;
			while (true) {
				String line = br.readLine();
				String[] tokens = line.split(" ");
				
				if (tokens[0].equals("GET")) {
					responseStaticResource(os, tokens[1], tokens[2]);
				} else {
					response400Error(os, tokens[2]);
				}
				if (line == null || "".equals(line)) { // 헤더만 읽겠다는 의미. 이 다음에 나오는건 바디
					break;
				}
				if (request == null) {
					request = line;
					break;
				}
			}
			consoleLog(request);
			// 예제 응답입니다.
			// 서버 시작과 테스트를 마친 후, 주석 처리 합니다.

			// os.write( "<h1>이 페이지가 잘 보이면 실습과제 SimpleHttpServer를 시작할 준비가 된
			// 것입니다.</h1>".getBytes( "UTF-8" ) );

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

	private void responseStaticResource(OutputStream outputStream, String url, String protocol) throws IOException {
		if (url.equals("/")) {
			url = "/index.html";
		}
		File file = new File("./webapp" + url);
		if (file.exists() == false) {
			response404Error(outputStream, protocol);
			return;
		}
		Path path = file.toPath();
		byte[] body = Files.readAllBytes(path);
		String mimeType = Files.probeContentType(path);
		outputStream.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
		outputStream.write(("Content-Type:" + mimeType + "; charset=utf-8\r\n").getBytes("UTF-8"));
		outputStream.write("\r\n".getBytes());
		outputStream.write(body);
	}

	private void response404Error(OutputStream outputStream, String protocol) throws IOException {
		File file = new File("./webapp/error/404.html");
		Path path = file.toPath();
		byte[] body = Files.readAllBytes(path);
		outputStream.write( ( protocol + " 404 File Not Found\r\n").getBytes() );
		outputStream.write( "Content-Type:text/html\r\n".getBytes() );
		outputStream.write( "\r\n".getBytes() ); 
		outputStream.write( body );
	}
	private void response400Error(OutputStream outputStream, String protocol) throws IOException {
		File file = new File("./webapp/error/400.html");
		Path path = file.toPath();
		byte[] body = Files.readAllBytes(path);
		outputStream.write( ( protocol + "400 Bad Request\r\n").getBytes() );
		outputStream.write( "Content-Type:text/html\r\n".getBytes() );
		outputStream.write( "\r\n".getBytes() ); 
		outputStream.write( body );

	}

	private void consoleLog(String message) {
		System.out.println("[RequestHandler#" + getId() + "] " + message);
	}
}