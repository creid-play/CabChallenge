package com.creid.app;

import com.creid.utils.RequestCache;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class MyApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> h = new HashSet<>();
        h.add( MedallionTrips.class );
        h.add( Cache.class );
        return h;
    }

    public Set<Object> getSingletons() {
        Set<Object> h = new HashSet<>();
        h.add( new RequestCache() );
        return h;
    }
}