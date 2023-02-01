package no.ntnu.nms.domain_model;

import java.util.ArrayList;
import java.util.Iterator;

public class PoolRegistry implements java.io.Serializable {

    private static PoolRegistry instance = null;

    private final ArrayList<Pool> poolList;

    private PoolRegistry() {
        poolList = new ArrayList<>();
    }

    public static PoolRegistry getInstance() {
        if (instance == null) {
            instance = new PoolRegistry();
        }
        return instance;
    }

    public void addPool(Pool pool) {
        poolList.add(pool);
    }

    public Iterator<Pool> getPoolList() {
        return poolList.iterator();
    }

    public Pool getPoolByMediaFunction(String mediaFunction) {
        return poolList.stream()
                .filter(pool -> pool.getMediaFunction().equals(mediaFunction))
                .findFirst()
                .orElse(null);
    }

    public void removePool(Pool pool) {
        poolList.remove(pool);
    }

    public void removePoolByMediaFunction(String mediaFunction) {
        poolList.removeIf(pool -> pool.getMediaFunction().equals(mediaFunction));
    }

    public void removeAllPools() {
        poolList.clear();
    }

    public int getPoolCount() {
        return poolList.size();
    }

    public boolean containsPool(Pool pool) {
        return poolList.contains(pool);
    }

    public boolean containsPoolByMediaFunction(String mediaFunction) {
        return getPoolByMediaFunction(mediaFunction) != null;
    }

    public String jsonify() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Pool pool : poolList) {
            sb.append(pool.jsonify());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

}
