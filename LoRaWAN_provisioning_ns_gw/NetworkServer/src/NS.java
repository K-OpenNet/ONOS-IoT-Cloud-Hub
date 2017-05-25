
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.json.*;
import org.apache.commons.codec.binary.Base64;

interface DEFINE {
	static final int GW_UDP_PORT = 1680;
	static final int APP_TCP_PORT = 1681;
	static final int MANAGER_NS_TCP_PORT = 1683;

	// Gateway <-> Network Server Message type
	static final int PUSH_DATA = 0;
	static final int PULL_DATA = 2;
	static final int PULL_ACK = 4;
	static final int PULL_RESPONSE = 3;

	static final int MAXIMUM_PACKET_LENGTH = 1500;
	static final int MAXIMUM_QUEUE_LENGTH = 1000;

	// LoRaWAN Message type
	static final byte JOIN_REQUEST = 0;
	static final byte JOIN_ACCEPT = 32;
	static final byte UNCONFIRMED_DATA_UP = 64;
	static final byte UNCONFIRMED_DATA_DOWN = 96;
	static final byte CONFIRMED_DATA_UP = (byte) 160;

	// Network Server <-> Application Server Message type
	// tcp header + version(1) + MSG Type(1)+ DevAddr(4) + JSON Object ( data
	// fport longitude
	// latitude )
	static final byte APP_DATA_UP = 0;
	static final byte APP_DATA_DOWN = 1;

	static final byte HELLO_NS = 3;

}

class GwState {

	byte[] gwEui = new byte[8];
	int port;

	String ipAddr;
	String time;
	Double lati = (double) 0;
	Double longi = (double) 0;

	int alti;
	int rxnb;
	int rxok;
	int rxfw;
	int ackr;
	int dwnb;
	int txnb;
	int updateCount;

	// List downLinkMsg;

}

class DevState {

	class BestGw {

		byte[] gwEui = new byte[8];
		String ipAddr;
		int port;
		int rssi;
		Double lati;
		Double longi;

	}

	byte[] devAddr = new byte[4];
	byte[] devEui = new byte[8];
	byte[] appEui = new byte[8];
	byte[] devNonce = new byte[2];
	
	
	byte[] appNonce = new byte[3];
	
	boolean connected = false;

	long tmst;
	double freq;
	long rfch;

	byte fport;

	short fcntup;
	short fcntdown;

	String datr;
	String codr;

	char currentClass;

	BestGw bestGw = new BestGw();

	DevState() {

		List devStateList = new ArrayList<DevState>();

	}

}

class AppState {
	String ipAddr;
	byte[] appEui = { 1, 1, 1, 1, 1, 1, 1, 1 };
	ArrayBlockingQueue upLinkMsg = new ArrayBlockingQueue<UpMsgAppData>(1000);;
}

class UpMsgAppData {

	JSONObject jsonObjectData;
	byte[] devAddr = new byte[4];

}

public class NS extends Thread {

	GW gw;
	DEV dev;
	APP app;
	MANAGER manager;
	List gwStateList;
	List appStateList;
	List devStateList;

	NsMsgFromGwHandler nsMsgFromGwHandler;

	class DEV {

		DEV() {
			devStateList = new ArrayList<DevState>();
		}
	}

	class MANAGER extends Thread implements DEFINE {

		Socket managerSocket = null;
		String managerAddr;

		MANAGER(String managerAddr) {
			this.managerAddr = managerAddr;
		}

