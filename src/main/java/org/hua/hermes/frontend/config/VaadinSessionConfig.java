package org.hua.hermes.frontend.config;

import com.vaadin.flow.server.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class VaadinSessionConfig implements VaadinServiceInitListener {

    @Autowired
    private List<ZoneId> zones;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException
            {
                var now = LocalDateTime.now();
                //Get zone that has the same ZoneRule with the system's default ZoneId.
                //Normally we should detect the zone based on the client's fingerprints
                //But I don't see an easy way to do that in Vaadin. So let's leave it to the user.
                var zone = zones.stream()
                        .filter(z -> z.getRules().getOffset(now)
                                .equals(ZoneId.systemDefault().getRules().getOffset(now)))
                        .findFirst()
                        .get(); //Surely there will be a match

                event.getSession().setAttribute(ZoneId.class,zone);
            }
        });

    }
}
