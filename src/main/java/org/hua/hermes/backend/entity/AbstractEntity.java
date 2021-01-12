package org.hua.hermes.backend.entity;

import lombok.Getter;
import java.util.Date;
import java.util.Objects;

@Getter
public abstract class AbstractEntity {

    private String id;

    private int version;

    private Date createdDate;

    private Date lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractEntity that = (AbstractEntity) o;
        return version == that.version &&
                Objects.equals(id, that.id);
    }
}
