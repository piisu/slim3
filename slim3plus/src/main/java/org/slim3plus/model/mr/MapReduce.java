package org.slim3plus.model.mr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Key;
import lombok.Data;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Model(schemaVersion = 1)
public class MapReduce implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MapReduce other = (MapReduce) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    @Attribute(lob = true)
    private Class<? extends MapReduceTask<?>> taskClass;

    private int workerCount;

    private boolean complete;

    private Date startDate;

    private Date endDate;

    private boolean cancel;

    @Attribute(lob = true)
    private Map<String, Object> params;

    @Attribute(lob = true)
    MapperContext context;

    @Attribute(persistent = false)
    private transient List<Key> workerKeys;

    @JsonIgnore
    public List<Key> getWorkerKeys() {
        if (workerKeys == null && 0 < workerCount) {
            workerKeys = new ArrayList<>(workerCount);
            for (int i = 0; i < workerCount; i++) {
                workerKeys.add(MapWorker.createKey(key, i));
            }
        }
        return workerKeys;
    }

    public String getKeyString() {
        return Datastore.keyToString(getKey());
    }
}
