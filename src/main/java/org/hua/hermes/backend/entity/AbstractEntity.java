package org.hua.hermes.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public abstract class AbstractEntity {

    //Constants
    public static final String ID_LABEL = "Id";
    public static final String VERSION_LABEL = "No. of Edits";
    public static final String CREATED_BY_LABEL = "Applicant";
    public static final String CREATED_DATE_LABEL = "Created On";
    public static final String LAST_MODIFIED_BY_LABEL = "Last Modified By";
    public static final String LAST_MODIFIED_ON_LABEL = "Last Modified On";

    @JsonProperty("id")
    private String id;

    @JsonProperty("version")
    private int version;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("last_modified_by")
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
