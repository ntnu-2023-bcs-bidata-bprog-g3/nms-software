package no.ntnu.nms.lfa;

import no.ntnu.nms.api.client.Client;
import no.ntnu.nms.exception.LfaRegistryException;
import no.ntnu.nms.logging.Logging;

import java.util.HashMap;

public class LfaRegistry {

    /**
     * The singleton instance of the LfaRegistry
     */
    private static LfaRegistry instance;

    /**
     * The map of all LFA's
     * Key: IP
     * Value: Name
     */
    private final HashMap<String, String> lfaMap;

    /**
     * Private constructor for the LfaRegistry
     */
    private LfaRegistry() {
        lfaMap = new HashMap<>();
    }

    /**
     * Returns the singleton instance of the LfaRegistry
     * @return the singleton instance of the LfaRegistry
     */
    public static LfaRegistry getInstance() {
        if (instance == null) {
            instance = new LfaRegistry();
        }
        return instance;
    }

    /**
     * Adds a LFA to the registry
     * @param ip {@link String} the IP of the LFA to add
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
     * Removes a LFA from the registry
     * @param lfa {@link String} the LFA to remove
     */
    public void removeLfa(String lfa) {
        lfaMap.remove(lfa);
    }

    /**
     * Refreshes the LFA map by removing all LFA's that are not alive
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
     * Returns a JSON representation of the LFA map
     * @return {@link String} JSON representation of the LFA map
     */
    public String jsonify() {
        if (lfaMap.isEmpty()) {
            return "{\"lfa\": []}";
        }
        StringBuilder json = new StringBuilder("{\"lfa\": [");
        for (String ip : lfaMap.keySet()) {
            json.append("{\"ip\": \"").append(ip).append("\",\"name\": \"").append(lfaMap.get(ip)).append("\"},");
        }
        json = new StringBuilder(json.substring(0, json.length() - 1));
        json.append("]}");
        return json.toString();
    }


}