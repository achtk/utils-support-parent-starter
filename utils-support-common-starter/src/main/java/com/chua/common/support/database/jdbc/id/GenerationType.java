
package com.chua.common.support.database.jdbc.id;

/**
 * Defines the types of primary key generation strategies.
 *
 * @author ACHTK
 * @see GeneratedValue
 * @since Java Persistence 1.0
 */
public enum GenerationType {

    /**
     * Indicates that the persistence provider must assign primary keys for the
     * entity using an underlying database table to ensure uniqueness.
     */
    TABLE,

    /**
     * Indicates that the persistence provider must assign primary keys for the
     * entity using a database sequence.
     */
    SEQUENCE,

    /**
     * Indicates that the persistence provider must assign primary keys for the
     * entity using a database identity column.
     */
    IDENTITY,

    /**
     * Indicates that the persistence provider should pick an appropriate
     * strategy for the particular database. The <code>AUTO</code> generation
     * strategy may expect a database resource to exist, or it may attempt to
     * create one. A vendor may provide documentation on how to create such
     * resources in the event that it does not support schema generation or
     * cannot create the schema resource at runtime.
     */
    AUTO,

    /**
     * A 32 character length UUID
     */
    UUID,

    /**
     * A 25 character length UUID
     */
    UUID25,

    /**
     * A 26 character length UUID
     */
    UUID26,

    /**
     * A 32 character length UUID
     */
    UUID32,

    /**
     * A 36 character length UUID
     */
    UUID36,

    /**
     * A Any length UUID
     */
    UUID_ANY,

    /**
     * A sorted UUID
     */
    SORTED_UUID,

    /**
     * A TimeStamp
     */
    TIMESTAMP,

    /**
     * A Snowflake ID
     */
    SNOWFLAKE,

    /**
     * Unknow or Customized IdGenerators
     */
    OTHER
}