		public void startThread() {
			try {
				managerSocket = new Socket(managerAddr, MANAGER_NS_TCP_PORT);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.start();

			if (managerSocket == null) {
				System.out.println("managerSocket is null");
			}

		}

		@Override
		public void run() {

			InputStream in = null;
			System.out.println("Manager is connected");
			try {
				in = managerSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (true) {
				DataInputStream dis = new DataInputStream(in);
				int msgLength = 0;
				try {
					msgLength = dis.readInt();
					System.out.println("[MANAGER]msglength - " + msgLength);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				byte[] msgTemp = new byte[msgLength - 4];
				try {

					dis.read(msgTemp, 0, msgTemp.length);

					// String tempString = new String(msgTemp,0,msgTemp.length);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("[MANAGER]msg from manager ");

				byte[] jsonByte = new byte[msgTemp.length - 2];
				System.arraycopy(msgTemp, 2, jsonByte, 0, jsonByte.length);
				String jsonString = new String(jsonByte);
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = null;
				int entityFlag = 0;

				try {
					jsonObject = new JSONObject(jsonString);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					jsonArray = jsonObject.getJSONArray("APP");
					entityFlag = 0;
					System.out.println("[MANAGER] HELLO MSG (new APP)");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

				try {
					jsonArray = jsonObject.getJSONArray("GW");
					entityFlag = 1;
					System.out.println("[MANAGER] HELLO MSG (new GW)");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				
				try {
					jsonArray = jsonObject.getJSONArray("DEV");
					entityFlag = 2;
					System.out.println("[MANAGER] HELLO MSG (new DEV)");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

				if (msgTemp[1] == HELLO_NS) {

					if (entityFlag == 0) {	// APP
						System.out.println("[MANAGER]json-" + jsonString);

						AppState appState = new AppState();
						JSONObject tempJsonObject = null;

						for (int i = 0; i < jsonArray.length(); i++) {
							appState = new AppState();
							try {
								tempJsonObject = jsonArray.getJSONObject(i);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								appState.ipAddr = tempJsonObject.getString("ip_address");

								String appEuiTemp[] = tempJsonObject.getString("APP_EUI").split(",");

								byte[] appEui = new byte[appEuiTemp.length];
								// System.out.println("[MANAGER]tempsize"+appEuiTemp.length);
								for (int j = 0; j < appEuiTemp.length; j++) {
									appEui[j] = Byte.parseByte(appEuiTemp[j]);
									// System.out.println("[MANAGER]appEui"+appEui[j]);
								}
								appState.appEui = appEui;
								appStateList.add(appState);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					
					if (entityFlag == 2) {	// DEV
						System.out.println("[MANAGER]json-" + jsonString);

						DevState devState = new DevState();
						JSONObject tempJsonObject = null;

						for (int i = 0; i < jsonArray.length(); i++) {
							try {
								tempJsonObject = jsonArray.getJSONObject(i);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								String devEuiTemp[] = tempJsonObject.getString("DEV_EUI").split(",");

								byte[] appEui = new byte[devEuiTemp.length];
								for (int j = 0; j < devEuiTemp.length; j++) {
									appEui[j] = Byte.parseByte(devEuiTemp[j]);
									// System.out.println("[MANAGER]appEui"+appEui[j]);
								}
								devState.devEui = appEui;
								devStateList.add(devState);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

					else if (entityFlag == 1) {	// GW
						System.out.println("[MANAGER]json-" + jsonString);

						GwState gwState = new GwState();
						JSONObject tempJsonObject = null;

						for (int i = 0; i < jsonArray.length(); i++) {
							try {
								tempJsonObject = jsonArray.getJSONObject(i);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								gwState.ipAddr = tempJsonObject.getString("ip_address");
								gwStateList.add(gwState);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

				}

			}
		}

	}

	class APP implements DEFINE {

		ServerSocket appServerSocket;
		AppAcceptThread appAcceptThread;
		// List appStateList;

		APP() {

			appStateList = new ArrayList<AppState>();
			appAcceptThread = new AppAcceptThread();

		}

		public void startThread() {
			try {
				appServerSocket = new ServerSocket(APP_TCP_PORT);
				appAcceptThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		class AppAcceptThread extends Thread {

			@Override
			public void run() {

				while (true) {

					Socket appUpDownLinkSocket;
					AppState appState;

					try {
						appUpDownLinkSocket = appServerSocket.accept();
						System.out.println("[APP]---------New App server join-------");
						appState = new AppState();
						AppUpLinkThread appUpLinkThread = new AppUpLinkThread(appUpDownLinkSocket, appState);
						appUpLinkThread.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

		class AppUpLinkThread extends Thread {

			class BuildMsgBetweenAppNs {

				byte[] msg;

				BuildMsgBetweenAppNs(UpMsgAppData upMsgAppData) {

					byte[] temp; // = new
									// byte[upMsgAppData.jsonObjectData.toString().length()];
					String tempString = upMsgAppData.jsonObjectData.toString();
					msg = new byte[tempString.getBytes().length + 6];

					temp = tempString.getBytes();

					System.arraycopy(upMsgAppData.devAddr, 0, msg, 2, 4);
					System.arraycopy(temp, 0, msg, 6, temp.length);
				}

				public void putVersion(byte version) {

					msg[0] = version;

				}

				public void putMsgType(byte msgType) {

					msg[1] = msgType;

				}

			}

			Socket appUpDownLinkSocket;

			AppUpLinkThread(Socket appUpDownLinkSocket, AppState appState) {
				this.appUpDownLinkSocket = appUpDownLinkSocket;

			}

			@Override
			public void run() {

				OutputStream outStream;
				DataOutputStream dataOutStream = null;
				UpMsgAppData upMsgAppData = null;

				BuildMsgBetweenAppNs buildMsgBetweenAppNs;
				// appStateList.add(e)

				try {
					outStream = appUpDownLinkSocket.getOutputStream();
					dataOutStream = new DataOutputStream(outStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (true) {
					for (int i = 0; i < appStateList.size(); i++) {
						AppState tempAppState = (AppState) appStateList.get(i);
						if (tempAppState.ipAddr.equals(appUpDownLinkSocket.getInetAddress().toString().split("/")[1])) {

							try {
								upMsgAppData = (UpMsgAppData) tempAppState.upLinkMsg.take();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							buildMsgBetweenAppNs = new BuildMsgBetweenAppNs(upMsgAppData);
							buildMsgBetweenAppNs.putMsgType(APP_DATA_UP);
							buildMsgBetweenAppNs.putVersion((byte) 1);

							try {
								dataOutStream.write(buildMsgBetweenAppNs.msg);
								// String tempString = new
								// String(buildMsgBetweenAppNs.msg);
								System.out.println("[APP]msg write !!!!");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("[APP]App server write error! remove app list");
								// appStateList.remove(i);
								return;
							}

						} else {
							//System.out.println("[APP]App server list error!");
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}

			}
		}

	}

	class GW implements DEFINE {

		GwUpLinkThread gwUpLinkThread;
		GwDownLinkThread gwDownLinkThread;

		ArrayBlockingQueue dataQueueFromGateway;
		ArrayBlockingQueue dataQueueToGateway;
		// List gwStateList;

		DatagramSocket gwUdpSocket;

		class GwUpLinkThread extends Thread {

			DatagramPacket datagramPacketIn;
			byte[] packetInBuffer;

			public void startThread() {

				this.start();
			}

			@Override
			public void run() {

				while (true) {

					packetInBuffer = new byte[MAXIMUM_PACKET_LENGTH];
					datagramPacketIn = new DatagramPacket(packetInBuffer, packetInBuffer.length);

					try {
						gwUdpSocket.receive(datagramPacketIn);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						dataQueueFromGateway.put(datagramPacketIn);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}

		class GwDownLinkThread extends Thread implements DEFINE {

			DatagramPacket datagramPacketOut;

			public void startThread() {
				this.start();
			}

			@Override
			public void run() {

				while (true) {
					try {
						datagramPacketOut = (DatagramPacket) dataQueueToGateway.take();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// check if datagram is pull response & txpk and increase
					// fcntdown
					byte[] tempDatagramPacket = datagramPacketOut.getData();
					JSONObject tempJsonObject = null;
					JSONObject txpkJsonObject = null;
					String jsonStringDataInTxpk;
					byte[] jsonByteDataInTxpk = null;
					if (tempDatagramPacket[3] == PULL_RESPONSE) {

						byte[] tempJsonObjectByte = new byte[tempDatagramPacket.length - 4];
						System.arraycopy(tempDatagramPacket, 4, tempJsonObjectByte, 0, tempJsonObjectByte.length);
						String tempJsonObjectString = new String(tempJsonObjectByte);

						try {
							tempJsonObject = new JSONObject(tempJsonObjectString);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						try {
							txpkJsonObject = tempJsonObject.getJSONObject("txpk");
							tempJsonObject.remove("txpk");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							jsonStringDataInTxpk = (String) txpkJsonObject.get("data");
							txpkJsonObject.remove("data");
							jsonByteDataInTxpk = jsonStringDataInTxpk.getBytes();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						byte[] tempDevAddr = new byte[4];
						DevState tempDevState;
						System.arraycopy(jsonByteDataInTxpk, 1, tempDevAddr, 0, 4);

						for (int i = 0; i < devStateList.size(); i++) {
							tempDevState = (DevState) devStateList.get(i);
							if (Arrays.equals(tempDevAddr, tempDevState.devAddr)) {
								tempDevState.fcntdown++;

								jsonByteDataInTxpk[7] = (byte) (tempDevState.fcntdown & 0xff);
								jsonByteDataInTxpk[6] = (byte) ((tempDevState.fcntdown >> 8) & 0xff);

								// jsonobject packing

								try {
									txpkJsonObject.put("data", jsonByteDataInTxpk);
									tempJsonObject.put("txpk", txpkJsonObject);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						}

					}

					try {
						gwUdpSocket.send(datagramPacketOut);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}

		GW() {

			gwStateList = new ArrayList<GwState>();
			dataQueueFromGateway = new ArrayBlockingQueue<DatagramPacket>(MAXIMUM_QUEUE_LENGTH);
			dataQueueToGateway = new ArrayBlockingQueue<DatagramPacket>(MAXIMUM_QUEUE_LENGTH);

			try {
				gwUdpSocket = new DatagramSocket(GW_UDP_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			gwUpLinkThread = new GwUpLinkThread();
			gwDownLinkThread = new GwDownLinkThread();

		}

		public void startThread() {
			gwUpLinkThread.start();
			gwDownLinkThread.start();

		}

	}

	class NsMsgFromGwHandler extends Thread implements DEFINE {

		int appEuiSeq = 0;

		public void startThread() {
			this.start();
		}

		public void addUpdateDev(JSONObject tempJsonObject, byte[] packetBuffer, byte[] decriptedBase64Data, DEV dev,
				GW gw, DatagramPacket datagramPacket) {

			byte[] tempDevAddrFromMsg = new byte[4];
			DevState tempDevState;
			

			GwState tempGwstate;
			double lati = 0;
			double longi = 0;

			for (int i = 0; i < gwStateList.size(); i++) {

				tempGwstate = (GwState) gwStateList.get(i);
				if (tempGwstate.ipAddr.equals(datagramPacket.getAddress().getAddress())) {
					lati = tempGwstate.lati;
					longi = tempGwstate.longi;
				}

			}
	
			System.arraycopy(decriptedBase64Data, 1, tempDevAddrFromMsg, 0, 4);
			if (devStateList.size() != 0) {

				// find dev and update

				// for() if found break

				for (int i = 0; i < devStateList.size(); i++) {

					tempDevState = (DevState) devStateList.get(i);

					if (Arrays.equals(tempDevAddrFromMsg, tempDevState.devAddr)) {

						System.out.println("found same dev");

						try {
							tempDevState.bestGw.rssi = tempJsonObject.getInt("rssi");
							tempDevState.bestGw.ipAddr = datagramPacket.getAddress().getHostAddress();
							tempDevState.bestGw.port = datagramPacket.getPort();
							System.arraycopy(packetBuffer, 4, tempDevState.bestGw.gwEui, 0, 8);
							tempDevState.codr = tempJsonObject.getString("codr");
							tempDevState.datr = tempJsonObject.getString("datr");
							tempDevState.freq = tempJsonObject.getDouble("freq");
							tempDevState.tmst = tempJsonObject.getLong("tmst");
							tempDevState.bestGw.lati = lati;
							tempDevState.bestGw.longi = longi;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						devStateList.add(tempDevState);
						devStateList.remove(i);

						return;
					}
				}
			}

			tempDevState = new DevState();

			System.arraycopy(tempDevAddrFromMsg, 0, tempDevState.devAddr, 0, 4);

			try {
				tempDevState.bestGw.rssi = tempJsonObject.getInt("rssi");
				tempDevState.bestGw.ipAddr = datagramPacket.getAddress().getHostAddress();
				tempDevState.bestGw.port = datagramPacket.getPort();
				System.arraycopy(packetBuffer, 4, tempDevState.bestGw.gwEui, 0, 8);
				tempDevState.codr = tempJsonObject.getString("codr");
				tempDevState.datr = tempJsonObject.getString("datr");
				tempDevState.freq = tempJsonObject.getLong("freq");
				tempDevState.tmst = tempJsonObject.getLong("tmst");
				tempDevState.appEui = new byte[8];
				tempDevState.appEui[0] = 0;//(byte) (appEuiSeq++ % 2);
				tempDevState.appEui[1] = 0;
				tempDevState.appEui[2] = 0;
				tempDevState.appEui[3] = 0;
				tempDevState.appEui[4] = 0;
				tempDevState.appEui[5] = 0;
				tempDevState.appEui[6] = 0;
				tempDevState.appEui[7] = (byte) (appEuiSeq++ % 2);

				devStateList.add(tempDevState);

				DevState devtest = (DevState) devStateList.get(0);

				System.out.println("");
				System.out.println("[NS]add new dev - ");
				for (int h = 0; h < 8; h++) {
					System.out.print(tempDevState.appEui[h] + ",");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void addUpdateGw(JSONObject jsonObject, DatagramPacket datagramPacket, GW gw) {

			byte[] packetBuffer = datagramPacket.getData();
			GwState tempGwState = new GwState();

			if (gwStateList.size() != 0) {

				for (int i = 0; i < gwStateList.size(); i++) {

					tempGwState = (GwState) gwStateList.get(i);

					if (tempGwState.ipAddr.equals(datagramPacket.getAddress().toString().split("/")[1])) {

						System.out.println("found same gw");
						System.arraycopy(packetBuffer, 4, tempGwState.gwEui, 0, 8);
						try {
							tempGwState.rxnb = jsonObject.getInt("rxnb");
							tempGwState.rxok = jsonObject.getInt("rxok");
							tempGwState.rxfw = jsonObject.getInt("rxfw");
							tempGwState.ackr = jsonObject.getInt("ackr");
							tempGwState.dwnb = jsonObject.getInt("dwnb");
							tempGwState.txnb = jsonObject.getInt("txnb");
							// tempGwState.longi = jsonObject.getDouble("long");
							// tempGwState.lati = jsonObject.getDouble("lati");
							tempGwState.updateCount++;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						try {
							tempGwState.longi = jsonObject.getDouble("long");
							tempGwState.lati = jsonObject.getDouble("lati");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						}

						gwStateList.add(tempGwState);
						gwStateList.remove(i);
						return;
					}
				}

			}
			/*
			 * System.arraycopy(packetBuffer, 4, tempGwState.gwEui, 0, 8);
			 * System.out.println("add new gw"); try { tempGwState.rxnb =
			 * jsonObject.getInt("rxnb"); tempGwState.rxok =
			 * jsonObject.getInt("rxok"); tempGwState.rxfw =
			 * jsonObject.getInt("rxfw"); tempGwState.ackr =
			 * jsonObject.getInt("ackr"); tempGwState.dwnb =
			 * jsonObject.getInt("dwnb"); tempGwState.txnb =
			 * jsonObject.getInt("txnb"); } catch (JSONException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * tempGwState.ipAddr =
			 * datagramPacket.getAddress().getHostAddress(); //
			 * tempGwState.downLinkMsg = new ArrayList<byte[]>();
			 * gwStateList.add(tempGwState);
			 */

		}

		@Override
		public void run() {
			int count = 0;
			DatagramPacket datagramPacket = null;
			byte[] packetBuffer;

			while (true) {

				System.out.println("gwstatelistsize :" + gwStateList.size());
				System.out.println("devstatelistsize :" + devStateList.size());			
				System.out.println("appstatelistsize :" + appStateList.size());
				/*
				 * System.out.println("gwstatelistsize :" + gwStateList.size());
				 * for (int i = 0; i < gwStateList.size(); i++) { GwState
				 * tempState = (GwState) gwStateList.get(i); System.out.println(
				 * "ip - " + tempState.ipAddr);
				 * 
				 * System.out.print("gwEUI - "); for (int j = 0; j < 8; j++) {
				 * System.out.print((int) (tempState.gwEui[j] & 0xff) + " "); }
				 * System.out.println("");
				 * 
				 * System.out.println("ackr : " + tempState.ackr);
				 * System.out.println("alti : " + tempState.alti);
				 * System.out.println("dwnb : " + tempState.dwnb);
				 * System.out.println("rxfw : " + tempState.rxfw);
				 * System.out.println("upcount : " + tempState.updateCount);
				 * System.out.println("port : " + tempState.port);
				 * System.out.println(""); System.out.println(""); }
				 */
				/*
				 * System.out.println("devstatelistsize :" +
				 * devStateList.size()); for (int i = 0; i <
				 * devStateList.size(); i++) { DevState tempState = (DevState)
				 * devStateList.get(i);
				 * 
				 * System.out.print("devAddr - "); for (int j = 0; j < 4; j++) {
				 * System.out.print((int) (tempState.devAddr[j] & 0xff) + " ");
				 * }
				 * 
				 * System.out.println(""); System.out.println("codr : " +
				 * tempState.codr); System.out.println("datr : " +
				 * tempState.datr); System.out.println("fport : " +
				 * tempState.fport); System.out.println("freq : " +
				 * tempState.freq); System.out.println("");
				 * System.out.println("");
				 * 
				 * }
				 */

				try {
					datagramPacket = (DatagramPacket) gw.dataQueueFromGateway.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				packetBuffer = datagramPacket.getData();

				switch (packetBuffer[3]) {

				case PUSH_DATA: {

					System.out.println("PUSH_DATA received");

					byte[] pushAckMsg = new byte[4];
					pushAckMsg[0] = 1;
					pushAckMsg[1] = packetBuffer[1];
					pushAckMsg[2] = packetBuffer[2];
					pushAckMsg[3] = 1;

					DatagramPacket pushAckDatagram = new DatagramPacket(pushAckMsg, pushAckMsg.length,
							datagramPacket.getAddress(), datagramPacket.getPort());

					try {
						gw.dataQueueToGateway.put(pushAckDatagram);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					// copy JSON object from PUSH_DATA message to
					// JsonFromPacketBuffer

					byte[] jsonByteFromPacketBuffer = new byte[datagramPacket.getLength()];
					String jsonStringFromPacketBuffer = null;
					JSONArray jsonArray;
					JSONObject jsonObject = null;
					byte[] decriptedBase64Data = null;

					System.arraycopy(packetBuffer, 12, jsonByteFromPacketBuffer, 0, datagramPacket.getLength() - 12);
					jsonStringFromPacketBuffer = new String(jsonByteFromPacketBuffer);
					try {
						jsonObject = new JSONObject(jsonStringFromPacketBuffer);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					System.out.println(jsonStringFromPacketBuffer);

					//////////////////////////// rxpk handling

					try {
						jsonArray = (JSONArray) jsonObject.getJSONArray("rxpk");

						for (int i = 0; i < jsonArray.length(); i++) {

							JSONObject tempJsonObject = (JSONObject) jsonArray.get(i);

							try {
								decriptedBase64Data = Base64.decodeBase64(tempJsonObject.getString("data").getBytes());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.out.println("--descripted base64 data--");
							for (int j = 0; j < decriptedBase64Data.length; j++) {
								System.out.print((int) (decriptedBase64Data[j] & 0xff) + " ");
							}
							System.out.println(" ");


							switch (decriptedBase64Data[0] & 0xff) {

							case JOIN_REQUEST: {
								System.out.println("JOIN_REQUEST");
								
								byte[] tempDevEui = new byte[8];
								System.arraycopy(decriptedBase64Data, 8, tempDevEui, 0, 8);
								
								for(int j=0; j< devStateList.size(); j++){
									DevState tempDevState = (DevState) devStateList.get(j);
									
									if(Arrays.equals(tempDevState.devEui, tempDevEui)){
										
										System.arraycopy(decriptedBase64Data , 16, tempDevState.devNonce, 0, 2);
										System.arraycopy(decriptedBase64Data , 0, tempDevState.devNonce, 0, 8);
										tempDevState.connected = true;
										tempDevState.devAddr[3] = (byte) j;
										System.arraycopy(decriptedBase64Data, 5, tempDevState.appNonce, 0, 3);
										System.arraycopy(decriptedBase64Data, 1, tempDevState.appEui, 0, 8);
										try {
											tempDevState.bestGw.rssi = tempJsonObject.getInt("rssi");
											tempDevState.bestGw.ipAddr = datagramPacket.getAddress().getHostAddress();
											tempDevState.bestGw.port = datagramPacket.getPort();
											System.arraycopy(packetBuffer, 4, tempDevState.bestGw.gwEui, 0, 8);
											tempDevState.codr = tempJsonObject.getString("codr");
											tempDevState.datr = tempJsonObject.getString("datr");
											tempDevState.freq = tempJsonObject.getLong("freq");
											tempDevState.tmst = tempJsonObject.getLong("tmst");

											devStateList.add(tempDevState);

											System.out.println("");
											System.out.println("[NS]add new dev - ");
											for (int h = 0; h < 8; h++) {
												System.out.print(tempDevState.appEui[h] + ",");
											}

										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										JSONObject upComponentJsonObject = new JSONObject();
										JSONObject upJsonObject = new JSONObject();
										DatagramPacket joinAcceptDatagramUpPacket;
										byte[] tempData = new byte[1+3+3+4+1+1];	// MHDR + AppNonce + NetId + DevAddr + DLSettings + RxDelay
										
										tempData[0] = JOIN_ACCEPT;
										System.arraycopy(tempDevState.appNonce, 0 , tempData, 1, 3);
										System.arraycopy(tempDevState.devAddr,0,tempData,1+3+3,4);
										
										
										
										
										
										for (int k = 0; k < gwStateList.size(); k++) {

											GwState tempGwState;
											upComponentJsonObject = new JSONObject();
											byte[] joinAcceptMsg; //= new byte[4 + upJsonObject.toString().length()];

											tempGwState = (GwState) gwStateList.get(k);

											if (Arrays.equals(tempDevState.bestGw.gwEui, tempGwState.gwEui)) {
												
												long tempTmst = tempJsonObject.getLong("tmst") + 1000000;
												upComponentJsonObject.put("imme", false);
												upComponentJsonObject.put("tmst", tempTmst);
												upComponentJsonObject.put("freq", tempJsonObject.getDouble("freq"));
												upComponentJsonObject.put("rfch", 0);
												upComponentJsonObject.put("modu", tempJsonObject.getString("modu"));
												upComponentJsonObject.put("datr", tempJsonObject.getString("datr"));
												upComponentJsonObject.put("codr", tempJsonObject.getString("codr"));
												upComponentJsonObject.put("ipol", true);

												byte[] encriptedBase64Data = Base64.encodeBase64(tempData);

												String encriptedBase64DatabyteToString = new String(encriptedBase64Data, 0,
														encriptedBase64Data.length);

												upComponentJsonObject.put("data", encriptedBase64DatabyteToString);
												upComponentJsonObject.put("size", decriptedBase64Data.length);

												upJsonObject.put("txpk", upComponentJsonObject);
												
												joinAcceptMsg = new byte[4 + upJsonObject.toString().length()];
												joinAcceptDatagramUpPacket = new DatagramPacket(joinAcceptMsg,
														joinAcceptMsg.length, datagramPacket.getAddress(),
														tempGwState.port);


												try {
													gw.dataQueueToGateway.put(joinAcceptDatagramUpPacket);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}	
										
									}
									
								}
								
								
								break;
							}

							case UNCONFIRMED_DATA_UP: {
								addUpdateDev(tempJsonObject, packetBuffer, decriptedBase64Data, dev, gw, datagramPacket);
								System.out.println("UNCONFIRMED_DATA_UP");
								byte devSequence = 0;
								int gwSequence = 0;
								for (int j = 0; j < devStateList.size(); j++) {
									byte[] tempDevAddrFromMsg = new byte[4];
									DevState tempDevState;
									System.arraycopy(decriptedBase64Data, 1, tempDevAddrFromMsg, 0, 4);
									tempDevState = (DevState) devStateList.get(j);

									DatagramPacket unconfirmedDatagramUpPacket;

									JSONObject upJsonObject;
									JSONObject upComponentJsonObject;

									if (Arrays.equals(tempDevAddrFromMsg, tempDevState.devAddr)) {

										System.out.println("found same dev");
										devSequence = (byte) j;

										System.out.println("tmst 1 " + tempJsonObject.getLong("tmst"));
										long tempTmst = tempJsonObject.getLong("tmst") + 1000000;
										upJsonObject = new JSONObject();
										upComponentJsonObject = new JSONObject();

										upComponentJsonObject.put("imme", false);
										upComponentJsonObject.put("tmst", tempTmst);
										upComponentJsonObject.put("freq", tempJsonObject.getDouble("freq"));
										upComponentJsonObject.put("rfch", 0);
										upComponentJsonObject.put("modu", tempJsonObject.getString("modu"));
										upComponentJsonObject.put("datr", tempJsonObject.getString("datr"));
										upComponentJsonObject.put("codr", tempJsonObject.getString("codr"));
										upComponentJsonObject.put("ipol", true);

										// System.out.println("@@@@@@@@@@@@@@@debug1");

										byte[] tempData = new byte[decriptedBase64Data.length];

										System.arraycopy(decriptedBase64Data, 0, tempData, 0,
												decriptedBase64Data.length);

										byte[] encriptedBase64Data = null;
										tempData[0] = (byte) UNCONFIRMED_DATA_DOWN;

										System.out.println("--descripted base64 data--");
										for (int l = 0; l < tempData.length; l++) {
											System.out.print((int) (tempData[l] & 0xff) + " ");
										}
										System.out.println(" ");

										encriptedBase64Data = Base64.encodeBase64(tempData);

										String encriptedBase64DatabyteToString = new String(encriptedBase64Data, 0,
												encriptedBase64Data.length);

										upComponentJsonObject.put("data", encriptedBase64DatabyteToString);
										upComponentJsonObject.put("size", decriptedBase64Data.length);

										upJsonObject.putOpt("txpk", upComponentJsonObject);
										System.out.println("upjson data : " + upJsonObject);

										byte[] upJsonObjetTobyte = upJsonObject.toString().getBytes();
										byte[] pullRespMsg = new byte[4 + upJsonObject.toString().length()];
										pullRespMsg[0] = 1;
										pullRespMsg[1] = 0;
										pullRespMsg[2] = 0;
										pullRespMsg[3] = 3; // pull response
										System.arraycopy(upJsonObjetTobyte, 0, pullRespMsg, 4,
												upJsonObjetTobyte.length);

										for (int k = 0; k < gwStateList.size(); k++) {

											GwState tempGwState;

											tempGwState = (GwState) gwStateList.get(k);

											if (Arrays.equals(tempDevState.bestGw.gwEui, tempGwState.gwEui)) {

												unconfirmedDatagramUpPacket = new DatagramPacket(pullRespMsg,
														pullRespMsg.length, datagramPacket.getAddress(),
														tempGwState.port);

												gwSequence = k;

												try {
													gw.dataQueueToGateway.put(unconfirmedDatagramUpPacket);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}

									}
								}

								// put datamsg in the app queue. so find same
								// app eui
								byte foptlen;
								byte datalen;
								byte[] data;
								byte fport;
								foptlen = (byte) (decriptedBase64Data[5] & 7);
								fport = decriptedBase64Data[8 + foptlen];
								datalen = (byte) (decriptedBase64Data.length - (8 + foptlen + 1));
								data = new byte[datalen];

								System.arraycopy(decriptedBase64Data, 8 + foptlen + 1, data, 0, datalen - 4);

								// String dataString = new String(data);

								float sensorData = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();

								JSONObject jsonObjectData = new JSONObject();

								UpMsgAppData upMsgAppData = new UpMsgAppData();
								jsonObjectData.put("fport", fport);
								jsonObjectData.put("data", sensorData);

								if (gwStateList.size() == 0) {
									System.out.println("gwSequence is 0");
									break;
								}

								GwState tempGwState = (GwState) gwStateList.get(gwSequence);

								jsonObjectData.put("long", tempGwState.longi);
								jsonObjectData.put("lati", tempGwState.lati);
								jsonObjectData.put("gwEui", tempGwState.gwEui);

								System.arraycopy(decriptedBase64Data, 1, upMsgAppData.devAddr, 0, 4);
								upMsgAppData.jsonObjectData = jsonObjectData;

								System.out.println("upMsgAppData json " + upMsgAppData.jsonObjectData.toString());

								for (int j = 0; j < appStateList.size(); j++) {

									AppState tempAppState;
									DevState tempDevState;

									tempAppState = (AppState) appStateList.get(j);
									tempDevState = (DevState) devStateList.get(devSequence);
									
									System.out.println("");
									System.out.println(" appEUI compare !!!!"+tempDevState.appEui[7]+","+tempAppState.appEui[7]);
								
									for(int m=0;m<8;m++){
										System.out.print(tempDevState.appEui[m]+",");
									}
									System.out.println("");
									for(int l=0;l<8;l++){
										System.out.print(tempAppState.appEui[l]+",");
									}
									
									System.out.println("");
									
									if (Arrays.equals(tempDevState.appEui, tempAppState.appEui)) {
										try {
											System.out.println("@@@@put APP_DATAUP@@@@@");
											tempAppState.upLinkMsg.put(upMsgAppData);

										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

								}

								break;
							}
							
							case CONFIRMED_DATA_UP: {
								System.out.println("CONFIRMED_DATA_UP");
								
							}
							default: {
								System.out.println("rxpk No Match");
								break;
							}

							}

						}

					} catch (JSONException e) {
						System.out.println(" no rxpk in PUSH_DATA");
					}

					//////////////////////////// stat handling

					try {
						jsonObject = (JSONObject) jsonObject.get("stat");
						addUpdateGw(jsonObject, datagramPacket, gw);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}

					break;

				}

				case PULL_DATA: {
					System.out.println("PULL_DATA received");

					for (int j = 0; j < gwStateList.size(); j++) {

						byte[] tempGwEuiFromMsg = new byte[8];
						GwState tempGwState;
						System.arraycopy(packetBuffer, 4, tempGwEuiFromMsg, 0, 8);
						tempGwState = (GwState) gwStateList.get(j);

						if (Arrays.equals(tempGwEuiFromMsg, tempGwState.gwEui)) {

							tempGwState.port = datagramPacket.getPort();
							gwStateList.add(tempGwState);
							gwStateList.remove(j);
						}
					}

					byte[] pushPullMsg = new byte[12];
					pushPullMsg[0] = 1;
					pushPullMsg[1] = packetBuffer[1];
					pushPullMsg[2] = packetBuffer[2];
					pushPullMsg[3] = 4;
					System.arraycopy(packetBuffer, 4, pushPullMsg, 4, 8);

					DatagramPacket pushPullDatagram = new DatagramPacket(pushPullMsg, pushPullMsg.length,
							datagramPacket.getAddress(), datagramPacket.getPort());

					try {
						gw.dataQueueToGateway.put(pushPullDatagram);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					break;
				}

				}

			}
		}

	}

	NS(String managerAddr) {
		gw = new GW();
		dev = new DEV();
		app = new APP();
		manager = new MANAGER(managerAddr);

		nsMsgFromGwHandler = new NsMsgFromGwHandler();
	}

	public void startServer() {

		gw.startThread();
		app.startThread();
		manager.startThread();
		nsMsgFromGwHandler.startThread();

	}

	public static void main(String[] args) {

		NS server = new NS(args[0]);
		System.out.println("[main] manager IP :" + args[0]);
		server.startServer(); // NS thread start

	}

	class MsgHandlerThread extends Thread {

	}

}
