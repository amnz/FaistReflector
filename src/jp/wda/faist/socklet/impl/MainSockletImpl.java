/* *****************************************************************************
 *  Copyright (c) 2003 EasyCom:-P! Co.,Ltd. All Rights Reserved.
 *
 *  EasyCom:-P! MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EasyCom:-P! SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * *****************************************************************************
 */

package jp.wda.faist.socklet.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.wda.faist.FaistService;
import jp.wda.faist.jservice.InProcessServiceKernel;
import jp.wda.faist.jservice.InProcessService;
import jp.wda.faist.jservice.InProcessServiceContainer;
import jp.wda.faist.socklet.ClientInfomation;
import jp.wda.faist.socklet.MainSocklet;
import jp.wda.g2.ConnectingConditions;
import jp.wda.g2.GeneralSocklet;
import jp.wda.g2.SocketProcessor;
import jp.wda.g2.SockletRequest;
import jp.wda.g2.exception.GPSSException;
import jp.wda.g2.util.SimpleXMLCreator;
import jp.wda.gpss.util.Logger;
import jp.wda.gpss.util.PseudoUUID;

/**
 *
 *
 *
 * $Id: MainSockletImpl.java,v 0:fb0c1025e23b 2012/05/30 07:58:23 amnz $
 * @author		$Author: amnz $
 * @revision	$Revision: 0:fb0c1025e23b $
 * @date		$Date: Wed, 30 May 2012 16:58:23 +0900 $
 */
public class MainSockletImpl extends GeneralSocklet implements MainSocklet {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public MainSockletImpl() {
		super();
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** システムロガー */
	protected final Logger log = Logger.getLogger("jp.wda.faist.systemlog");

	private ConcurrentHashMap<String, FaistService> services = new ConcurrentHashMap<String, FaistService>();

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private String jserviceEncoding = "UTF-8";
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setJserviceEncoding(String s){ jserviceEncoding = s; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	public boolean accept(SockletRequest request){
		SocketProcessor client = request.getClient();
		ClientInfomation info = new ClientInfomation(client);
		info.setHardwareID(getHardwareID(client, request.getAllClients()));

		client.send("+OK welcome::" + info.getHardwareID() + "::" + info.getDHPublicValue());
		log.info("welcome " + client.getIPAddress() + "[HID:" + info.getHardwareID() + "]");

		return true;
	}

	/** {@inheritDoc} */
	public boolean denied(SockletRequest request){
		request.send("-Good bye...");
		return false;
	}

	/** {@inheritDoc} */
	public void desert(SockletRequest request){
		SocketProcessor client = request.getClient();
		ClientInfomation info = (ClientInfomation)client.getAttributes();
		FaistService service = services.get(info.getServiceName());
		if(service != null) {
			if(info.isService()) {
				// サービス終了処理
				log.debug("desert " + info.getServiceName() + " service.");
				service.setProcessor(null);

				// 全クライアント切断
				List<SocketProcessor> clients = new ArrayList<SocketProcessor>();
				for(SocketProcessor c : service.getClients()) { clients.add(c); }
				for(SocketProcessor c : clients) { c.terminate(); }
			} else {
				// クライアント終了処理
				if(service.getProcessor() != null) {
					service.getProcessor().send("<desert clientID=\"" + client.getClientID() + "\"/>");
				}
				service.getClients().remove(client);
			}
		}

		log.info("Good bye..." + request.getClientID());
	}

	/** {@inheritDoc} */
	public void destroy(){
	}

	// コマンド処理メソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	public Object doCommand(SockletRequest request) throws GPSSException{
		SocketProcessor client = request.getClient();
		ClientInfomation info = (ClientInfomation)client.getAttributes();
		SimpleXMLCreator xml = SimpleXMLCreator.parse(request.getCommand());

		if("launch".equals(xml.getName()))		{ return launch(client, xml); }
		if("connect".equals(xml.getName()))		{ return connect(client, xml); }

		if(info.isService()) {
			if("accept".equals(xml.getName()))		{ return acceptClient(client, xml); }
			if("deny".equals(xml.getName()))		{ return denyClient(client, xml); }
			if("attribute".equals(xml.getName()))	{ return setAttribute(client, xml); }
			if("invoke".equals(xml.getName()))		{ return reflectFromService(client, xml); }
		} else {
			if("invoke".equals(xml.getName()))		{ return reflectToService(client, xml); }
		}

		client.send("<result code=\"-1\" message=\"unknown command.\"/>");
		return null;
	}

