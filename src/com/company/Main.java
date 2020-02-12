package com.company;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        ReferenceQueue<Map> referenceQueue = new ReferenceQueue<>();
        List<MapFinalizer> mapFinalizerMap = new ArrayList<>();
        List<Map> mapList = new ArrayList<>();

        for (int i = 0 ; i < 20 ; i++){
            Map map = new HashMap(){
                {
                    put("Test1", "Test2");
                    put("Test3", "Test4");
                }
            };
            mapList.add(map);
            mapFinalizerMap.add(new MapFinalizer(map, referenceQueue));
        }


        //Object is not enqueue (strong references)
        //All finalizers has referent
        mapFinalizerMap
                .forEach(x -> System.out.println("In GC Queue? " + x.isEnqueued()));




        System.out.println("Remove map list");
        mapList = null;
        System.gc();

        //Object is in queue
        //All finalizers don't have any referent
        mapFinalizerMap
                .forEach(x -> System.out.println("In GC Queue? " + x.isEnqueued()));


        Reference<?> reference;
        while((reference = referenceQueue.poll()) != null){
            System.out.println("In GC QUEUE: " + reference.isEnqueued());
        }
    }
}


class MapFinalizer extends PhantomReference<Map> {

    public MapFinalizer(Map referent, ReferenceQueue<? super Map> q) {
        super(referent, q);
    }
}


