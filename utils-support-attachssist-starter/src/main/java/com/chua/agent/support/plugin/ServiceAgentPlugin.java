package com.chua.agent.support.plugin;

import com.alibaba.json.JSONArray;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.utils.NetAddress;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * 端点
 *
 * @author CH
 */
public class ServiceAgentPlugin implements HtmlAgentPlugin {

    public static final ServiceAgentPlugin INSTANCE = new ServiceAgentPlugin();

    public static final JSONArray JSON_ARRAY = new JSONArray();
    public static final Map<Node, Set<Node>> sourceToTarget = new LinkedHashMap<>();
    /**
     * 注册地址
     * @param call 节点
     */
    public static void registerAddress(List<InetSocketAddress> call) {
        registerAddress(call, "", "");
    }
    /**
     * 注册地址
     * @param call 节点
     * @param type 类型
     * @param images  图片类型地址
     */
    public static void registerAddress(List<InetSocketAddress> call, String type, String images) {
        Node node = createLocalHost();
        for (InetSocketAddress inetSocketAddress : call) {
            String id = inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort();
            Node node1 = getNode(id);
            if(null == node1) {
                node1 = new Node();
                node1.setId(id);
                node1.setType(type);
                node1.setLabel(node1.id);
                if("image".equals(type)) {
                    node1.setImg(images);
                }
            }

            sourceToTarget.computeIfAbsent(node, it -> new LinkedHashSet<>()).add(node1);
        }
    }
    /**
     * 注册地址
     * @param call 节点
     * @param type 类型
     * @param images  图片类型地址
     */
    public static void registerAddress(String call, String type, String images) {
       registerAddress(call, type, images, true);
    }
    /**
     * 注册地址
     * @param call 节点
     * @param type 类型
     * @param images  图片类型地址
     * @param inLocation 是否与本地关联
     */
    public static void registerAddress(String call, String type, String images, boolean inLocation) {
        if(null == call) {
            return;
        }

        if(!inLocation) {
            return;
        }
        Node node = createLocalHost();
        Node node1 = getNode(call);

        if(null == node1) {
            node1 = new Node();
            node1.setId(call);
            node1.setType(type);
            node1.setLabel(node1.id);
            if("image".equals(type)) {
                node1.setImg(images);
            }
        }

        sourceToTarget.computeIfAbsent(node, it -> new LinkedHashSet<>()).add(node1);
    }
    private static Node createLocalHost() {
        Node node = new Node();
        node.setId("127.0.0.1");
        node.setLabel("127.0.0.1");
        return node;
    }

    /**
     * 数据库连接
     * @param objects 参数
     * @param type 类型
     * @param images  图片类型地址
     */
    public static void registerConnection(Object[] objects, String type, String images) {
        NetAddress netAddress = NetAddress.of(objects[0].toString());
        Node node = createLocalHost();
        Node node1 = new Node();
        node1.setId(netAddress.getAddress() + netAddress.getPath());
        node1.setType(type);
        node1.setLabel(node1.id );
        if("image".equals(type)) {
            node1.setImg(images);
        }
        sourceToTarget.computeIfAbsent(node, it -> new LinkedHashSet<>()).add(node1);
    }

    /**
     * 边
     * @param source 原地址
     * @param target 目标地址
     */
    public static void registerEdge( String source, String target) {
        registerEdge("", "", source, "", "", target);
    }
    /**
     * 边
     * @param sourceImage 原图片
     * @param sourceType 原节点类型
     * @param source 原地址
     * @param targetImage 目标图片
     * @param sourceType 目标节点类型
     * @param target 目标地址
     */
    public static void registerEdge(String sourceType, String sourceImage, String source, String targetType, String targetImage,String target) {
        if(null == source || null == target) {
            return;
        }

        Node key = getNode(source);

        if(null == key) {
            key = new Node();
            key.setId(source);
            key.setLabel(source);
            key.setType(sourceType);
            key.setImg(sourceImage);
        }


        Node value = getNode(target);

        if(null == value) {
            value = new Node();
            value.setId(target);
            value.setLabel(target);
            value.setType(targetType);
            value.setImg(targetImage);
        }
        sourceToTarget.computeIfAbsent(key, it -> new LinkedHashSet<>()).add(value);

    }

    private static Node getNode(String source) {
        Set<Node> nodes1 = getAllNodes();
        for (Node node : nodes1) {
            if(node.getId().equals(source)) {
                return  node;
            }
        }
        return null;
    }


    @Path("server")
    public String html() {
        return "server.html";
    }

    @Path("server_data")
    public String data() {
        JSONObject jsonObject = new JSONObject();
        JSONArray nodes = new JSONArray();
        JSONArray edges = new JSONArray();

        Set<Node> nodes1 = getAllNodes();
        nodes.addAll(nodes1);

        for (Map.Entry<Node, Set<Node>> entry : sourceToTarget.entrySet()) {
            String sId = entry.getKey().getId();
            for (Node node : entry.getValue()) {
                Edge edge = new Edge();
                edge.setSource(sId);
                edge.setTarget(node.getId());

                edges.add(edge);
            }
        }

        jsonObject.put("nodes", nodes);
        jsonObject.put("edges", edges);
        return jsonObject.toJSONString();
    }

    private static Set<Node> getAllNodes() {
        Collection<Set<Node>> values = sourceToTarget.values();
        Set<Node> nodeSet = new LinkedHashSet<>();
        for (Set<Node> value : values) {
            nodeSet.addAll(value);
        }
        nodeSet.addAll(sourceToTarget.keySet());
        return nodeSet;
    }

    @Override
    public String name() {
        return "server";
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }

    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

    }
    public static class Node {
        private String id;
        private String type;
        private String label;
        private String img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return Objects.equals(id, node.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class Edge {
        private String source;
        private String target;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}
