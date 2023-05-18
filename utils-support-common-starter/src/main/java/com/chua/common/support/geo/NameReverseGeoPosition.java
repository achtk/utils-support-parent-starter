package com.chua.common.support.geo;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpClientInvoker;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.io.CompressInnerInputStream;
import com.chua.common.support.io.CompressInputStream;
import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.lang.profile.ProfileProvider;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.*;

import static java.lang.Math.*;

/**
 * 逆物理地址解析
 * The main 'geoname' table has the following fields :
 * ---------------------------------------------------
 * geonameid         : integer id of record in geonames database
 * name              : name of geographical point (utf8) varchar(200)
 * asciiname         : name of geographical point in plain ascii characters, varchar(200)
 * alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
 * latitude          : latitude in decimal degrees (wgs84)
 * longitude         : longitude in decimal degrees (wgs84)
 * feature class     : see http://www.geonames.org/export/codes.html, char(1)
 * feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
 * country code      : ISO-3166 2-letter country code, 2 characters
 * cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
 * admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
 * admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
 * admin3 code       : code for third level administrative division, varchar(20)
 * admin4 code       : code for fourth level administrative division, varchar(20)
 * population        : bigint (8 byte int)
 * elevation         : in meters, integer
 * dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
 * timezone          : the iana timezone id (see file timeZone.txt) varchar(40)
 * modification date : date of last modification in yyyy-MM-dd format
 *
 * @author CH
 * @since 2022-05-12
 */
@Slf4j
@Spi("names")
public class NameReverseGeoPosition extends ProfileProvider<ReverseGeoPosition> implements ReverseGeoPosition, Serializable {
    private static final String DUMP = "http://download.geonames.org/export/dump";
    static KdTree<GeoName> kdTree;

    private static final String CN = "CN.zip";

