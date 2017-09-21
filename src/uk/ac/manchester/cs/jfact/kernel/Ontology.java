package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.split.TSplitVars;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;
import conformance.PortedFrom;

@PortedFrom(file = "tOntology.h", name = "TOntology")
public class Ontology {
    /** all the axioms */
    @PortedFrom(file = "tOntology.h", name = "Axioms")
    private List<Axiom> axioms = new ArrayList<Axiom>();
    /** expression manager that builds all the expressions for the axioms */
    @PortedFrom(file = "tOntology.h", name = "EManager")
    private ExpressionManager expressionManager = new ExpressionManager();
    /** id to be given to the next axiom */
    @PortedFrom(file = "tOntology.h", name = "axiomId")
    private int axiomId;
    /** true iff ontology was changed */
    @PortedFrom(file = "tOntology.h", name = "changed")
    private boolean changed;
    @PortedFrom(file = "tOntology.h", name = "Splits")
    public TSplitVars Splits = new TSplitVars();

    public Ontology() {
        axiomId = 0;
        changed = false;
    }

    @PortedFrom(file = "tOntology.h", name = "get")
    public Axiom get(int i) {
        return axioms.get(i);
    }

    /** @return true iff the ontology was changed since its last load */
    @PortedFrom(file = "tOntology.h", name = "isChanged")
    public boolean isChanged() {
        return changed;
    }

    /** set the processed marker to the end of the ontology */
    @PortedFrom(file = "tOntology.h", name = "setProcessed")
    public void setProcessed() {
        changed = false;
    }

    /** add given axiom to the ontology */
    @PortedFrom(file = "tOntology.h", name = "add")
    public Axiom add(Axiom p) {
        p.setId(++axiomId);
        axioms.add(p);
        changed = true;
        return p;
    }

    /** retract given axiom to the ontology */
    @PortedFrom(file = "tOntology.h", name = "retract")
    public void retract(Axiom p) {
        if (p.getId() <= axioms.size() && axioms.get(p.getId() - 1).equals(p)) {
            changed = true;
            p.setUsed(false);
        }
    }

    /** clear the ontology */
    @PortedFrom(file = "tOntology.h", name = "clear")
    public void clear() {
        axioms.clear();
        expressionManager.clear();
        changed = false;
    }

    // access to axioms
    /** get access to an expression manager */
    @PortedFrom(file = "tOntology.h", name = "getExpressionManager")
    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    /** RW begin() for the whole ontology */
    @PortedFrom(file = "tOntology.h", name = "getAxioms")
    public List<Axiom> getAxioms() {
        return axioms;
    }

    /** size of the ontology */
    @PortedFrom(file = "tOntology.h", name = "size")
    public int size() {
        return axioms.size();
    }

    /** accept method for the visitor pattern */
    public void accept(DLAxiomVisitor visitor) {
        visitor.visitOntology(this);
    }

    public <O> O accept(DLAxiomVisitorEx<O> visitor) {
        return visitor.visitOntology(this);
    }
}
