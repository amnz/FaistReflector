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

import java.util.HashMap;
import java.util.List;

import jp.wda.faist.jservice.parser.ArgumentParser;
import jp.wda.faist.jservice.parser.ClientParser;
import jp.wda.faist.jservice.parser.IntParser;
import jp.wda.faist.jservice.parser.NullParser;
import jp.wda.faist.jservice.parser.NumberParser;
import jp.wda.faist.jservice.parser.StringParser;
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
public abstract class InProcessService {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public InProcessService() {
		super();

		addArgumentParser(new ClientParser());
		addArgumentParser(new NullParser());
		addArgumentParser(new IntParser());
		addArgumentParser(new NumberParser());
		addArgumentParser(new StringParser());
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	private final Logger log = Logger.getLogger("jp.wda.faist.systemlog");

	/** 引数解析機 */
	private HashMap<String, ArgumentParser> parsers = new HashMap<String, ArgumentParser>();

	/* ***********************************************************************>> */
	/** XXXX */
	protected InProcessServiceKernel kernel = null;

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param parser
	 */
	public void addArgumentParser(ArgumentParser parser) {
		parsers.put(parser.getType(), parser);
	}

	/**
	 *
	 * @param arguments
	 * @param argTypes
	 * @param argValues
	 */
	public boolean parseArguments(List<SimpleXMLCreator> arguments, List<Class<?>> argTypes, List<Object> argValues) {
		if(arguments == null || arguments.size() == 0) { return true; }

		for(SimpleXMLCreator arg : arguments) {
			String type = (String)arg.getAttribute("type");
			if(type == null || type.length() == 0) { continue; }
			if(!parsers.containsKey(type)) { continue; }

			if(!parsers.get(type).parse(arg, argTypes, argValues)) {
				log.error("引数が不正です。: type=" + type + " value=" + arg.getText());
				return false;
			}
		}
		return true;
	}

	// 抽象メソッド /////////////////////////////////////////////////////////////////////
	//                                                                Abstract Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 新規クライアントが接続してきたときに呼ばれます。
	 *
	 * アクセスを許可する場合は kernel().accept(clientID); を、
	 * 拒否する場合は kernel().deny(clientID); を実行してください。
	 *
	 * @param xml リクエストメッセージ
	 */
	public abstract void screen(SimpleXMLCreator xml);

	/**
	 * クライアントが切断した場合に呼ばれます。
	 *
	 * @param xml リクエストメッセージ
	 */
	public abstract void leave(SimpleXMLCreator xml);

	/**
	 * リフレクタからのresultメッセージが送信された場合に呼ばれます。
	 *
	 * @param xml リクエストメッセージ
	 */
	public abstract void reflectorResult(SimpleXMLCreator xml);

	/**
	 * 不正なメッセージが送信された場合に呼ばれます。
	 *
	 * @param xml リクエストメッセージ
	 */
	public abstract void unknownMessage(SimpleXMLCreator xml);

	/**
	 * 不正な引数かまたは存在しないメソッドを実行しようとした場合に呼ばれます。
	 *
	 * @param xml リクエストメッセージ
	 * @param cause 原因
	 */
	public abstract void unknownMethod(SimpleXMLCreator xml, Throwable cause);

	/**
	 * メソッドを実行時に例外が発生した場合に呼ばれます。
	 *
	 * @param xml リクエストメッセージ
	 * @param cause 原因
	 */
	public abstract void evaluteFailure(SimpleXMLCreator xml, Throwable cause);

}
