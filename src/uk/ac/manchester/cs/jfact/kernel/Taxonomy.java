package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.*;

import java.util.*;

import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.Original;
import conformance.PortedFrom;

@PortedFrom(file = "Taxonomy.h", name = "Taxonomy")
public class Taxonomy {
    /** array of taxonomy verteces */
    @PortedFrom(file = "Taxonomy.h", name = "Graph")
    private List<TaxonomyVertex> graph = new ArrayList<TaxonomyVertex>();
    @PortedFrom(file = "Taxonomy.h", name = "Syns")
    List<ClassifiableEntry> Syns = new ArrayList<ClassifiableEntry>();
    /** aux. vertex to be included to taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "Current")
    protected TaxonomyVertex current;
    /** vertex with parent Top and child Bot, represents the fresh entity */
    @PortedFrom(file = "Taxonomy.h", name = "FreshNode")
    TaxonomyVertex FreshNode = new TaxonomyVertex();
    /** pointer to currently classified entry */
    @PortedFrom(file = "Taxonomy.h", name = "curEntry")
    protected ClassifiableEntry curEntry;
    /** number of tested entryes */
    @PortedFrom(file = "Taxonomy.h", name = "nEntries")
    protected int nEntries;
    /** number of completely-defined entries */
    @PortedFrom(file = "Taxonomy.h", name = "nCDEntries")
    protected long nCDEntries;
    /** optimisation flag: if entry is completely defined by it's told subsumers,
     * no other classification required */
    @PortedFrom(file = "Taxonomy.h", name = "useCompletelyDefined")
    protected boolean useCompletelyDefined;
    /** behaviour flag: if true, insert temporary vertex into taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "willInsertIntoTaxonomy")
    protected boolean willInsertIntoTaxonomy;
    /** stack for Taxonomy creation */
    @PortedFrom(file = "Taxonomy.h", name = "waitStack")
    private LinkedList<ClassifiableEntry> waitStack = new LinkedList<ClassifiableEntry>();
    /** told subsumers corresponding to a given entry */
    @PortedFrom(file = "Taxonomy.h", name = "ksStack")
    protected LinkedList<KnownSubsumers> ksStack = new LinkedList<KnownSubsumers>();
    /** labellers for marking taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "checkLabel")
    protected long checkLabel = 1;
    @PortedFrom(file = "Taxonomy.h", name = "valueLabel")
    protected long valueLabel = 1;
    @Original
    private JFactReasonerConfiguration options;

    /** apply ACTOR to subgraph starting from NODE as defined by flags; this
     * version is intended to work only with SupConceptActor, which requires the
     * method to return as soon as the apply() method returns false */
    @PortedFrom(file = "Taxonomy.h", name = "getRelativesInfo")
    public boolean getRelativesInfo(TaxonomyVertex node, SupConceptActor actor,
            boolean needCurrent, boolean onlyDirect, boolean upDirection) {
        // if current node processed OK and there is no need to continue -- exit
        // this is the helper to the case like getDomain():
        // if there is a named concept that represent's a domain -- that's what
        // we need
        if (needCurrent) {
            if (!actor.apply(node)) {
                return false;
            }
            if (onlyDirect) {
                return true;
            }
        }
        Queue<List<TaxonomyVertex>> queue = new LinkedList<List<TaxonomyVertex>>();
        queue.add(node.neigh(upDirection));
        while (queue.size() > 0) {
            List<TaxonomyVertex> neigh = queue.remove();
            int size = neigh.size();
            for (int i = 0; i < size; i++) {
                TaxonomyVertex _node = neigh.get(i);
                // recursive applicability checking
                if (!_node.isChecked(checkLabel)) {
                    // label node as visited
                    _node.setChecked(checkLabel);
                    // if current node processed OK and there is no need to
                    // continue -- exit
                    // if node is NOT processed for some reasons -- go to
                    // another level
                    if (!actor.apply(_node)) {
                        return false;
                    }
                    if (onlyDirect) {
                        continue;
                    }
                    // apply method to the proper neighbours with proper
                    // parameters
                    queue.add(_node.neigh(upDirection));
                }
            }
        }
        clearCheckedLabel();
        return true;
    }

