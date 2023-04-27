package no.ntnu.nms.domainmodel;

import no.ntnu.nms.CustomerConstants;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.logging.Logging;
import no.ntnu.nms.persistence.PersistenceController;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Registry for all pools.
 * Singleton class.
 */
public class PoolRegistry implements Serializable {

    /**
     * PropertyChangeListener for the registry.
     * Saves the registry to file when a change is made.
     */
    private final transient PropertyChangeListener pcl = evt -> {
        if (evt.getOldValue() != evt.getNewValue()) {
            updatePoolReg();
        }
    };

    /**
     * Singleton instance.
     */
    private static PoolRegistry instance = null;

    /**
     * List of all pools, used by the registry.
     */
    private final ArrayList<Pool> poolList;

    /**
     * Registry file path.
     */
    private final String storageFilePath;

    /**
     * Private constructor to prevent instantiation.
     */
    private PoolRegistry(String path) {
        storageFilePath = path;
        poolList = new ArrayList<>();
    }

    /**
     * Init function used for setting up the application.
     * @param path {@link String} path to the registry file.
     */
    public static void init(String path) {
        instance = new PoolRegistry(path);
        PersistenceController.saveToFile(instance, instance.storageFilePath, false);
    }

    /**
     * Get the singleton instance of the registry.
     * @param isTest {@link boolean} true if the application is running in test mode.
     * @return {@link PoolRegistry} getter for the singleton instance.
     */
    public static PoolRegistry getInstance(boolean isTest) {
        if (instance == null) {
            String path = isTest ? CustomerConstants.TEST_DATA_PATH :
                    CustomerConstants.PROD_DATA_PATH;
            path += "pool/poolreg.ser";
            PersistenceController.loadFromFile(path);
        }
        return instance;
    }

    /**
     * Update the singleton instance of the registry. Used when loading from file.
     * @param poolRegistry {@link PoolRegistry} new instance of the registry.
     */
    public static void updatePoolRegistryInstance(PoolRegistry poolRegistry) {
        if (poolRegistry != null && poolRegistry != instance) {
            instance = poolRegistry;
        }
    }

    /**
     * Add a pool to the registry.
     * Needs to be reimplemented to use the serialization utilities
     * @param pool {@link Pool} to add to the registry.
     */
    public void addPool(Pool pool) {
        poolList.add(pool);
        pool.addPropertyChangeListener(pcl);
        updatePoolReg();
    }

    /**
     * Get an iterator for the complete pool list.
     * @return {@link Iterator} iterator for the complete pool list.
     */
    public Iterator<Pool> getPoolList() {
        return poolList.iterator();
    }

    /**
     * Get a pool by its media function.
     * @param mediaFunction {@link String} media function of the pool.
     * @return {@link Pool} pool with the given media function or {@code null} if no
     *     pool with the given media function exists.
     */
    public Pool getPoolByMediaFunction(String mediaFunction) {
        return poolList.stream()
                .filter(pool -> pool.getMediaFunction().equals(mediaFunction))
                .findFirst()
                .orElse(null);
    }

    /**
     * Remove a pool from the registry.
     * Needs to be reimplemented to use the serialization utilities
     * @param pool {@link Pool} to remove from the registry.
     */
    public void removePool(Pool pool) {
        poolList.remove(pool);
        pool.removePropertyChangeListener(pcl);
        updatePoolReg();
    }

    /**
     * Get the number of pools in the registry.
     * @return {@link int} number of pools in the registry.
     */
    public int getPoolCount() {
        return poolList.size();
    }

    /**
     * Check if the registry contains a given pool.
     * @param pool {@link Pool} to check for.
     * @return {@link boolean} {@code true} if the registry contains the given pool,
     */
    public boolean containsPool(Pool pool) {
        return poolList.contains(pool);
    }

    /**
     * Check if the registry contains a pool with a given media function.
     * @param mediaFunction {@link String} media function of the pool to check for.
     * @return {@link boolean} {@code true} if the registry contains a pool with the
     *     given media function.
     */
    public boolean containsPoolByMediaFunction(String mediaFunction) {
        return getPoolByMediaFunction(mediaFunction) != null;
    }

    /**
     * Clear the registry.
     */
    public void clear() {
        poolList.clear();
        updatePoolReg();
    }

    /**
     * Get a JSON representation of the registry.
     * @return {@link String} JSON representation of the registry.
     */
    public String jsonify() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"pools\":[");
        for (Pool pool : poolList) {
            sb.append(pool.jsonify());
            sb.append(",");
        }
        if (instance.getPoolCount() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Update the registry file.
     */
    private void updatePoolReg() {
        try {
            PersistenceController.saveToFile(this, storageFilePath, false);
        } catch (FileHandlerException e) {
            Logging.getLogger().severe("Unable to update pool registry. " +
                    "Core functionality has been affected. " + e.getMessage());
            System.exit(1);
        }
    }
}
