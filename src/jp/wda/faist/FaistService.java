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

package jp.wda.faist;

import java.util.ArrayList;
import java.util.List;

import jp.wda.faist.selector.ClientIDSelector;
import jp.wda.faist.selector.ClientSelector;
import jp.wda.g2.SocketProcessor;
import jp.wda.g2.util.SimpleXMLCreator;

/**
 *
 *
 *
 * $Id: FaistService.java,v 0:fb0c1025e23b 2012/05/30 07:58:23 amnz $
 * @author		$Author: amnz $
 * @revision	$Revision: 0:fb0c1025e23b $
 * @date		$Date: Wed, 30 May 2012 16:58:23 +0900 $
 */
public class FaistService {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public FaistService() {
		super();

		addClientSelector(new ClientIDSelector());
	}

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private String name = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public String getName(){ return name; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setName(String s){ name = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private String password = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public String getPassword(){ return password; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setPassword(String s){ password = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private SocketProcessor processor = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public SocketProcessor getProcessor(){ return processor; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setProcessor(SocketProcessor s){ processor = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private List<SocketProcessor> clients = new ArrayList<SocketProcessor>();
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public List<SocketProcessor> getClients(){ return clients; }

	/* ***********************************************************************>> */
	/** XXXX */
	private List<ClientSelector> selectors = new ArrayList<ClientSelector>();
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public void addClientSelector(ClientSelector selector){
		selectors.add(selector);
	}

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param	clientID
	 * @return
	 */
	public SocketProcessor getClientByID(String clientID) {
		if(clientID == null || clientID.length() == 0) { return null; }

		for (SocketProcessor c : clients) {
			if(!c.getClientID().equals(clientID)) { continue; }

			return c;
		}
		return null;
	}

	/**
	 *
	 * @param xml
	 * @return
	 */
	public List<SocketProcessor> selectClient(SimpleXMLCreator xml) {
		List<SocketProcessor> clients = new ArrayList<SocketProcessor>();
		for(ClientSelector selector : selectors) {
			if(selector.select(this, xml, clients)) { return clients; }
		}
		return getClients();
	}

}
