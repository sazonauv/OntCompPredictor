package uk.ac.manchester.cs.jfact.datatypes;

import java.util.*;

/** @author ignazio */
public class DatatypeIntersection<T extends Comparable<T>> implements
        DatatypeCombination<DatatypeIntersection<T>, Datatype<T>> {
    private final Set<Datatype<T>> basics = new HashSet<Datatype<T>>();
    private final String uri;
    private final Datatype<T> host;

    /** @param c
     * @return datatype host for a set of datatypes */
    public static Datatype<?> getHostDatatype(Collection<Datatype<?>> c) {
        List<Datatype<?>> list = new ArrayList<Datatype<?>>(c);
        // all types need to be compatible, or the intersection cannot be
        // anything but empty
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (!list.get(i).isCompatible(list.get(j))) {
                    return null;
                }
            }
        }
        // the most specific type needs to be returned
        int old_size;
        do {
            old_size = list.size();
            for (int i = 0; i < list.size() - 1;) {
                if (list.get(i).isSubType(list.get(i + 1))) {
                    list.remove(i + 1);
                } else if (list.get(i + 1).isSubType(list.get(i))) {
                    list.remove(i);
                } else {
                    i++;
                }
            }
        } while (list.size() > 1 && old_size != list.size());
        // now if list.size >1, there is no single most specific type...
        // troubles
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /** @param host */
    public DatatypeIntersection(Datatype<T> host) {
        uri = "intersection#a" + DatatypeFactory.getIndex();
        this.host = host;
    }

    /** @param host
     * @param list */
    public DatatypeIntersection(Datatype<T> host, Iterable<Datatype<T>> list) {
        this(host);
        for (Datatype<T> d : list) {
            basics.add(d);
        }
    }

    @Override
    public Datatype<?> getHost() {
        return host;
    }

    @Override
    public Iterable<Datatype<T>> getList() {
        return basics;
    }

    @Override
    public DatatypeIntersection<T> add(Datatype<T> d) {
        DatatypeIntersection<T> toReturn = new DatatypeIntersection<T>(host, basics);
        toReturn.basics.add(d);
        return toReturn;
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(l)) {
            return false;
        }
        for (Datatype<?> d : basics) {
            if (!d.isCompatible(l)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDatatypeURI() {
        return uri;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(type)) {
            return false;
        }
        for (Datatype<?> d : basics) {
            if (!d.isCompatible(type)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean emptyValueSpace() {
        // all base types must be numeric
        if (!host.getNumeric()) {
            return false;
        }
        Comparable min = null;
        Comparable max = null;
        // all intervals must intersect - i.e., the interval with max min
        // (excluded if any interval excludes it), min max (excluded if any
        // interval excludes it) must contain at least one element
        // get max minimum value
        boolean minExclusive = false;
        boolean maxExclusive = false;
        for (Datatype<T> dt : basics) {
            Comparable facetValue = dt
                    .asNumericDatatype().getMin();
            if (facetValue != null) {
                if (min == null || min.compareTo(facetValue) < 0) {
                    min = facetValue;
                }
            }
            facetValue = dt.asNumericDatatype().getMax();
            if (facetValue != null) {
                if (max == null || facetValue.compareTo(max) < 0) {
                    max = facetValue;
                }
            }
            if (dt.asNumericDatatype().hasMinExclusive()) {
                minExclusive = true;
            }
            if (dt.asNumericDatatype().hasMaxExclusive()) {
                maxExclusive = true;
            }
        }
        int excluded = 0;
        if (minExclusive) {
            excluded++;
        }
        if (maxExclusive) {
            excluded++;
        }
        return !DatatypeFactory.nonEmptyInterval(min, max, excluded);
    }

    @Override
    public String toString() {
        return uri + "{" + basics + "}";
    }
}
