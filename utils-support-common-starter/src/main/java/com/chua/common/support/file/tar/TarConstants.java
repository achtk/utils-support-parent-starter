package com.chua.common.support.file.tar;


/**
 * This interface contains all the definitions used in the package.
 * <p>
 * For tar formats (FORMAT_OLDGNU, FORMAT_POSIX, etc.) see GNU tar
 * <I>tar.h</I> type <I>enum archive_format</I>
 *
 * @author Administrator
 */
public interface TarConstants {

    /**
     * GNU format as per before tar 1.12.
     */
    int FORMAT_OLDGNU = 2;

    /**
     * Pure Posix format.
     */
    int FORMAT_POSIX = 3;

    /**
     * The length of the name field in a header buffer.
     */
    int NAMELEN = 100;

    /**
     * The length of the mode field in a header buffer.
     */
    int MODELEN = 8;

    /**
     * The length of the user id field in a header buffer.
     */
    int UIDLEN = 8;

    /**
     * The length of the group id field in a header buffer.
     */
    int GIDLEN = 8;

    /**
     * The maximum value of gid/uid in a tar archive which can
     * be expressed in octal char notation (that's 7 sevens, octal).
     */
    long MAXID = 07777777L;

    /**
     * The length of the checksum field in a header buffer.
     */
    int CHKSUMLEN = 8;

    /**
     * The length of the size field in a header buffer.
     * Includes the trailing space or NUL.
     */
    int SIZELEN = 12;

    /**
     * The maximum size of a file in a tar archive
     * which can be expressed in octal char notation (that's 11 sevens, octal).
     */
    long MAXSIZE = 077777777777L;

    /**
     * Offset of start of magic field within header record
     */
    int MAGIC_OFFSET = 257;
    /**
     * The length of the magic field in a header buffer including the version.
     */
    int MAGICLEN = 8;

    /**
     * The length of the magic field in a header buffer.
     */
    int PURE_MAGICLEN = 6;

    /**
     * Offset of start of magic field within header record
     */
    int VERSION_OFFSET = 263;
    /**
     * Previously this was regarded as part of "magic" field, but it
     * is separate.
     */
    int VERSIONLEN = 2;

    /**
     * The length of the modification time field in a header buffer.
     */
    int MODTIMELEN = 12;

    /**
     * The length of the user name field in a header buffer.
     */
    int UNAMELEN = 32;

    /**
     * The length of the group name field in a header buffer.
     */
    int GNAMELEN = 32;

    /**
     * The length of each of the device fields (major and minor) in a header buffer.
     */
    int DEVLEN = 8;

    /**
     * Length of the prefix field.
     */
    int PREFIXLEN = 155;

    /**
     * The length of the access time field in an old GNU header buffer.
     */
    int ATIMELEN_GNU = 12;

    /**
     * The length of the created time field in an old GNU header buffer.
     */
    int CTIMELEN_GNU = 12;

    /**
     * The length of the multivolume start offset field in an old GNU header buffer.
     */
    int OFFSETLEN_GNU = 12;

    /**
     * The length of the long names field in an old GNU header buffer.
     */
    int LONGNAMESLEN_GNU = 4;

    /**
     * The length of the padding field in an old GNU header buffer.
     */
    int PAD2LEN_GNU = 1;

    /**
     * The sum of the length of all sparse headers in an old GNU header buffer.
     */
    int SPARSELEN_GNU = 96;

    /**
     * The length of the is extension field in an old GNU header buffer.
     */
    int ISEXTENDEDLEN_GNU = 1;

    /**
     * The length of the real size field in an old GNU header buffer.
     */
    int REALSIZELEN_GNU = 12;

    /**
     * The sum of the length of all sparse headers in a sparse header buffer.
     */
    int SPARSELEN_GNU_SPARSE = 504;

    /**
     * The length of the is extension field in a sparse header buffer.
     */
    int ISEXTENDEDLEN_GNU_SPARSE = 1;

    /**
     * LF_ constants represent the "link flag" of an entry, or more commonly,
     * the "entry type". This is the "old way" of indicating a normal file.
     */
    byte LF_OLDNORM = 0;

    /**
     * Normal file type.
     */
    byte LF_NORMAL = (byte) '0';

    /**
     * Link file type.
     */
    byte LF_LINK = (byte) '1';

    /**
     * Symbolic link file type.
     */
    byte LF_SYMLINK = (byte) '2';

    /**
     * Character device file type.
     */
    byte LF_CHR = (byte) '3';

    /**
     * Block device file type.
     */
    byte LF_BLK = (byte) '4';

    /**
     * Directory file type.
     */
    byte LF_DIR = (byte) '5';

    /**
     * FIFO (pipe) file type.
     */
    byte LF_FIFO = (byte) '6';

    /**
     * Contiguous file type.
     */
    byte LF_CONTIG = (byte) '7';

    /**
     * Identifies the *next* file on the tape as having a long linkname.
     */
    byte LF_GNUTYPE_LONGLINK = (byte) 'K';

    /**
     * Identifies the *next* file on the tape as having a long name.
     */
    byte LF_GNUTYPE_LONGNAME = (byte) 'L';

    /**
     * Sparse file type.
     */
    byte LF_GNUTYPE_SPARSE = (byte) 'S';

    // See "https://www.opengroup.org/onlinepubs/009695399/utilities/pax.html#tag_04_100_13_02"

    /**
     * Identifies the entry as a Pax extended header.
     */
    byte LF_PAX_EXTENDED_HEADER_LC = (byte) 'x';

    /**
     * Identifies the entry as a Pax extended header (SunOS tar -E).
     */
    byte LF_PAX_EXTENDED_HEADER_UC = (byte) 'X';

    /**
     * Identifies the entry as a Pax global extended header.
     */
    byte LF_PAX_GLOBAL_EXTENDED_HEADER = (byte) 'g';

    String TMAGIC = "ustar";

    /**
     * The magic tag representing a POSIX tar archive.
     */
    String MAGIC_POSIX = "ustar\0";
    String VERSION_POSIX = "00";

    /**
     * The magic tag representing a GNU tar archive.
     */
    String GNU_TMAGIC = "ustar  ";
    /**
     * Appear to be two possible GNU versions
     */
    String VERSION_GNU_SPACE = " \0";
    String VERSION_GNU_ZERO = "0\0";

    /**
     * The name of the GNU tar entry which contains a long name.
     */
    String GNU_LONGLINK = "././@LongLink";

}
