package com.cobolt.core;

import com.cobolt.objects.HashUtils;

import java.io.Serializable;

/**
 * Base class for all Cobolt objects (blobs, trees, commits)
 */
public abstract class CoboltObject implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;

    /**
     * Get the object type (blob, tree, commit)
     */
    public abstract String getType();

    /**
     * Get the object content as bytes
     */
    public abstract byte[] getContent();

    /**
     * Compute and set the object ID based on type and content
     */
    public void computeId() {
        this.id = HashUtils.computeObjectId(getType(), getContent());
    }

    /**
     * Get the object ID (SHA-1 hash)
     */
    public String getId() {
        if (id == null) {
            computeId();
        }
        return id;
    }

    /**
     * Get short version of ID (7 characters)
     */
    public String getShortId() {
        return HashUtils.shortHash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CoboltObject))
            return false;
        CoboltObject other = (CoboltObject) obj;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
