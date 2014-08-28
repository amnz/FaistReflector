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

package jp.wda.faist.jservice.parser;

import java.util.List;

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
public interface ArgumentParser {

	/**
	 *
	 * @return
	 */
	public String getType();

	/**
	 *
	 * @param param
	 * @param argTypes
	 * @param args
	 * @return
	 */
	public boolean parse(SimpleXMLCreator param, List<Class<?>> argTypes, List<Object> args);

}
