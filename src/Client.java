import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {

	private static final int PORT_NO = 1500;
	private static final String DEFAULT_DIR = "/home/minaz/clientFtpDir";

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", PORT_NO);
		DataInputStream diStream = new DataInputStream(socket.getInputStream());
		DataOutputStream doStream = new DataOutputStream(socket.getOutputStream());
		File dir = new File(DEFAULT_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		String response = diStream.readUTF();
		System.out.print("\t\t\t" + response);
		response = diStream.readUTF();
		System.out.println(response);
		Scanner input = new Scanner(System.in);
		sendCommand(socket, diStream, doStream, input);
		
		/*byte[] receivedFile = new byte[5000];////////// initialize
		diStream.read(receivedFile, 0, 5000);*/
		
		diStream.close();
		socket.close();
	}

	/**
	 * @param diStream
	 * @param doStream
	 * @param input
	 * @throws IOException
	 */
	private static void sendCommand(Socket socket, DataInputStream diStream,
			DataOutputStream doStream, Scanner input) throws IOException {
		String in = input.next();
		
		switch (in) {
		case "help":
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			sendCommand(socket, diStream, doStream, input);
			break;
		case "list":
			doStream.writeUTF(in);
			diStream.readUTF();
		case "upload":
			doStream.writeUTF(in);
			System.out.println("Enter file name");
			//System.out.println(diStream.readUTF()); 
			/*TODO: client should enter full path 
			to file (choose file to upload)*/
			in = input.next(); /*TODO: change it so that filename is only required for client 
			side (or maybe use that to create file with same name on server sid)e*/
			File myFile = new File(DEFAULT_DIR + File.separatorChar + in); //TODO: check file path
            //
			int count;
			byte[] buffer = new byte[1024];

			OutputStream out = socket.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
			while ((count = bis.read(buffer)) >= 0) {
			     out.write(buffer, 0, count);
			     //out.flush();
			}
			
		case "download":
			doStream.writeUTF(in);
			in = diStream.readUTF();
			System.out.println(in);
			in = input.next();
			doStream.writeUTF(in);
			in = diStream.readUTF();
			System.out.println(in);
			
			//
			FileOutputStream fos = new FileOutputStream(DEFAULT_DIR + File.separatorChar + "file");
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] b = new byte[1024];
			int c;
			InputStream is = socket.getInputStream();
			while((c = is.read(b)) >= 0){
				fos.write(b, 0, c);
			}
			fos.close();
			//bos.close();
		default:
			break;
		}
		
		//doStream.writeUTF(in);
		input.close();
	}

}
