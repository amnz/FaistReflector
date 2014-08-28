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
public interface ClientSelector {

	/**
	 *
	 * @param service
	 * @param xml
	 * @param clients
	 * @return
	 */
	public boolean select(FaistService service, SimpleXMLCreator xml, List<SocketProcessor> clients);

	/**
	 *
	 * @param xml
	 */
	public void appendCondition(SimpleXMLCreator xml);

}