    /** apply ACTOR to subgraph starting from NODE as defined by flags; */
    @PortedFrom(file = "Taxonomy.h", name = "getRelativesInfo")
    public void getRelativesInfo(TaxonomyVertex node, Actor actor, boolean needCurrent,
            boolean onlyDirect, boolean upDirection) {
        // if current node processed OK and there is no need to continue -- exit
        // this is the helper to the case like getDomain():
        // if there is a named concept that represent's a domain -- that's what
        // we need
        if (needCurrent && actor.apply(node) && onlyDirect) {
            return;
        }
        Queue<List<TaxonomyVertex>> queue = new LinkedList<List<TaxonomyVertex>>();
        queue.add(node.neigh(upDirection));
        while (queue.size() > 0) {
            List<TaxonomyVertex> neigh = queue.remove();
            int size = neigh.size();
            for (int i = 0; i < size; i++) {
                TaxonomyVertex _node = neigh.get(i);
                // recursive applicability checking
                if (!_node.isChecked(checkLabel)) {
                    // label node as visited
                    _node.setChecked(checkLabel);
                    // if current node processed OK and there is no need to
                    // continue -- exit
                    // if node is NOT processed for some reasons -- go to
                    // another level
                    if (actor.apply(_node) && onlyDirect) {
                        continue;
                    }
                    // apply method to the proper neighbours with proper
                    // parameters
                    queue.add(_node.neigh(upDirection));
                }
            }
        }
        clearCheckedLabel();
    }

    /** clear the CHECKED label from all the taxonomy vertex */
    @PortedFrom(file = "Taxonomy.h", name = "clearCheckedLabel")
    protected void clearCheckedLabel() {
        checkLabel++;
    }

    @PortedFrom(file = "Taxonomy.h", name = "clearLabels")
    protected void clearLabels() {
        checkLabel++;
        valueLabel++;
    }

    /** initialise aux entry with given concept p */
    @PortedFrom(file = "Taxonomy.h", name = "setCurrentEntry")
    protected void setCurrentEntry(ClassifiableEntry p) {
        current.clear();
        curEntry = p;
    }

    /** check if no classification needed (synonym, orphan, unsatisfiable) */
    @PortedFrom(file = "Taxonomy.h", name = "immediatelyClassified")
    protected boolean immediatelyClassified() {
        return classifySynonym();
    }

    /** check if it is possible to skip TD phase */
    @PortedFrom(file = "Taxonomy.h", name = "needTopDown")
    protected boolean needTopDown() {
        return false;
    }

    /** explicitely run TD phase */
    @PortedFrom(file = "Taxonomy.h", name = "runTopDown")
    protected void runTopDown() {}

    /** check if it is possible to skip BU phase */
    @PortedFrom(file = "Taxonomy.h", name = "needBottomUp")
    protected boolean needBottomUp() {
        return false;
    }

    /** explicitely run BU phase */
    @PortedFrom(file = "Taxonomy.h", name = "runBottomUp")
    protected void runBottomUp() {}

    /** actions that to be done BEFORE entry will be classified */
    @PortedFrom(file = "Taxonomy.h", name = "preClassificationActions")
    protected void preClassificationActions() {}

    // -- DFS-based classification
    /** add top entry together with its known subsumers */
    @PortedFrom(file = "Taxonomy.h", name = "addTop")
    private void addTop(ClassifiableEntry p) {
        waitStack.push(p);
        ksStack.push(new ToldSubsumers(p.getToldSubsumers()));
    }

    /** remove top entry */
    @PortedFrom(file = "Taxonomy.h", name = "removeTop")
    protected void removeTop() {
        waitStack.pop();
        ksStack.pop();
    }

    /** check if it is necessary to log taxonomy action */
    @PortedFrom(file = "Taxonomy.h", name = "needLogging")
    protected boolean needLogging() {
        return true;
    }