	/**
	 *
	 * @param client
	 * @param xml
	 * @return
	 * @throws GPSSException
	 */
	private Object launch(SocketProcessor client, SimpleXMLCreator xml) throws GPSSException{
		ClientInfomation info = (ClientInfomation)client.getAttributes();
		info.setServiceName((String)xml.getAttribute("service"));
		info.setService(true);
		String password = info.decrypt((String)xml.getAttribute("password"));

		FaistService service = null;
		synchronized (services) {
			service = services.get(info.getServiceName());
			if(service != null) {
				if(service.getProcessor() != null) {
					client.send("<result code=\"1004\" message=\"service is already launched.\"/>");
					return null;
				}
				if(!service.getPassword().equals(password)) {
					client.send("<result code=\"1001\" message=\"service name is already used.\"/>");
					return null;
				}
			} else {
				service = new FaistService();
				service.setName(info.getServiceName());
				service.setPassword(password);
				services.put(info.getServiceName(), service);
			}
			service.setProcessor(client);
		}

		log.debug("launch " + info.getServiceName() + " service.:" + client.getClientID());

		SimpleXMLCreator result = new SimpleXMLCreator("result");
		result.setAttribute("code", 0);

		SimpleXMLCreator clientsNode = result.addChild("clients");
		for(SocketProcessor c : service.getClients()) {
			SimpleXMLCreator clientNode = clientsNode.addChild("client");
			clientNode.setAttribute("clientID", c.getClientID());

			SimpleXMLCreator attributesNode = clientNode.addChild("attributes");
			Map<String, String> attributes = ((ClientInfomation)c.getAttributes()).getAttributes();
			for(String key : attributes.keySet()) {
				SimpleXMLCreator attr = attributesNode.addChild("attr");
				attr.setAttribute("key", key);
				attr.setText(attributes.get(key));
			}
		}

		client.send(result.toString());
		return null;
	}

	/**
	 * Javaインプロセスサービスを登録します。
	 * S2コンテナから使用してください。
	 *
	 * @param name
	 * @param password
	 * @param service
	 */
	public void registerService(String name, String password, InProcessService service) {
		InProcessServiceContainer container = new InProcessServiceContainer();
		InProcessServiceKernel    kernel    = new InProcessServiceKernel(this, service);
		kernel.setEncoding(jserviceEncoding);

		ClientInfomation info = new ClientInfomation(kernel);
		info.setServiceName(name);
		info.setService(true);
		try {
			info.setHardwareID(new PseudoUUID(kernel.getIPAddress()).toString());
		}catch(IOException e){
			log.error("Javaサービス用ハードウェアID作成に失敗しました。サービス名=" + name, e);
			return;
		}

		container.setProcessor(kernel);
		container.setName(name);
		container.setPassword(password);

		services.put(name, container);
	}

	/**
	 *
	 * @param client
	 * @param xml
	 * @return
	 * @throws GPSSException
	 */
	private Object connect(SocketProcessor client, SimpleXMLCreator xml) throws GPSSException{
		ClientInfomation info = (ClientInfomation)client.getAttributes();
		info.setServiceName((String)xml.getAttribute("service"));
		info.setService(false);

		if(!services.containsKey(info.getServiceName())) {
			client.send("<result code=\"1002\" message=\"service is not found.\"/>");
			return null;
		}
		String password = info.decrypt((String)xml.getAttribute("password"));
		FaistService service = services.get(info.getServiceName());
		if(service.getProcessor() == null) {
			client.send("<result code=\"1003\" message=\"service down.\"/>");
			return null;
		}
		if(!service.getPassword().equals(password)) {
			client.send("<result code=\"3\" message=\"access denied.\"/>");
			return null;
		}

		log.debug("connect to " + info.getServiceName() + " service. :" + client.getClientID());
		service.getClients().add(client);
		service.getProcessor().send("<screen ipAddress=\"" + client.getIPAddress() + "\" clientID=\"" + client.getClientID() + "\" hwid=\"" + info.getHardwareID() + "\"/>");
		return null;
	}
	/**
	 *
	 * @param serviceSocket
	 * @param xml
	 * @return
	 * @throws GPSSException
	 */
	public Object acceptClient(SocketProcessor serviceSocket, SimpleXMLCreator xml) throws GPSSException{
		ClientInfomation info = (ClientInfomation)serviceSocket.getAttributes();
		FaistService service = services.get(info.getServiceName());
		if(service == null || service.getProcessor() == null) { throw new GPSSException(); }

		SocketProcessor client = service.getClientByID((String)xml.getAttribute("clientID"));
		if(client == null) { return null; }

		client.send("<result code=\"0\"/>");
		return null;
	}
	/**
	 *
	 * @param client
	 * @param xml
	 * @return
	 * @throws GPSSException
	 */
	public Object denyClient(SocketProcessor serviceSocket, SimpleXMLCreator xml) throws GPSSException{
		ClientInfomation info = (ClientInfomation)serviceSocket.getAttributes();
		FaistService service = services.get(info.getServiceName());
		if(service == null || service.getProcessor() == null) { throw new GPSSException(); }

		SocketProcessor client = service.getClientByID((String)xml.getAttribute("clientID"));
		if(client == null) { return null; }

		client.terminate();
		return null;
	}

