package com.chua.image.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ExifFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.geo.Point;
import com.chua.common.support.resource.ResourceConfiguration;
import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.file.FileTypeDirectory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static com.drew.metadata.exif.ExifDirectoryBase.*;
import static com.drew.metadata.file.FileTypeDirectory.*;

/**
 * 图片元数据
 *
 * @author CH
 */
@Spi("image")
public class ImageExifFile extends AbstractResourceFile implements ExifFile {

    private static final String[] DEFAULT_COLUMN = new String[]{"width", "height", "file type", "mime type", "file name"};
    private final Metadata metadata;
    private final ExifSubIFDDirectory firstDirectoryOfType;
    private final FileTypeDirectory fileTypeDirectory;
    private final ExifIFD0Directory exifIFD0Directory;
    private final GpsDirectory gpsDirectory;

    public ImageExifFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
        try (InputStream is = openInputStream()) {
            this.metadata = ImageMetadataReader.readMetadata(is);
            this.firstDirectoryOfType = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            this.fileTypeDirectory = metadata.getFirstDirectoryOfType(FileTypeDirectory.class);
            this.exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            this.gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void removeExif(OutputStream outputStream) {

    }


    private boolean contains(String tagName) {
        for (String s : DEFAULT_COLUMN) {
            if (tagName.toLowerCase().contains(s)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getExtension() {
        return fileTypeDirectory.getString(TAG_EXPECTED_FILE_NAME_EXTENSION);
    }

    @Override
    public String getMineType() {
        return fileTypeDirectory.getString(TAG_DETECTED_FILE_MIME_TYPE);
    }

    @Override
    public String getLongFileType() {
        return fileTypeDirectory.getString(TAG_DETECTED_FILE_TYPE_LONG_NAME);
    }

    @Override
    public String getFileType() {
        return fileTypeDirectory.getString(TAG_DETECTED_FILE_TYPE_NAME);
    }

    @Override
    public Date getOriginalDate() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getDateOriginal();
    }

    @Override
    public Date getDigitizedDate() {
        if (null == firstDirectoryOfType) {
            if (null == firstDirectoryOfType) {
                return null;
            }
            return null;
        }
        return firstDirectoryOfType.getDateDigitized();
    }

    @Override
    public String getNumber() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_FNUMBER);
    }

    @Override
    public String getExposureTime() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_EXPOSURE_TIME);
    }

    @Override
    public String getIsoEquivalent() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_ISO_EQUIVALENT);
    }

    @Override
    public String getFocalLength() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_FOCAL_LENGTH);
    }

    @Override
    public Double getMaxAperture() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getDoubleObject(TAG_MAX_APERTURE);
    }

    @Override
    public String getExifImageWidth() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_EXIF_IMAGE_WIDTH);
    }

    @Override
    public String getExifImageHeight() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_EXIF_IMAGE_HEIGHT);
    }

    @Override
    public String getMake() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_MAKE);
    }

    @Override
    public String getModel() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_MODEL);
    }

    @Override
    public String getXAxisResolution() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_X_RESOLUTION);
    }

    @Override
    public String getYAxisResolution() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_Y_RESOLUTION);
    }

    @Override
    public String getSoftware() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_SOFTWARE);
    }

    @Override
    public String get35MmFilmEquivFocalLength() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
    }

    @Override
    public String getAperture() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_APERTURE);
    }

    @Override
    public String getApplicationNotes() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_APPLICATION_NOTES);
    }

    @Override
    public String getArtist() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_ARTIST);
    }

    @Override
    public String getTagBodySerialNumber() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_BODY_SERIAL_NUMBER);
    }

    @Override
    public String getResolutionUnit() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_RESOLUTION_UNIT);
    }

    @Override
    public String getExposureBias() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_EXPOSURE_BIAS);
    }

    @Override
    public String getColorSpace() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_COLOR_SPACE);
    }

    @Override
    public String getYcbcrCoefficients() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_YCBCR_COEFFICIENTS);
    }

    @Override
    public String getYcbcrPositioning() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_YCBCR_POSITIONING);
    }

    @Override
    public String getYcbcrSubsampling() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_YCBCR_SUBSAMPLING);
    }

    @Override
    public String getExifVersion() {
        if (null == firstDirectoryOfType) {
            return null;
        }
        return firstDirectoryOfType.getString(TAG_EXIF_VERSION);
    }

    @Override
    public Point getGeoLocation() {

        if (null == gpsDirectory) {
            if (null == exifIFD0Directory) {
                return new Point(0, 0);
            }
            exifIFD0Directory.getString(ExifIFD0Directory.TAG_GPS_INFO_OFFSET);
            return new Point(0, 0);
        }


        GeoLocation geoLocation = gpsDirectory.getGeoLocation();
        return new Point(geoLocation.getLatitude(), geoLocation.getLongitude());
    }
}
