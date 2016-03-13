package pers.missingno.player.objects;

import java.util.LinkedList;

public class LinkedSet<E> extends LinkedList<E> {

    @Override
    public boolean offer(E o) {
        if(contains(o)){
            return false;
        }
        return super.offer(o);
    }

    @Override
    public boolean offerFirst(E e) {
        if(contains(e)){
            return false;
        }
        return super.offerFirst(e);
    }
}