    public Taxonomy(ClassifiableEntry pTop, ClassifiableEntry pBottom,
            JFactReasonerConfiguration c) {
        options = c;
        current = new TaxonomyVertex();
        curEntry = null;
        nEntries = 0;
        nCDEntries = 0;
        useCompletelyDefined = false;
        willInsertIntoTaxonomy = true;
        graph.add(new TaxonomyVertex(pBottom));
        graph.add(new TaxonomyVertex(pTop));
        // set up fresh node
        FreshNode.addNeighbour(true, getTopVertex());
        FreshNode.addNeighbour(false, getBottomVertex());
    }

    /** special access to TOP of taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "getTopVertex")
    public TaxonomyVertex getTopVertex() {
        return graph.get(1);
    }

    /** special access to BOTTOM of taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "getBottomVertex")
    public TaxonomyVertex getBottomVertex() {
        return graph.get(0);
    }

    /** get node for fresh entity E */
    @PortedFrom(file = "Taxonomy.h", name = "getFreshVertex")
    TaxonomyVertex getFreshVertex(ClassifiableEntry e) {
        FreshNode.setSample(e, false);
        return FreshNode;
    }

    // -- classification interface
    // flags interface
    /** set Completely Defined flag */
    @PortedFrom(file = "Taxonomy.h", name = "setCompletelyDefined")
    public void setCompletelyDefined(boolean use) {
        useCompletelyDefined = use;
    }

    /** call this method after taxonomy is built */
    @PortedFrom(file = "Taxonomy.h", name = "finalise")
    public void finalise() {
        // create links from leaf concepts to bottom
        boolean upDirection = false;
        // TODO maybe useful to index Graph
        for (int i = 1; i < graph.size(); i++) {
            TaxonomyVertex p = graph.get(i);
            if (p.noNeighbours(upDirection)) {
                p.addNeighbour(upDirection, getBottomVertex());
                getBottomVertex().addNeighbour(!upDirection, p);
            }
        }
        willInsertIntoTaxonomy = false;
        // after finalisation one shouldn't add
                                        // new entries to taxonomy
    }

    @PortedFrom(file = "Taxonomy.h", name = "setupTopDown")
    private void setupTopDown() {
        setToldSubsumers();
        if (!needTopDown()) {
            ++nCDEntries;
            setNonRedundantCandidates();
        }
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append("Taxonomy consists of ");
        o.append(nEntries);
        o.append(" entries\n            of which ");
        o.append(nCDEntries);
        o.append(" are completely defined\n\nAll entries are in format:\n\"entry\" {n: parent_1 ... parent_n} {m: child_1 child_m}\n\n");
        TreeSet<TaxonomyVertex> sorted = new TreeSet<TaxonomyVertex>(
                new Comparator<TaxonomyVertex>() {
                    @Override
                    public int compare(TaxonomyVertex o1, TaxonomyVertex o2) {
                        return o1.getPrimer().getName()
                                .compareTo(o2.getPrimer().getName());
                    }
                });
        sorted.addAll(graph.subList(1, graph.size()));
        for (TaxonomyVertex p : sorted) {
            o.append(p);
        }
        o.append(getBottomVertex());
        return o.toString();
    }

    @PortedFrom(file = "Taxonomy.h", name = "addCurrentToSynonym")
    public void addCurrentToSynonym(TaxonomyVertex syn) {
        if (queryMode()) {
            // no need to insert; just mark SYN as a host to curEntry
            syn.setHostVertex(curEntry);
        } else {
            syn.addSynonym(curEntry);
            options.getLog().print("\nTAX:set ", curEntry.getName(), " equal ",
                    syn.getPrimer().getName());
        }
    }

    @PortedFrom(file = "Taxonomy.h", name = "insertCurrentNode")
    void insertCurrentNode() {
        current.setSample(curEntry, true);
        // put curEntry as a representative of Current
        if (!queryMode())
        {
            // insert node into taxonomy
            current.incorporate(options);
            graph.add(current);
            // we used the Current so need to create a new one
            current = new TaxonomyVertex();
        }
    }

    /** @return true if taxonomy works in a query mode (no need to insert query
     *         vertex) */
    @PortedFrom(file = "Taxonomy.h", name = "queryMode")
    public boolean queryMode() {
        return !willInsertIntoTaxonomy;
    }

