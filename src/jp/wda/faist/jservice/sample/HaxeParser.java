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

import java.util.List;

import jp.wda.faist.jservice.parser.ArgumentParser;
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
public class HaxeParser implements ArgumentParser {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public HaxeParser() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public String getType() { return "haxe"; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	public boolean parse(SimpleXMLCreator arg, List<Class<?>> argTypes, List<Object> argValues) {
		String val = arg.getText();

		if(val.equals("z")) {
			argTypes.add(int.class);
			argValues.add(0);
			return true;
		}

		if(val.startsWith("i")) {
			try {
				argValues.add(Integer.parseInt(val.substring(1)));
			} catch(NumberFormatException ex) {
				return false;
			}

			argTypes.add(int.class);
			return true;
		}

		return true;
	}

}
