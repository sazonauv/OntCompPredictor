package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import conformance.Original;
import conformance.PortedFrom;

@PortedFrom(file = "RAutomaton.h", name = "RATransition")
public class RATransition {
    /** set of roles that may affect the transition */
    @PortedFrom(file = "RAutomaton.h", name = "label")
    private Set<Role> label;
    @Original
    BitSet cache = null;
    /** state of the transition */
    @PortedFrom(file = "RAutomaton.h", name = "state")
    private int state;

    /** create a transition to given state */
    public RATransition(int st) {
        state = st;
        label = new LinkedHashSet<Role>();
    }

    /** create a transition with a given label R to given state ST */
    public RATransition(int st, Role R) {
        this(st);
        label.add(R);
    }

    /** add label of transition TRANS to transition's label */
    @PortedFrom(file = "RAutomaton.h", name = "add")
    public void add(RATransition trans) {
        label.addAll(trans.label);
        cache = null;
    }

    // query the transition
    /** get the 1st role in (multi-)transition */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public Collection<Role> begin() {
        return label;
    }

    /** give a point of the transition */
    @PortedFrom(file = "RAutomaton.h", name = "final")
    public int final_state() {
        return state;
    }

    /** check whether transition is applicable wrt role R */
    @PortedFrom(file = "RAutomaton.h", name = "applicable")
    public boolean applicable(Role R) {
        if (cache == null) {
            cache = new BitSet();
            for (Role t : label) {
                cache.set(t.getAbsoluteIndex());
            }
        }
        return cache.get(R.getAbsoluteIndex());
    }

    /** check whether transition is empty */
    @PortedFrom(file = "RAutomaton.h", name = "empty")
    public boolean isEmpty() {
        return label.isEmpty();
    }

    /** print the transition starting from FROM */
    @PortedFrom(file = "RAutomaton.h", name = "print")
    public void print(LogAdapter o, int from) {
        o.print("\n", from, " -- ");
        if (isEmpty()) {
            o.print("e");
        } else {
            List<Role> l = new ArrayList<Role>(label);
            for (int i = 0; i < l.size(); i++) {
                if (i > 0) {
                    o.print(",");
                }
                o.print("\"");
                o.print(l.get(i).getName());
                o.print("\"");
            }
        }
        o.print(" -> ");
        o.print(final_state());
    }

    /** check whether transition is TopRole one */
    @PortedFrom(file = "RAutomaton.h", name = "isTop")
    boolean isTop() {
        return label.size() == 1 && label.iterator().next().isTop();
    }
}