    /** remove node from the taxonomy; assume no references to the node */
    @PortedFrom(file = "Taxonomy.h", name = "removeNode")
    void removeNode(TaxonomyVertex node) {
        graph.remove(node);
    }

    /** @return true if V is a direct parent of current wrt labels */
    @PortedFrom(file = "Taxonomy.h", name = "isDirectParent")
    boolean isDirectParent(TaxonomyVertex v) {
        for (TaxonomyVertex q : v.neigh(false)) {
            if (q.isValued(valueLabel) && q.getValue()) {
                return false;
            }
        }
        return true;
    }

    @PortedFrom(file = "Taxonomy.h", name = "performClassification")
    private void performClassification() {
        // do something before classification (tunable)
        preClassificationActions();
        ++nEntries;
        options.getLog().print("\n\nTAX: start classifying entry ");
        options.getLog().print(curEntry.getName());
        // if no classification needed -- nothing to do
        if (immediatelyClassified()) {
            return;
        }
        // perform main classification
        generalTwoPhaseClassification();
        // create new vertex
        TaxonomyVertex syn = current.getSynonymNode();
        if (syn != null) {
            addCurrentToSynonym(syn);
        } else {
            insertCurrentNode();
        }
        // clear all labels
        clearLabels();
    }

    @PortedFrom(file = "Taxonomy.h", name = "generalTwoPhaseClassification")
    private void generalTwoPhaseClassification() {
        setupTopDown();
        if (needTopDown()) {
            getTopVertex().setValued(true, valueLabel);
            getBottomVertex().setValued(false, valueLabel);
            runTopDown();
        }
        clearLabels();
        if (needBottomUp()) {
            getBottomVertex().setValued(true, valueLabel);
            runBottomUp();
        }
        clearLabels();
    }

    @PortedFrom(file = "Taxonomy.h", name = "classifySynonym")
    protected boolean classifySynonym() {
        ClassifiableEntry syn = resolveSynonym(curEntry);
        if (syn.equals(curEntry)) {
            return false;
        }
      //  assert willInsertIntoTaxonomy;
        assert syn.getTaxVertex() != null;
        addCurrentToSynonym(syn.getTaxVertex());
        return true;
    }

    @PortedFrom(file = "Taxonomy.h", name = "setNonRedundantCandidates")
    private void setNonRedundantCandidates() {
        if (!curEntry.hasToldSubsumers()) {
            options.getLog().print("\nTAX: TOP");
        }
        options.getLog().print(" completely defines concept ");
        options.getLog().print(curEntry.getName());
        for (ClassifiableEntry p : ksStack.peek().s_begin()) {
            TaxonomyVertex par = p.getTaxVertex();
            if (par == null) {
                continue;
            }
            if (isDirectParent(par)) {
                current.addNeighbour(true, par);
            }
        }
    }

    @PortedFrom(file = "Taxonomy.h", name = "setToldSubsumers")
    private void setToldSubsumers() {
        Collection<ClassifiableEntry> top = ksStack.peek().s_begin();
        if (needLogging() && !top.isEmpty()) {
            options.getLog().print("\nTAX: told subsumers");
        }
        for (ClassifiableEntry p : top) {
            if (p.isClassified()) {
                if (needLogging()) {
                    options.getLog().printTemplate(Templates.TOLD_SUBSUMERS, p.getName());
                }
                propagateTrueUp(p.getTaxVertex());
            }
        }
        // XXX this is misleading: in the C++ code the only implementation
        // available will always say that top is empty here even if it never is.
        // if (!top.isEmpty() && needLogging()) {
        // LL.print(" and possibly ");
        // for (ClassifiableEntry q : top) {
        // LL.print(Templates.TOLD_SUBSUMERS, q.getName());
        // }
        // }
    }

