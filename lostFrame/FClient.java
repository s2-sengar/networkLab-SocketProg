import java.net.*;
import java.io.*;
import java.util.*;

public class FClient {

	public static void main(String[] args) {

		DatagramSocket cs = null;
		FileOutputStream fos = null;

		try {

			cs = new DatagramSocket();

			cs.setSoTimeout(3000); // set timout in ms

			Scanner sc = new Scanner(System.in);
			byte[] rd, sd, fr;
			String reply;
			DatagramPacket sp, rp;
			int count = 0;
			boolean end = false;
			int flag = 0;
			int consignmentCount = 0;

			String req = "";
			String reqArray[] = new String[0];

			if (flag == 0) {
				System.out.println("Send File Request:");
				req = sc.nextLine();
				reqArray = req.split(" ");
				flag++;
			}

			if (reqArray[0].equals("REQUEST")) {
				// write received data into demoText1.html
				fos = new FileOutputStream(reqArray[1].substring(0, reqArray[1].length() - 5) + "-recived.html");
				// rename the client side recived file;
				fr = reqArray[1].getBytes();
				sp = new DatagramPacket(fr, fr.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
				cs.send(sp);

				System.out.println("Requesting Requesting " + reqArray[1] + " from server"
						+ InetAddress.getByName(args[0]) + ":" + Integer.parseInt(args[1]) + " serverport");

				while (!end) {
					String ack = "" + count;

					// send ACK
					sd = ack.getBytes();
					sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(args[0]), // ip address
							Integer.parseInt(args[1])); // port no

					cs.send(sp);
					System.out.println("Sent ACK " + ack);

					// get next consignment
					rd = new byte[512];
					rp = new DatagramPacket(rd, rd.length);

					try {
						cs.receive(rp);
						reply = new String(rp.getData());
						if (!reply.trim().equals("END")) {
							System.out.println("Recived CONSIGNMENT #" + consignmentCount);
							consignmentCount++;
							fos.write(rp.getData());
						} else { // if last consignment
							end = true;
						}
						count++;
					} catch (SocketTimeoutException e) {
						// TODO: Exception Handling for frame loss;
						System.out.println("Timeout");
						sd = ack.getBytes();
						sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(args[0]), // ip address
								Integer.parseInt(args[1])); // port no
						cs.send(sp);

					}

				}
			} else {
				System.out.println("Invalid REQUEST FORMAT\n Give input in format of REQUEST fileName CRLF");
			}

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (cs != null)
					cs.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}
