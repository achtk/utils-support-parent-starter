
package com.chua.common.support.database.jdbc.id;

import com.chua.common.support.database.jdbc.Dialect;
import com.chua.common.support.database.jdbc.Type;

import java.sql.Connection;

/**
 * AutoGenerator will depends database's id generator mechanism like MySql's
 * Identity, Oracle's Sequence...
 *
 * @author Yong Zhu
 * @version 1.0.0
 * @since 1.0.0
 */
public class AutoIdGenerator implements IdGenerator {
	private static final String JDIALECTS_AUTOID_NAME = "jdia_autoid";
	private static final String JDIALECTS_AUTOID_TABLE = "jdia_table_autoid";
	private static final String JDIALECTS_AUTOID_SEQUENCE = "jdia_seq_autoid";

	public static final AutoIdGenerator INSTANCE = new AutoIdGenerator();

	public static final TableIdGenerator TABLE_AUTOID_INSTANCE = new TableIdGenerator(JDIALECTS_AUTOID_NAME,
			JDIALECTS_AUTOID_TABLE, "idcolumn", "valuecolumn", "next_val", 1, 50);

	public static final SequenceIdGenerator SEQ_AUTOID_INSTANCE = new SequenceIdGenerator(JDIALECTS_AUTOID_NAME,
			JDIALECTS_AUTOID_SEQUENCE, 1, 1);

	@Override
	public GenerationType getGenerationType() {
		return GenerationType.AUTO;
	}

	@Override
	public String getIdGenName() {
		return JDIALECTS_AUTOID_NAME;
	}

	@Override
	public IdGenerator newCopy() {
		return INSTANCE;
	}

	@Override
	public Boolean dependOnAutoIdGenerator() {
		return true;
	}

	@Override
	public Object getNextID(Connection con, Dialect dialect, Type dataType) {
		if (dialect.ddlFeatures.supportBasicOrPooledSequence()) {
			return SEQ_AUTOID_INSTANCE.getNextID(con, dialect, dataType);
		} else {
			return TABLE_AUTOID_INSTANCE.getNextID(con, dialect, dataType);
		}
	}

	/**
	 * If dialect support sequence, return a SequenceIdGenerator, otherwise return a
	 * TableIdGenerator
	 */
	public IdGenerator getSequenceOrTableIdGenerator(Dialect dialect) {
		if (dialect.ddlFeatures.supportBasicOrPooledSequence()) {
			return SEQ_AUTOID_INSTANCE;
		} else {
			return TABLE_AUTOID_INSTANCE;
		}
	}
}