    public NameReverseGeoPosition() {
        if (null != kdTree) {
            return;
        }

        Metadata database = Repository.of(getString("database", Projects.userHome() + "/geo"))
                .remoteResource(DUMP + "/" + CN)
                .first(CN);

        try (CompressInnerInputStream inputStream = new CompressInnerInputStream(database.openInputStream(), "zip", "CN.txt")) {
            createKdTree(inputStream, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        String name = "classpath:**/" + CN;
//        ResourceProvider provider = ResourceProvider.of(name);
//        Set<Resource> resources = provider.getResources();
//        String sd = "download";
//        if (resources.isEmpty() && getDoubleValue(sd, 0d) == 0d) {
//            String url = DUMP + "/" + CN;
//            log.warn("未找到资源文件, 开始下载文件 DOWNLOAD....");
//            InputStream inputStream = unZip(url);
//            log.warn("下载完成");
//            try {
//                createKdTree(inputStream, false);
//            } catch (IOException ignored) {
//            }
//            return;
//        }
//
//        for (Resource resource : resources) {
//            try (CompressInputStream resourceInputStream = new CompressInputStream(resource, "CN.txt")) {
//                createKdTree(resourceInputStream, false);
//                break;
//            } catch (IOException ignored) {
//                log.warn(ignored.getMessage());
//            }
//        }
    }

    /**
     * 解压zip
     *
     * @param url url
     * @return 流
     */
    private InputStream unZip(String url) {
        URL resource = NameReverseGeoPosition.class.getResource("/");
        String form = resource.getPath();
        HttpClientInvoker invoker = HttpClient.get()
                .url(url).newInvoker();
        HttpResponse httpResponse = invoker.execute();
        httpResponse.receive(new File(form, CN));
        return (InputStream) httpResponse.content();
    }

    /**
     * 创建kd树
     *
     * @param stream    流
     * @param majorOnly 版本
     * @throws IOException ex
     */
    private void createKdTree(InputStream stream, boolean majorOnly) throws IOException {
        ArrayList<GeoName> arPlaceNames;
        try (ProgressBar progressBar = new ProgressBar("开始加载逆地址信息", 0)) {
            arPlaceNames = new ArrayList<>();
            // Read the geonames file in the directory
            try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
                String str;
                while ((str = in.readLine()) != null) {
                    GeoName newPlace = new GeoName(str);
                    progressBar.step();
                    if (!majorOnly || newPlace.majorPlace) {
                        arPlaceNames.add(newPlace);
                    }
                }
            }
        }
        kdTree = new KdTree<>(arPlaceNames);
    }

    /**
     * 获取最近逆地址
     *
     * @param latitude  经度
     * @param longitude 纬度
     * @return GeoCity
     */
    @Override
    public GeoCity nearestPlace(double latitude, double longitude) {
        GeoName geoName = kdTree.findNearest(new GeoName(latitude, longitude));
        GeoCity geoCity = new GeoCity();
        if (geoName.nameLocation.length > 3) {
            geoCity.city(geoName.nameLocation[geoName.nameLocation.length - 3]);
        }
        geoCity.latitude(geoName.latitude)
                .longitude(geoName.longitude)
                .isoCode(geoName.country);
        return geoCity;
    }

    /**
     * @author Daniel Glasson
     * A KD-Tree implementation to quickly find nearest points
     * Currently implements createKDTree and findNearest as that's all that's required here
     */
    @SuppressWarnings("all")
    private class KdTree<T extends BaseKdNodeComparator<T>> {
        private KdNode<T> root;

        public KdTree(List<T> items) {
            root = createKdTree(items, 0);
        }

        public T findNearest(T search) {
            return findNearest(root, search, 0).location;
        }

        // Only ever goes to log2(items.length) depth so lack of tail recursion is a non-issue
        private KdNode<T> createKdTree(List<T> items, int depth) {
            if (items.isEmpty()) {
                return null;
            }
            Collections.sort(items, items.get(0).getComparator(depth % 3));
            int currentIndex = items.size() / 2;
            return new KdNode<T>(createKdTree(new ArrayList<T>(items.subList(0, currentIndex)), depth + 1), createKdTree(new ArrayList<T>(items.subList(currentIndex + 1, items.size())), depth + 1), items.get(currentIndex));
        }

        private KdNode<T> findNearest(KdNode<T> currentNode, T search, int depth) {
            int direction = search.getComparator(depth % 3).compare(search, currentNode.location);
            KdNode<T> next = (direction < 0) ? currentNode.left : currentNode.right;
            KdNode<T> other = (direction < 0) ? currentNode.right : currentNode.left;
            KdNode<T> best = (next == null) ? currentNode : findNearest(next, search, depth + 1); // Go to a leaf
            if (currentNode.location.squaredDistance(search) < best.location.squaredDistance(search)) {
                best = currentNode; // Set best as required
            }
            if (other != null) {
                if (currentNode.location.axisSquaredDistance(search, depth % 3) < best.location.squaredDistance(search)) {
                    KdNode<T> possibleBest = findNearest(other, search, depth + 1);
                    if (possibleBest.location.squaredDistance(search) < best.location.squaredDistance(search)) {
                        best = possibleBest;
                    }
                }
            }
            return best;
        }
    }


    /**
     * @author Daniel Glasson
     * Make the user return a comparator for each axis
     * Squared distances should be an optimisation
     */
    private abstract class BaseKdNodeComparator<T> {
        /**
         * This should return a comparator for whatever axis is passed in
         *
         * @param axis axis
         * @return comparator
         */
        protected abstract Comparator getComparator(int axis);

        /**
         * Return squared distance between current and other
         *
         * @param other other
         * @return double
         */
        protected abstract double squaredDistance(T other);

        /**
         * Return squared distance between one axis only
         *
         * @param axis  axis
         * @param other other
         * @return double
         */
        protected abstract double axisSquaredDistance(T other, int axis);
    }

    /**
     * KdNode
     *
     * @author Daniel Glasson
     */
    private class KdNode<T extends BaseKdNodeComparator<T>> {
        KdNode<T> left;
        KdNode<T> right;
        T location;

        public KdNode(KdNode<T> left, KdNode<T> right, T location) {
            this.left = left;
            this.right = right;
            this.location = location;
        }
    }


    /**
     * Created by Daniel Glasson on 18/05/2014.
     * This class works with a placenames files from http://download.geonames.org/export/dump/
     */

    private class GeoName extends BaseKdNodeComparator<GeoName> {
        public String name;
        public boolean majorPlace; // Major or minor place
        public double latitude;
        public double longitude;
        public String[] nameLocation;
        public double[] point = new double[3]; // The 3D coordinates of the point
        public String country;
        public String timezone;

        GeoName(String data) {
            String[] names = data.split("\t");
            name = names[1];
            nameLocation = names[3].split(",");
            majorPlace = "P".equals(names[6]);
            latitude = Double.parseDouble(names[4]);
            longitude = Double.parseDouble(names[5]);
            setPoint();
            timezone = names[17];
            country = names[8];
        }

        GeoName(Double latitude, Double longitude) {
            name = country = "Search";
            this.latitude = latitude;
            this.longitude = longitude;
            setPoint();
        }

        private void setPoint() {
            point[0] = cos(toRadians(latitude)) * cos(toRadians(longitude));
            point[1] = cos(toRadians(latitude)) * sin(toRadians(longitude));
            point[2] = sin(toRadians(latitude));
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        protected double squaredDistance(GeoName other) {
            double x = this.point[0] - other.point[0];
            double y = this.point[1] - other.point[1];
            double z = this.point[2] - other.point[2];
            return (x * x) + (y * y) + (z * z);
        }

        @Override
        protected double axisSquaredDistance(GeoName other, int axis) {
            double distance = point[axis] - other.point[axis];
            return distance * distance;
        }

        @Override
        protected Comparator<GeoName> getComparator(int axis) {
            return GeoNameComparator.values()[axis];
        }


    }

    protected enum GeoNameComparator implements Comparator<GeoName> {
        /**
         * x
         */
        x {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[0], b.point[0]);
            }
        },
        /**
         * y
         */
        y {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[1], b.point[1]);
            }
        },
        /**
         * z
         */
        z {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[2], b.point[2]);
            }
        };
    }


}
