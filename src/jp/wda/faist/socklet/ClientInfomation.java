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

package jp.wda.faist.socklet;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jp.wda.faist.util.EncryptedData;
import jp.wda.g2.SocketProcessor;

/**
 *
 *
 *
 * $Id: ClientInfomation.java,v 0:fb0c1025e23b 2012/05/30 07:58:23 amnz $
 * @author		$Author: amnz $
 * @revision	$Revision: 0:fb0c1025e23b $
 * @date		$Date: Wed, 30 May 2012 16:58:23 +0900 $
 */
public class ClientInfomation {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public ClientInfomation() {
		super();

		this.dhsalt = new BigInteger(512, new Random(System.currentTimeMillis()));
		this.dhpub  = DH_GENERATOR.modPow(dhsalt, DH_PRIME);
	}

	/**
	 * デフォルトの設定を用いてオブジェクトを構築するコンストラクタ
	 *
	 */
	public ClientInfomation(SocketProcessor client) {
		this();

		this.client   = client;
		this.client.setAttributes(this);

		if(client.getConnectingConditions() != null && client.getConnectingConditions().getPassword() != null){
			BigInteger clientPub = new BigInteger(client.getConnectingConditions().getPassword(), 16);
			BigInteger rawDESKey = clientPub.modPow(dhsalt, DH_PRIME);

			this.secretKey = null;
			try{
				MessageDigest md = MessageDigest.getInstance(KEY_DIGEST);
				md.update(rawDESKey.toByteArray());

				byte[] buf = md.digest();
				this.secretKey = new SecretKeySpec(buf, CIPHER);
			}catch(Throwable ex){
				ex.printStackTrace();
			}

		}

	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	private SocketProcessor client;

	private BigInteger dhsalt = null;

	private BigInteger dhpub  = null;

	public static final String KEY_DIGEST = "md5";

	public static final String CIPHER = "Blowfish";

	public static final BigInteger DH_GENERATOR = new BigInteger("2");
	public static final BigInteger DH_PRIME = new BigInteger(
			"FFFFFFFF" + "FFFFFFFF" + "C90FDAA2" + "2168C234" + "C4C6628B" + "80DC1CD1" +
			"29024E08" + "8A67CC74" + "020BBEA6" + "3B139B22" + "514A0879" + "8E3404DD" +
			"EF9519B3" + "CD3A431B" + "302B0A6D" + "F25F1437" + "4FE1356D" + "6D51C245" +
			"E485B576" + "625E7EC6" + "F44C42E9" + "A637ED6B" + "0BFF5CB6" + "F406B7ED" +
			"EE386BFB" + "5A899FA5" + "AE9F2411" + "7C4B1FE6" + "49286651" + "ECE65381" +
			"FFFFFFFF" + "FFFFFFFF"
			, 16);

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXX */
	private String hardwareID = null;
	/**
	 * XXXを取得します。<BR>
	 * @return XXX
	 */
	public String getHardwareID(){ return hardwareID; }
	/**
	 * XXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setHardwareID(String s){ hardwareID = s; }

	/* ***********************************************************************>> */
	/** XXX */
	private SecretKey secretKey = null;
	/**
	 * XXXを取得します。<BR>
	 * @return XXX
	 */
	public SecretKey getSecretKey(){ return secretKey; }

	/* ***********************************************************************>> */
	/** XXXX */
	private String serviceName = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public String getServiceName(){ return serviceName; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setServiceName(String s){ serviceName = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private boolean service = false;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public boolean isService(){ return service; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setService(boolean s){ service = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private Map<String, String> attributes = new ConcurrentHashMap<String, String>();
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public Map<String, String> getAttributes(){ return attributes; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public String getDHPublicValue(){
		if(dhpub == null){ return ""; }
		return dhpub.toString(16);
	}

	/**
	 *
	 * @param value
	 * @param iv
	 * @return
	 */
	public String decrypt(EncryptedData value){
		if(value == null || value.isNull()){ return null; }

		byte[] data = parseBytes(value.getData());
		byte[] biIV = parseBytes(value.getIv());

		try{
			Cipher c = Cipher.getInstance(CIPHER + "/CBC/PKCS5Padding");
			IvParameterSpec dps = new IvParameterSpec(biIV);
			c.init(Cipher.DECRYPT_MODE, secretKey, dps);

			return new String( c.doFinal(data), client.getEncoding() );
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @param value
	 * @param iv
	 * @return
	 */
	public String decrypt(Object value, Object iv){
		if(value == null || iv == null){ return null; }
		return decrypt(new EncryptedData(value.toString(), iv.toString()));
	}

	/**
	 *
	 * @param value
	 * @param iv
	 * @return
	 */
	public String decrypt(String value){
		return decrypt(parseEncryptedData(value));
	}

	/** {@inheritDoc} */
	private EncryptedData parseEncryptedData(String value) {
		if(value == null || value.length() == 0){ return new EncryptedData(); }

		int idx = value.indexOf('[');
		if(idx <= 0){ return new EncryptedData(); }

		String iv = value.substring(idx + 1);
		if(iv.endsWith("]")){
			iv = iv.substring(0, iv.length() - 1);
		}
		value = value.substring(0, idx);

		return new EncryptedData(value, iv);
	}

	/**
	 *
	 * @param num
	 * @return
	 */
	private byte[] parseBytes(String num){
		if(num == null || num.length() == 0){ return new byte[0]; }
		if(num.length() % 2 == 1){ num = "0" + num; }

		byte[] result = new byte[num.length() / 2];

		for(int i = 0; i < result.length; i++){
			result[i] = (byte)Integer.parseInt(num.substring(i * 2, i * 2 + 2), 16);
		}

		return result;
	}

}