    /** ensure that all TS of the top entry are classified. @return the reason of
     * cycle or NULL. */
    @PortedFrom(file = "Taxonomy.h", name = "prepareTS")
    ClassifiableEntry prepareTS(ClassifiableEntry cur) {
        // we just found that TS forms a cycle -- return stop-marker
        if (waitStack.contains(cur)) {
            return cur;
        }
        // starting from the topmost entry
        addTop(cur);
        // true iff CUR is a reason of the cycle
        boolean cycleFound = false;
        // for all the told subsumers...
        for (ClassifiableEntry p : ksStack.peek().s_begin()) {
            if (!p.isClassified())
            {
                // need to classify it first
                if (p.isNonClassifiable()) {
                    continue;
                }
                // prepare TS for *p
                ClassifiableEntry v = prepareTS(p);
                // if NULL is returned -- just continue
                if (v == null) {
                    continue;
                }
                if (v == cur)
                {
                    // current cycle is finished, all saved in Syns
                    // after classification of CUR we need to mark all the Syns
                    // as synonyms
                    cycleFound = true;
                    // continue to prepare its classification
                    continue;
                } else {
                    // arbitrary vertex in a cycle: save in synonyms of a root
                    // cause
                    Syns.add(cur);
                    // don't need to classify it
                    removeTop();
                    // return the cycle cause
                    return v;
                }
            }
        }
        // all TS are ready here -- let's classify!
        classifyTop();
        // now if CUR is the reason of cycle mark all SYNs as synonyms
        if (cycleFound) {
            TaxonomyVertex syn = cur.getTaxVertex();
            for (ClassifiableEntry q : Syns) {
                syn.addSynonym(q);
            }
            Syns.clear();
        }
        // here the cycle is gone
        return null;
    }

    @PortedFrom(file = "Taxonomy.h", name = "classifyEntry")
    public void classifyEntry(ClassifiableEntry p) {
        assert waitStack.isEmpty();
        // don't classify artificial concepts
        if (p.isNonClassifiable()) {
            return;
        }
        prepareTS(p);
    }

    @PortedFrom(file = "Taxonomy.h", name = "classifyTop")
    private void classifyTop() {
        assert !waitStack.isEmpty();
        // load last concept
        setCurrentEntry(waitStack.peek());
        if (options.isTMP_PRINT_TAXONOMY_INFO()) {
            options.getLog().print("\nTrying classify",
                    curEntry.isCompletelyDefined() ? " CD " : " ", curEntry.getName(),
                    "... ");
        }
        performClassification();
        if (options.isTMP_PRINT_TAXONOMY_INFO()) {
            options.getLog().print("done");
        }
        removeTop();
    }
    @PortedFrom(file = "Taxonomy.h", name = "propagateTrueUp")
    protected void propagateTrueUp(TaxonomyVertex node) {
        // if taxonomy class already checked -- do nothing
        if (node.isValued(valueLabel)) {
            assert node.getValue();
            return;
        }
        // overwise -- value it...
        node.setValued(true, valueLabel);
        // ... and value all parents
        List<TaxonomyVertex> list = node.neigh(true);
        for (int i = 0; i < list.size(); i++) {
            propagateTrueUp(list.get(i));
        }
    }

    /** abstract class to represent the known subsumers of a concept */
    abstract class KnownSubsumers {
        /** begin of the Sure subsumers interval */
        abstract List<ClassifiableEntry> s_begin();

        /** begin of the Possible subsumers interval */
        abstract List<ClassifiableEntry> p_begin();

        // flags
        /** whether there are no sure subsumers */
        boolean s_empty() {
            return s_begin().isEmpty();
        }

        /** whether there are no possible subsumers */
        boolean p_empty() {
            return p_begin().isEmpty();
        }

        /** @return true iff CE is the possible subsumer */
        boolean isPossibleSub(ClassifiableEntry ce) {
            return true;
        }
    }

    /** class to represent the TS's */
    class ToldSubsumers extends KnownSubsumers {
        /** two iterators for the TS of a concept */
        List<ClassifiableEntry> beg;

        public ToldSubsumers(Collection<ClassifiableEntry> b) {
            beg = new ArrayList<ClassifiableEntry>(b);
        }

        /** begin of the Sure subsumers interval */
        @Override
        List<ClassifiableEntry> s_begin() {
            return beg;
        }

        /** end of the Sure subsumers interval */
        /** begin of the Possible subsumers interval */
        @Override
        List<ClassifiableEntry> p_begin() {
            return Collections.emptyList();
        }
    }
}
