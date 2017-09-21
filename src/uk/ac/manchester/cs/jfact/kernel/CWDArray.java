package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.ArrayIntMap;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import conformance.Original;
import conformance.PortedFrom;

@PortedFrom(file = "CWDArray.h", name = "CWDArray")
public class CWDArray {
    @Original
    private static double distribution = 0.025;
    /** array of concepts together with dep-sets */
    @PortedFrom(file = "CWDArray.h", name = "Base")
    private List<ConceptWDep> base = new ArrayList<ConceptWDep>();
    @Original
    private BitSet cache;
    @Original
    private ArrayIntMap indexes = new ArrayIntMap();
    @Original
    private boolean createCache = false;
    @Original
    private static int cacheLimit = 1;
    @Original
    private int size = 0;

    /** init/clear label */
    @PortedFrom(file = "CWDArray.h", name = "init")
    public void init() {
        base.clear();
        cache = null;
        indexes.clear();
        createCache = false;
        size = 0;
    }

    @PortedFrom(file = "CWDArray.h", name = "begin")
    public List<ConceptWDep> getBase() {
        return base;
    }

    @Original
    public ArrayIntMap getContainedConcepts() {
        return indexes;
    }

    /** adds concept P to a label - to be called only from CGLabel */
    @Original
    protected void private_add(ConceptWDep p) {
        base.add(p);
        size++;
        if (cache != null) {
            cache.set(asPositive(p.getConcept()));
        }
        indexes.put(p.getConcept(), size - 1);
        int span = Math.max(asPositive(indexes.keySet(0)),
                indexes.keySet(indexes.size() - 1));
        // create a cache only if the size is higher than a preset minimum and
        // there is at least an element in 20; caches with very dispersed
        // elements eat up too much memory
        createCache = size > cacheLimit && (double) size / (span + 1) > distribution;
    }

    /** check whether label contains BP (ignoring dep-set) */
    @PortedFrom(file = "CWDArray.h", name = "contains")
    public boolean contains(int bp) {
        if (cache == null && createCache) {
            initCache();
        }
        if (cache != null) {
            return cache.get(asPositive(bp));
        } else {
            return indexes.containsKey(bp);
        }
    }

    @Original
    private void initCache() {
        cache = new BitSet();
        for (int i = 0; i < indexes.size(); i++) {
            cache.set(asPositive(indexes.keySet(i)));
        }
    }

    @Original
    int asPositive(int p) {
        return p >= 0 ? 2 * p : 1 - 2 * p;
    }

    @PortedFrom(file = "CWDArray.h", name = "index")
    public int index(int bp) {
        // check that the index actually exist: quicker
        if (cache != null && !cache.get(asPositive(bp))) {
            return -1;
        }
        return indexes.get(bp);
    }

    @PortedFrom(file = "CWDArray.h", name = "get")
    public DepSet get(int bp) {
        // check that the index actually exist: quicker
        if (cache != null && !cache.get(asPositive(bp))) {
            return null;
        }
        int i = indexes.get(bp);
        if (i < 0) {
            return null;
        }
        return base.get(i).getDep();
    }

    @Original
    public ConceptWDep getConceptWithBP(int bp) {
        // check that the index actually exist: quicker
        if (cache != null && !cache.get(asPositive(bp))) {
            return null;
        }
        int i = indexes.get(bp);
        if (i < 0) {
            return null;
        }
        return base.get(i);
    }

    @PortedFrom(file = "CWDArray.h", name = "size")
    public int size() {
        return size;
    }

    @PortedFrom(file = "CWDArray.h", name = "<=")
    public boolean lesserequal(CWDArray label) {
        // use the cache on the label if there is one
        if (label.cache != null) {
            for (int i = 0; i < indexes.size(); i++) {
                if (!label.cache.get(asPositive(indexes.keySet(i)))) {
                    return false;
                }
            }
            return true;
        }
        // checks the keys are in both maps
        return label.indexes.containsAll(indexes);
    }

    @Override
    public int hashCode() {
        return indexes.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof CWDArray) {
            CWDArray obj2 = (CWDArray) obj;
            return indexes.equals(obj2.indexes);
        }
        return false;
    }

    /** save label using given SS */
    @PortedFrom(file = "CWDArray.h", name = "save")
    public int save() {
        return size;
    }

    @PortedFrom(file = "CWDArray.h", name = "updateDepSet")
    public Restorer updateDepSet(int index, DepSet dep) {
        if (dep.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Restorer ret = new UnMerge(this, base.get(index), index);
        base.get(index).addDep(dep);
        return ret;
    }

    @PortedFrom(file = "CWDArray.h", name = "updateDepSet")
    public List<Restorer> updateDepSet(DepSet dep) {
        if (dep.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Restorer> toReturn = new ArrayList<Restorer>(size);
        for (int i = 0; i < size; i++) {
            Restorer ret = new UnMerge(this, base.get(i), i);
            base.get(i).addDep(dep);
            toReturn.add(ret);
        }
        return toReturn;
    }

    @PortedFrom(file = "CWDArray.h", name = "restore")
    public void restore(int ss, int level) {
        for (int i = ss; i < size; i++) {
            int concept = base.get(i).getConcept();
            indexes.remove(concept);
            if (cache != null) {
                cache.clear(asPositive(concept));
            }
        }
        Helper.resize(base, ss);
        size = ss;
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append(" [");
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                o.append(", ");
            }
            o.append(base.get(i));
        }
        o.append("]");
        return o.toString();
    }
}
