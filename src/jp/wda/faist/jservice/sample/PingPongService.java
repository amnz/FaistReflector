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

package jp.wda.faist.jservice.sample;

import jp.wda.faist.jservice.InProcessService;
import jp.wda.faist.jservice.ServiceMethod;
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
public class PingPongService extends InProcessService {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public PingPongService() {
		super();

		addArgumentParser(new HaxeParser());
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	private final Logger log = Logger.getLogger(this.getClass());

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	public void screen(SimpleXMLCreator xml) {
		String id = (String)xml.getAttribute("clientID");
		String ip = (String)xml.getAttribute("ipAddress");
		log.debug(" - screen :" + id + " / " + ip);

		kernel.accept(id);
	}

	/** {@inheritDoc} */
	@Override
	public void leave(SimpleXMLCreator xml) {
		String id = (String)xml.getAttribute("clientID");
		log.debug(" - good by... :" + id);
	}

	/** {@inheritDoc} */
	@Override
	public void reflectorResult(SimpleXMLCreator xml) {

	}

	/** {@inheritDoc} */
	@Override
	public void unknownMessage(SimpleXMLCreator xml) {

	}

	/** {@inheritDoc} */
	@Override
	public void unknownMethod(SimpleXMLCreator xml, Throwable cause) {
		log.error("unknown method...", cause);
	}

	/** {@inheritDoc} */
	@Override
	public void evaluteFailure(SimpleXMLCreator xml, Throwable cause) {
		log.error("evalute failure...", cause);
	}

	// コマンド処理メソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param count
	 */
	@ServiceMethod
	public void ping(String clientID, int count, String data) {
		log.debug("ping! " + count + " : data = " + data);
		kernel.client(clientID)
			.method("pong")
			.invoke(count);
	}

}
