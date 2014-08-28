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

import jp.wda.faist.jservice.parser.ArgumentEncoder;
import jp.wda.faist.selector.ClientIDSelector;
import jp.wda.faist.selector.ClientSelector;
import jp.wda.faist.socklet.impl.MainSockletImpl;
import jp.wda.g2.util.SimpleXMLCreator;
import jp.wda.gpss.util.Logger;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Rev$
 * @date		$Date$
 */
public class ClientProxy {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public ClientProxy(MainSockletImpl socklet, InProcessServiceKernel kernel) {
		super();

		this.socklet = socklet;
		this.kernel  = kernel;
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	protected final Logger log = Logger.getLogger("jp.wda.faist.systemlog");

	/** リフレクタsocklet */
	private MainSockletImpl socklet = null;

	/** Javaサービスカーネル */
	private InProcessServiceKernel kernel = null;

	/** クライアントセレクタ */
	private ClientSelector clientSelector = null;

	/** クライアントハンドラ名 */
	private String handlerName = "root";

	/** クライアントハンドラ名 */
	private String methodName;

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param id
	 * @return
	 */
	public ClientProxy id(String id) {
		this.clientSelector = new ClientIDSelector(id);
		return this;
	}

	/**
	 *
	 * @param selector
	 * @return
	 */
	public ClientProxy selector(ClientSelector selector) {
		this.clientSelector = selector;
		return this;
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public ClientProxy handler(String name) {
		this.handlerName = name;
		return this;
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public ClientProxy method(String name) {
		this.methodName = name;
		return this;
	}


	/**
	 *
	 * @param args
	 */
	public void invoke(Object... args) {
		SimpleXMLCreator result = new SimpleXMLCreator("invoke");
		result.setAttribute("method",  this.methodName);
		result.setAttribute("handler", this.handlerName);

		if(this.clientSelector != null) {
			this.clientSelector.appendCondition(result);
		}

		SimpleXMLCreator params = result.addChild("params");
		if(args == null || args.length == 0) {
			SimpleXMLCreator param = params.addChild("param");
			param.setAttribute("type", "void");
			param.setText("0");
		} else {
			for(Object a : args) {
				if(a == null) {
					SimpleXMLCreator param = params.addChild("param");
					param.setAttribute("type", "null");
					continue;
				}

				if(a instanceof Integer || a.getClass().getName().equals("int")) {
					SimpleXMLCreator param = params.addChild("param");
					param.setAttribute("type", "int");
					param.setCData(String.valueOf(a));
					continue;
				}

				if(a instanceof Number) {
					SimpleXMLCreator param = params.addChild("param");
					param.setAttribute("type", "number");
					param.setCData(String.valueOf(a));
					continue;
				}

				if(a instanceof ArgumentEncoder) {
					ArgumentEncoder arg = (ArgumentEncoder)a;
					SimpleXMLCreator param = params.addChild("param");
					param.setAttribute("type", arg.type());
					param.setCData(arg.value());
					continue;
				}

				SimpleXMLCreator param = params.addChild("param");
				param.setAttribute("type", "string");
				param.setCData(String.valueOf(a));
			}
		}

		try {
			socklet.reflectFromService(kernel, result);
		} catch(Throwable ex) {
			log.error("reflectFromService failure...", ex);
		}
	}

}
