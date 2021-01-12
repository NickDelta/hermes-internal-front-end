package org.hua.hermes.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Application  {

    @NotEmpty(message = "{entity.application.organization.notempty}")
    private String organization;

    @NotNull(message = "{entity.application.state.notnull}")
    private ApplicationState state;

    @Size(max = 1024, message = "{entity.application.details.size}")
    private String details;

    @NotNull(message = "{entity.application.appointmentDate.notnull}")
    @Future(message = "{entity.application.appointmentDate.future}")
    private Date appointmentDate;

}
