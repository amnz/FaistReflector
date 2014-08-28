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

import jp.wda.faist.jservice.parser.ArgumentEncoder;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Rev$
 * @date		$Date$
 */
public class HaxeInt implements ArgumentEncoder {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public HaxeInt(int value) {
		super();

		this.value = value;
	}

	/**  */
	private int value;

	/** {@inheritDoc} */
	@Override
	public String type() { return "haxe"; }

	/** {@inheritDoc} */
	@Override
	public String value() {
		if(value == 0) { return "z"; }
		return "i" + value;
	}

}
