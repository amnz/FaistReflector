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

package jp.wda.faist.util;

/**
 *
 *
 *
 * $Id: EncryptedData.java,v 0:fb0c1025e23b 2012/05/30 07:58:23 amnz $
 * @author		$Author: amnz $
 * @revision	$Revision: 0:fb0c1025e23b $
 * @date		$Date: Wed, 30 May 2012 16:58:23 +0900 $
 */
public class EncryptedData {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *	デフォルトの設定を用いてオブジェクトを構築するコンストラクタ
	 */
	public EncryptedData(){
		super();
	}

	/**
	 *
	 * @param data
	 * @param iv
	 */
	public EncryptedData(String data, String iv){
		super();

		this.data = data;
		this.iv   = iv;
	}

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXX */
	private String data = null;
	/**
	 * XXXを取得します。<BR>
	 * @return XXX
	 */
	public String getData(){ return data; }
	/**
	 * XXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setData(String s){ data = s; }

	/* ***********************************************************************>> */
	/** XXX */
	private String iv = null;
	/**
	 * XXXを取得します。<BR>
	 * @return XXX
	 */
	public String getIv(){ return iv; }
	/**
	 * XXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setIv(String s){ iv = s; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @return
	 */
	public boolean isNull(){
		return data == null || iv == null;
	}

}
