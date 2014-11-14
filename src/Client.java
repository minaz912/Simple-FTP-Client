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
	// private static final String DEFAULT_DIR = "~/clientFtpDir"; //enable this
	// for linux systems
	private static final String DEFAULT_DIR = "C:\\Client";

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Socket socket;
		if (args.length > 0) {
			socket = new Socket("localhost", Integer.parseInt(args[0]));
		} else {
			socket = new Socket("localhost", PORT_NO);
		}
		DataInputStream diStream = new DataInputStream(socket.getInputStream());
		DataOutputStream doStream = new DataOutputStream(
				socket.getOutputStream());
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

		/*
		 * byte[] receivedFile = new byte[5000];////////// initialize
		 * diStream.read(receivedFile, 0, 5000);
		 */

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
		String in = input.nextLine();

		switch (in) {
		case "help":
			doStream.writeUTF(in);
			in = diStream.readUTF();
			System.out.println(in);
			sendCommand(socket, diStream, doStream, input);
			break;
		case "list":
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			sendCommand(socket, diStream, doStream, input);
			break;
		case "cd":
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			in = input.nextLine();
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			sendCommand(socket, diStream, doStream, input);
			break;
		case "upload":
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			in = input.nextLine();
			File myFile = new File(DEFAULT_DIR + File.separatorChar + in);
			if (myFile.exists()) {
				doStream.writeUTF(in);
			} else {
				in = "File does not exist!";
				System.out.println(in);
				doStream.writeUTF(in);
				sendCommand(socket, diStream, doStream, input);
				break;
			}
			//
			int count;
			byte[] buffer = new byte[1024];

			OutputStream out = socket.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(myFile));
			while ((count = bis.read(buffer)) >= 0) {
				out.write(buffer, 0, count);
			}
			sendCommand(socket, diStream, doStream, input);
			break;
		case "download":
			doStream.writeUTF(in);
			in = diStream.readUTF();
			System.out.println(in);
			String fName = input.nextLine();
			doStream.writeUTF(fName);
			in = diStream.readUTF();
			System.out.println(in);

			//
			FileOutputStream fos = new FileOutputStream(DEFAULT_DIR
					+ File.separatorChar + fName);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] b = new byte[1024];
			int c;
			InputStream is = socket.getInputStream();
			while ((c = is.read(b)) >= 0) {
				bos.write(b, 0, c);
				if (c < 1024) {
					bos.flush();
					System.out.println("File downloaded!");
					break;
				}
			}
			fos.close();
			// bos.close();
			sendCommand(socket, diStream, doStream, input);
			break;
		case "mv":
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			in = input.nextLine();
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			in = input.nextLine();
			doStream.writeUTF(in);
			System.out.println(diStream.readUTF());
			sendCommand(socket, diStream, doStream, input);
			break;
		case "q":
			System.out.println("Goodbye :)");
			socket.close();
			System.exit(0);
		default:
			System.out.println("Invalid command, please try again!");
			sendCommand(socket, diStream, doStream, input);
			break;
		}

		// doStream.writeUTF(in);
		input.close();
	}

}
