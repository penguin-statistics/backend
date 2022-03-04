package io.penguinstats.util.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.penguinstats.enums.Server;

@Component("dirtyStages")
public class DirtyStages {

    private Map<Server, Set<String>> stageIdsMap;

    public DirtyStages() {
        this.stageIdsMap = new HashMap<>();
        for (Server server : Server.values()) {
            this.stageIdsMap.put(server, new HashSet<>());
        }
    }

    public void addStageId(Server server, String stageId) {
        this.stageIdsMap.get(server).add(stageId);
    }

    public void addStageIds(Server server, Collection<String> stageIds) {
        this.stageIdsMap.get(server).addAll(stageIds);
    }

    public Set<String> getStageIds(Server server) {
        return this.stageIdsMap.get(server);
    }

    public void clear(Server server) {
        this.stageIdsMap.get(server).clear();
    }

}
