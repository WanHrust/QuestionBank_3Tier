import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class MultyThread {
	static int nClients = 0;

	public static void main(String argv[]) throws IOException {
		ServerSocket ss = new ServerSocket(2008);
		System.out.println("Server Started");
		while (true) {
			new TinyHttpdConnection(ss.accept());
			MultyThread.nClients++;
			System.out.println("New connection started" + MultyThread.nClients);
		}
	}
} // sending the socket returned from accept to thread

class TinyHttpdConnection extends Thread {
	Socket sock;
	boolean isRunning;
	int counter;
	QuestionAnswerHolder h;
	TinyHttpdConnection(Socket s) {
		sock = s;
		counter=1;
		isRunning = true;
		try {
			h = new QuestionAnswerHolder();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPriority(NORM_PRIORITY - 1);
		start();

	}

	public void run() {
		Question q = null;
		while (isRunning) {
			try {
//
//				System.out.println("Client number " + MultyThread.nClients
//						+ " connetcted");
				OutputStream out = sock.getOutputStream();
				PrintWriter pw = new PrintWriter(out, true);
				InputStream clientIn = sock.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						clientIn));
				// System.out.println("Reachable");
				String clientMessage = br.readLine().toUpperCase();
				// System.out.println("Unreachable");
				if (clientMessage == null) {
					sock.close();
					System.out.println("Connection closed");
					isRunning = false;
				} else
					switch (clientMessage) {
					case "QUIT":
						sock.close();
						System.out.println("Connection closed");
						isRunning = false;
						break;
					case "NEXTQUESTION":
						if (counter<=20){
							q = h.getRandomQuestion(0);
							pw.println(q.getQuestion());
							}
						else if (counter<=28){
							q = h.getRandomQuestion(1);
							pw.println(q.getQuestion() + "#" + q.getA() + "#" + q.getB() + "#" + q.getC() + "#" + q.getD() + "#" + q.getE());
							}
						counter++;
						// TODO
						break;
					case "TRUE":
					case "FALSE":
						if(clientMessage.equalsIgnoreCase(q.getCorrectAnswer()))
							pw.println("Correct");
						else
							pw.println("Wrong");
						//TODO
						break;
					case "GETANSWER":
						pw.println(q.getCorrectAnswer());
						break;
					
					}
			}
			catch(NullPointerException e){
				isRunning = false;
				System.out.println("Connection closed");
			}
			catch(SocketException e){
				isRunning = false;
				System.out.println("Connection2 closed");
				try {
					sock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch (SQLException e) {
				isRunning = false;
				System.out.println("Database failure");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			}
		} // end of while
	}
}