	/**
	 *
	 * @param serviceSocket
	 * @param xml
	 * @return
	 */
	public Object setAttribute(SocketProcessor serviceSocket, SimpleXMLCreator xml)  throws GPSSException {
		SocketProcessor target = getClient(serviceSocket, (String)xml.getAttribute("clientID"));
		if(target == null) { return null; }

		((ClientInfomation)target.getAttributes()).getAttributes().put((String)xml.getAttribute("key"), xml.getText());

		return null;
	}

	/**
	 *
	 * @param serviceSocket
	 * @param clientID
	 * @return
	 */
	private SocketProcessor getClient(SocketProcessor serviceSocket, String clientID) {
		if(clientID == null || clientID.length() == 0) { return null; }

		ClientInfomation info = (ClientInfomation)serviceSocket.getAttributes();
		FaistService service = services.get(info.getServiceName());
		if(service == null) { return null; }

		for(SocketProcessor c : service.getClients()) {
			if(clientID.equals(c.getClientID())) { return c; }
		}
		return null;
	}

	/**
	 * サービスからのメソッド実行命令をクライアントへ転送します。
	 *
	 * @param client
	 * @param xml
	 * @return
	 * @throws GPSSException
	 */
	public Object reflectFromService(SocketProcessor client, SimpleXMLCreator xml) throws GPSSException {
		ClientInfomation info = (ClientInfomation)client.getAttributes();
		FaistService service = services.get(info.getServiceName());
		if(service == null) { throw new GPSSException(); }

		List<SocketProcessor> clients = service.selectClient(xml);
		for(SocketProcessor c : clients) { c.send(xml.toString()); }

		return null;
	}

	/**
	 * クライアントからのメソッド実行命令をサービスへ転送します。
	 *
	 * @param client
	 * @param xml
	 * @return
	 * @throws GPSSException
	 */
	private Object reflectToService(SocketProcessor client, SimpleXMLCreator xml) throws GPSSException {
		ClientInfomation info = (ClientInfomation)client.getAttributes();
		FaistService service = services.get(info.getServiceName());
		if(service == null) { throw new GPSSException(); }
		if(service.getProcessor() == null) { return null; }

		SimpleXMLCreator result = new SimpleXMLCreator(xml.getName());
		result.setAttribute("method",  xml.getAttribute("method"));
		result.setAttribute("handler", xml.getAttribute("handler"));
		SimpleXMLCreator params = result.addChild("params");

		SimpleXMLCreator originalParams = xml.getChild("params");
		if(originalParams != null) {
			List<SimpleXMLCreator> param = originalParams.getChildren("param");
			for(SimpleXMLCreator x : param) {
				params.addChild(x);
				if("client".equals(x.getAttribute("type"))) {
					x.setText(client.getClientID());
				} else if("encrypted".equals(x.getAttribute("type"))) {
					String value = info.decrypt(x.getText());
					x.setAttribute("type", "string");
					x.setText(value);
				}
			}
		}

		service.getProcessor().send(result.toString());
		return null;
	}

	// 内部メソッド /////////////////////////////////////////////////////////////////////
	//                                                                 Private Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * クライアントのハードウェアIDを検査し、多重ログインを防止します。
	 *
	 * @param client ハードウェアIDを検査するクライアント
	 * @param socklets
	 * @return ハードウェアID
	 */
	private synchronized String getHardwareID(SocketProcessor client, SocketProcessor[] allClients){
		ConnectingConditions cond = client.getConnectingConditions();
		String hwid = cond.getParameter("hid");
		if(hwid == null){
			try{
				return new PseudoUUID(client.getIPAddress()).toString();
			}catch(IOException e){
				log.error("ハードウェアID作成に失敗しました。IP=" + client.getIPAddress(), e);
				return client.getClientID();
			}
		}

		return hwid;
	}

}
