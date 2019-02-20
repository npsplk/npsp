package lk.npsp.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import io.github.jhipster.config.jcache.BeanClassLoaderAwareJCacheRegionFactory;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(this.getClass().getClassLoader());
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(lk.npsp.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            cm.createCache(lk.npsp.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            cm.createCache(lk.npsp.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Route.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Route.class.getName() + ".locations", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Location.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Location.class.getName() + ".parkingAreas", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Location.class.getName() + ".routes", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.ParkingArea.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.ParkingArea.class.getName() + ".parkingSlots", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.ParkingSlot.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.LocationType.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.TransportType.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.VehicleFacility.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.VehicleFacility.class.getName() + ".vehicles", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.VehicleFacility.class.getName() + ".schedules", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Vehicle.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Vehicle.class.getName() + ".vehicleFacilities", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Vehicle.class.getName() + ".trips", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.VehicleOwner.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.VehicleOwner.class.getName() + ".vehicles", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Schedule.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Schedule.class.getName() + ".weekdays", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Schedule.class.getName() + ".vehicleFacilities", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Schedule.class.getName() + ".trips", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Trip.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Weekday.class.getName(), jcacheConfiguration);
            cm.createCache(lk.npsp.domain.Route.class.getName() + ".routeLocations", jcacheConfiguration);
            cm.createCache(lk.npsp.domain.RouteLocation.class.getName(), jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }
}
