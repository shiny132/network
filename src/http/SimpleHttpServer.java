package http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
	
	private static final int PORT = 8088;

	public static void main(String[] args) {

		ServerSocket serverSocket = null;

		try {
			// 1. Create Server Socket
			serverSocket = new ServerSocket(); // 소켓 클래스 생성
			   
			// 2. Bind
			String localhost = InetAddress.getLocalHost().getHostAddress(); //로컬호스트 가져오기 ( IP )
			serverSocket.bind( new InetSocketAddress( localhost, PORT ) ); //소켓어드레스 만들기 (인터넷 어드레스)
			consoleLog("bind " + localhost + ":" + PORT); // 바인딩이 잘 되었는지 여부 로그

			while (true) {
				// 3. Wait for connecting ( accept )
				Socket socket = serverSocket.accept(); // accept를 하게되면 Blocking이 되면서 잠시 정지하기 때문에 무한루프도 문제없음

				// 4. Delegate Processing Request
				new RequestHandler2(socket).start(); //소켓을 던져줌 그리고 start를 하면 RequestHandler에서 run이 실행됨
			}

		} catch (IOException ex) {
			consoleLog("error:" + ex);
		} finally {
			// 5. clean-up
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException ex) {
				consoleLog("error:" + ex);
			}
		}
	}

	public static void consoleLog(String message) {
		System.out.println("[HttpServer#" + Thread.currentThread().getId()  + "] " + message); //현재 쓰레드 번호 출력
	}
}