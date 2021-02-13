package org.hua.hermes.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Application extends AbstractEntity {

    public static final String ENTITY_NAME = "Application";

    public static final String STATE_LABEL = "State";
    public static final String APPOINTMENT_DATE_LABEL = "Appointment Date & Time";
    public static final String DETAILS_LABEL = "Details";

    @JsonProperty("organization")
    private String organization;

    @JsonProperty("state")
    private ApplicationState state;

    @JsonProperty("details")
    private String details;

    @JsonProperty("appointment_date")
    private Date appointmentDate;

}
