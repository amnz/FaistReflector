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

package jp.wda.faist.selector;

import java.util.List;

import jp.wda.faist.FaistService;
import jp.wda.g2.SocketProcessor;
import jp.wda.g2.util.SimpleXMLCreator;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Rev$
 * @date		$Date$
 */
public class ClientIDSelector implements ClientSelector {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public ClientIDSelector() {
		super();

		this.clientID = null;
	}

	/**
	 */
	public ClientIDSelector(String clientID) {
		super();

		this.clientID = clientID;
	}

	private static final String SELECTOR_NAME = "clientID";

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/**  */
	private String clientID;

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	public void appendCondition(SimpleXMLCreator xml) {
		xml.setAttribute("selector",    SELECTOR_NAME);
		xml.setAttribute(SELECTOR_NAME, this.clientID);
	}

	/** {@inheritDoc} */
	@Override
	public boolean select(FaistService service, SimpleXMLCreator xml, List<SocketProcessor>clients) {
		String selectorName = (String)xml.getAttribute("selector");
		if(selectorName == null || !SELECTOR_NAME.equals(selectorName)) { return false; }

		String clientID = (String)xml.getAttribute(SELECTOR_NAME);
		if(clientID == null || clientID.length() == 0) { return false; }

		xml.removeAttribute("selector");
		xml.removeAttribute(SELECTOR_NAME);

		SocketProcessor client = service.getClientByID(clientID);
		if(client != null) { clients.add(client); }

		return true;
	}

}
