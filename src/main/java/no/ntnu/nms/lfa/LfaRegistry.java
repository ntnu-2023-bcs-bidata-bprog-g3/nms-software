package no.ntnu.nms.lfa;

import no.ntnu.nms.api.client.Client;
import no.ntnu.nms.exception.LfaRegistryException;
import no.ntnu.nms.logging.Logging;

import java.util.HashMap;

/**
 * The LfaRegistry is a singleton class that holds all LFA's.
 */
public class LfaRegistry {

    /**
     * The singleton instance of the LfaRegistry.
     */
    private static LfaRegistry instance;

    /**
     * The map of all LFA's.
     * Key: IP
     * Value: Name
     */
    private final HashMap<String, String> lfaMap;

    /**
     * Private constructor for the LfaRegistry.
     */
    private LfaRegistry() {
        lfaMap = new HashMap<>();
    }

    /**
     * Returns the singleton instance of the LfaRegistry.
     * @return the singleton instance of the LfaRegistry
     */
    public static LfaRegistry getInstance() {
        if (instance == null) {
            instance = new LfaRegistry();
        }
        return instance;
    }

    /**
     * Adds a LFA to the registry.
     * @param ip {@link String} the IP of the LFA to add.
     * @param name {@link String} the name of the LFA to add
     */
    public void addLfa(String ip, String name) throws LfaRegistryException {
        if (!lfaMap.containsKey(ip)) {
            lfaMap.put(ip, name);
        } else {
            throw new LfaRegistryException("This LFA is already registered");
        }
    }

    /**
     * Returns if the LFA is in the registry.
     * @param ip {@link String} the IP of the LFA to check
     * @return {@link boolean} true if the LFA is in the registry, false otherwise
     */
    public boolean lfaInRegistry(String ip) {
        return lfaMap.containsKey(ip);
    }

    /**
     * Removes a LFA from the registry.
     * @param lfa {@link String} the LFA to remove
     */
    public void removeLfa(String lfa) {
        lfaMap.remove(lfa);
    }

    /**
     * Refreshes the LFA map by removing all LFA's that are not alive.
     */
    public void refreshLfaMap() {
        for (String ip : lfaMap.keySet()) {
            if (!Client.lfaIsAlive(ip)) {
                removeLfa(ip);
                Logging.getLogger().warning("LFA " + ip + " is not alive, " +
                        "removing from registry");
            }
        }
    }

    /**
     * Returns the name of the LFA with the given IP.
     *
     * @param ip the ip
     * @return String name
     */
    public String getLfaName(String ip) {
        return lfaMap.get(ip);
    }

    /**
     * Returns the number of registered LFAs.
     * @return {@link int} the number of registered LFAs
     */
    public int getSize() {
        return lfaMap.size();
    }

    /**
     * Returns a JSON representation of the LFA map.
     * @return {@link String} JSON representation of the LFA map
     */
    public String jsonify() {
        StringBuilder json = new StringBuilder("{\"lfas\": [");

        if (lfaMap.isEmpty()) {
            return json.append("]}").toString();
        }
        for (String ip : lfaMap.keySet()) {
            json
                    .append("{\"ip\": \"")
                    .append(ip)
                    .append("\",\"name\": \"")
                    .append(lfaMap.get(ip))
                    .append("\"},");
        }
        json = new StringBuilder(json.substring(0, json.length() - 1));
        json.append("]}");
        return json.toString();
    }


}
