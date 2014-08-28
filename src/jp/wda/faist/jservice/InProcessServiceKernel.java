/* *****************************************************************************
 *  Copyright (c) 2012 Movatoss Co.,Ltd. All Rights Reserved.
 *
 *  Movatoss Co.,Ltd. MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 *  OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EasyCom:-P! SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * *****************************************************************************
 */

package jp.wda.faist.jservice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wda.faist.selector.ClientSelector;
import jp.wda.faist.socklet.ClientInfomation;
import jp.wda.faist.socklet.impl.MainSockletImpl;
import jp.wda.g2.ConnectingConditions;
import jp.wda.g2.SocketProcessor;
import jp.wda.g2.exception.AccessDeniedException;
import jp.wda.g2.exception.CommandParseException;
import jp.wda.g2.util.SimpleXMLCreator;
import jp.wda.gpss.util.Logger;
import jp.wda.gpss.util.PseudoUUID;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Rev$
 * @date		$Date$
 */
public class InProcessServiceKernel implements SocketProcessor {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public InProcessServiceKernel(MainSockletImpl socklet, InProcessService service) {
		super();

		this.dhsalt = new BigInteger(512, new Random(System.currentTimeMillis()));
		this.dhpub  = ClientInfomation.DH_GENERATOR.modPow(dhsalt, ClientInfomation.DH_PRIME);
		this.connectingConditions.setPassword(dhpub.toString(16));

		this.socklet = socklet;
		this.service = service;
		this.service.kernel = this;
		this.ipAddress = "172.0.0.1";

		try {
			this.clientID = new PseudoUUID(this.ipAddress).toString();
		}catch(IOException e){
			log.error("InProcess用ダミークライアントのclientID生成に失敗しました。", e);
		}
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** XXXX */
	private BigInteger dhsalt = null;

	/** XXXX */
	private BigInteger dhpub  = null;

	/** ロガー */
	protected final Logger log = Logger.getLogger("jp.wda.faist.systemlog");

	/** XXXX */
	private InProcessService service = null;

	/** XXXX */
	private MainSockletImpl socklet = null;

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private String clientID = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	@Override
	public String getClientID(){ return clientID; }

	/* ***********************************************************************>> */
	/** XXXX */
	private String encoding = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	@Override
	public String getEncoding(){ return encoding; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	@Override
	public void setEncoding(String s){ encoding = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private String ipAddress = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	@Override
	public String getIPAddress(){ return ipAddress; }

	/* ***********************************************************************>> */
	/** XXXX */
	private long timeout = -1;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	@Override
	public long getTimeout(){ return timeout; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	@Override
	public void setTimeout(long s){ timeout = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private Object attributes = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	@Override
	public Object getAttributes(){ return attributes; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	@Override
	public void setAttributes(Object s){ attributes = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private ConnectingConditions connectingConditions = new ConnectingConditions();
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	@Override
	public ConnectingConditions getConnectingConditions(){ return connectingConditions; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	@Override
	public void setConnectingConditions(ConnectingConditions s){ connectingConditions = s; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	public void doCommand(ByteBuffer command) {

	}

	/** {@inheritDoc} */
	@Override
	public boolean send(String message) {
		SimpleXMLCreator xml;
		try {
			xml = SimpleXMLCreator.parse(message);
		} catch(Throwable ex) {
			log.error("XML解析に失敗しました。：" + message, ex);
			return false;
		}

		if("screen".equals(xml.getName())) {
			service.screen(xml);
			return true;
		}
		if("desert".equals(xml.getName())) {
			service.leave(xml);
			return true;
		}
		if("result".equals(xml.getName())) {
			service.reflectorResult(xml);
			return true;
		}
		if(!"invoke".equals(xml.getName())) {
			service.unknownMessage(xml);
			return false;
		}

		try {
			return evalute(xml);
		} catch(Throwable ex) {
			log.error("インプロセスカーネル処理中に例外が発生しました。：" + message, ex);
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void terminate() {

	}
	/** {@inheritDoc} */
	@Override
	public void terminate(String message) {

	}
	/** {@inheritDoc} */
	@Override
	public boolean isTerminated() {
		return false;
	}

	/**
	 *
	 * @param xml
	 * @return
	 * @throws IOException
	 */
	private boolean evalute(SimpleXMLCreator xml) throws IOException {
		// 引数検索
		List<Class<?>> argTypes  = new ArrayList<Class<?>>();
		List<Object>   argValues = new ArrayList<Object>();
		SimpleXMLCreator params = xml.getChild("params");
		if(params != null) {
			if(!service.parseArguments(params.getChildren("param"), argTypes, argValues)) {
				service.unknownMethod(xml, new CommandParseException("argument"));
				return false;
			}
		}

		// メソッド検索
		String methodName = (String)xml.getAttribute("method");
		Method method;

		try{
			method = service.getClass().getMethod(methodName, argTypes.toArray(new Class<?>[0]));
		}catch(NoSuchMethodException ex){
			service.unknownMethod(xml, ex);
			return false;
		}catch(SecurityException ex){
			service.unknownMethod(xml, ex);
			return false;
		}

		ServiceMethod annotation = method.getAnnotation(ServiceMethod.class);
		if(annotation == null) {
			service.unknownMethod(xml, new AccessDeniedException());
			return false;
		}

		// コマンドメソッド実行
		try{
			method.invoke(service, argValues.toArray(new Object[0]));
		}catch(Throwable ex){
			service.evaluteFailure(xml, ex);
			return false;
		}

		return true;
	}

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 指定されたIDのクライアントからの接続を受け入れます。
	 *
	 * @param clientID
	 */
	public void accept(String clientID) {
		SimpleXMLCreator xml = new SimpleXMLCreator("accept");
		xml.setAttribute("clientID", clientID);

		try {
			socklet.acceptClient(this, xml);
		} catch(Throwable ex) {
			log.error("accept failure...", ex);
		}
	}

	/**
	 * 指定されたIDのクライアントからの接続を拒否します。
	 *
	 * @param	clientID
	 */
	public void deny(String clientID) {
		SimpleXMLCreator xml = new SimpleXMLCreator("deny");
		xml.setAttribute("clientID", clientID);

		try {
			socklet.denyClient(this, xml);
		} catch(Throwable ex) {
			log.error("deny failure...", ex);
		}
	}

	/**
	 *
	 * @param	clientID
	 * @param	key
	 * @param	value
	 */
	public void setAttribute(String clientID, String key, String value) {
		SimpleXMLCreator xml = new SimpleXMLCreator("attribute");
		xml.setAttribute("clientID", clientID);
		xml.setAttribute("key", key);
		xml.setCData(value);

		try {
			socklet.setAttribute(this, xml);
		} catch(Throwable ex) {
			log.error("setAttribute failure...", ex);
		}
	}

	/**
	 *
	 * @return
	 */
	public ClientProxy client() {
		return new ClientProxy(this.socklet, this);
	}

	/**
	 *
	 * @param clientID
	 * @return
	 */
	public ClientProxy client(String clientID) {
		return new ClientProxy(this.socklet, this).id(clientID);
	}

	/**
	 *
	 * @param selector
	 * @return
	 */
	public ClientProxy client(ClientSelector selector) {
		return new ClientProxy(this.socklet, this).selector(selector);
	}

}
