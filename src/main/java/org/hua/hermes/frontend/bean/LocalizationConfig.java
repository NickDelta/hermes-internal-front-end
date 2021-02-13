package org.hua.hermes.frontend.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Configuration
public class LocalizationConfig
{
    @Bean
    public List<ZoneId> getZoneIds(){
        var now = LocalDateTime.now();
        return ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .sorted((zoneId1, zoneId2) -> {
                    ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
                    ZoneOffset offset2 = now.atZone(zoneId2).getOffset();
                    return offset1.compareTo(offset2);
                })
                .filter(distinctByKeys(zoneId -> zoneId.getRules().getOffset(now)))
                .collect(Collectors.toList());
    }

    @Bean
    public Set<Locale> getLocales(){
        var locales = new HashSet<Locale>();
        locales.add(Locale.getDefault());
        locales.add(new Locale("el","GR"));
        locales.add(Locale.US);

        return locales;
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
    {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

}
