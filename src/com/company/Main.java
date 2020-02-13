package com.company;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        //Create ReferenceQueue for Map references
        ReferenceQueue<Map> referenceQueue = new ReferenceQueue<>();
        //Create MapFinalizer that is PhantomReference
        List<MapFinalizer> mapFinalizerMap = new ArrayList<>();
        //Lists of maps
        List<Map> mapList = new ArrayList<>();

        //started creating new maps and populate array
        for (int i = 0 ; i < 20 ; i++){
            //create new map with random values
            Map map = new HashMap();
            for (int x = 0 ; x < 20 ; x++) {
                map.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            }
            //add new map to list of map
            mapList.add(map);
            //add reference to spy for given map - create new MapFinalizer(PhantomReference) with queue
            mapFinalizerMap.add(new MapFinalizer(map, referenceQueue));
        }

        //Object is not enqueue (still strong references)
        //All finalizers has referent
        displayIfObjectAreGcEnqued((List<MapFinalizer>) mapFinalizerMap);

        //ReferenceQueue is empty for now
        System.out.println("ReferenceQueue is empty? " + (referenceQueue.poll() == null));
        //Remove map list strong reference
        mapList = null;
        //need to to "suggest" gc to start work - just suggestion :)
        System.gc();

        //Object is in queue
        //All finalizers don't have any referent
        displayIfObjectAreGcEnqued(mapFinalizerMap);

        //Right now after GC cleans, all references should be visible in referenceQueue
        Reference<?> reference;
        while((reference = referenceQueue.poll()) != null){
            //remove from queue
            reference.clear();
        }

        //So in case of normal application - we can use PhantomReference to be sure that some "big" object is removed from heap before we want to take some other
    }

    private static void displayIfObjectAreGcEnqued(List<MapFinalizer> mapFinalizerMap) {
        mapFinalizerMap
                .forEach(x -> System.out.println("In GC Queue? " + x.isEnqueued()));
    }
}


class MapFinalizer extends PhantomReference<Map> {

    public MapFinalizer(Map referent, ReferenceQueue<? super Map> q) {
        super(referent, q);
    }
}


