package uk.ac.manchester.cs.jfact.split;

import java.util.*;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.DLDag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.TSplitVar.Entry;
import conformance.Original;
import conformance.PortedFrom;

/** all split rules: vector of rules with init and access methods */
@PortedFrom(file = "tSplitExpansionRules.h", name = "TSplitRules")
public class TSplitRules {
    /** class to check whether there is a need to unsplit splitted var */
    public class TSplitRule {
        /** signature of equivalent part of the split */
        Set<NamedEntity> eqSig;
        /** signature of subsumption part of the split */
        Set<NamedEntity> impSig;
        /** pointer to split vertex to activate */
        int bpSplit;


        TSplitRule() {}

        /** init c'tor */
        TSplitRule(Set<NamedEntity> es, Set<NamedEntity> is, int p) {
            eqSig = new HashSet<NamedEntity>(es);
            impSig = new HashSet<NamedEntity>(is);
            bpSplit = p;
        }

        /** copy c'tor */
        TSplitRule(TSplitRule copy) {
            this(copy.eqSig, copy.impSig, copy.bpSplit);
        }

        // access methods
        /** get bipolar pointer of the rule */
        public int bp() {
            return bpSplit;
        }

        /** check whether signatures of a rule are related to current signature
         * in such a way that allows rule to fire */
        public boolean canFire(Set<NamedEntity> CurrentSig) {
            return CurrentSig.containsAll(eqSig) && intersectsWith(impSig, CurrentSig);
        }

        /** calculates dep-set for a rule that can fire, write it to DEP. */
        public DepSet
                fireDep(Set<NamedEntity> CurrentSig, Map<NamedEntity, DepSet> SigDep) {
            DepSet dep = DepSet.create();
            // eqSig is contained in current, so need all
            for (NamedEntity p : eqSig) {
                dep = DepSet.plus(dep, SigDep.get(p));
            }
            // impSig has partial intersect with current; 1st common entity is
            // fine
            for (NamedEntity p : impSig) {
                if (CurrentSig.contains(p)) {
                    dep = DepSet.plus(dep, SigDep.get(p));
                    break;
                }
            }
            return dep;
        }

        /** check whether set S1 intersects with the set S2 */
        boolean intersectsWith(Set<?> S1, Set<?> S2) {
            for (Object o : S1) {
                if (S2.contains(o)) {
                    return true;
                }
            }
            return false;
        }
    }

    /** all known rules */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "Base")
    List<TSplitRule> Base = new ArrayList<TSplitRule>();
    /** all entities that appears in all the splits in a set */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "PossibleSignature")
    Set<NamedEntity> PossibleSignature = new HashSet<NamedEntity>();
    /** map between BP and TNamedEntities */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "EntityMap")
    List<NamedEntity> EntityMap = new ArrayList<NamedEntity>();
    @Original
    private JFactReasonerConfiguration config;

    public TSplitRules(JFactReasonerConfiguration options) {
        config = options;
    }

    @PortedFrom(file = "tSplitExpansionRules.h", name = "begin")
    public List<TSplitRule> getRules() {
        return Base;
    }

    /** add new split rule */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "addSplitRule")
    void addSplitRule(Set<NamedEntity> eqSig, Set<NamedEntity> impSig, int bp) {
        Base.add(new TSplitRule(eqSig, impSig, bp));
    }

    /** calculate single entity based on a named entry ENTRY and possible
     * signature */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "getSingleEntity")
    NamedEntity getSingleEntity(NamedEntry entry) {
        if (entry == null) {
            return null;
        }
        NamedEntity ret = entry.getEntity();
        // now keep only known signature concepts
        return PossibleSignature.contains(ret) ? ret : null;
    }

    /** create all the split rules by given split set SPLITS */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "createSplitRules")
    public void createSplitRules(TSplitVars Splits) {
        for (TSplitVar p : Splits.getEntries()) {
            initSplit(p);
        }
    }

    /** ensure that Map has the same size as DAG, so there would be no access
     * violation */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "ensureDagSize")
    public void ensureDagSize(int dagSize) {
        Helper.resize(EntityMap, dagSize);
    }

    /** @return named entity corresponding to a given bp */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "getEntity")
    public NamedEntity getEntity(int bp) {
        return EntityMap.get(bp > 0 ? bp : -bp);
    }

    /** init entity map using given DAG. note that this should be done AFTER rule
     * splits are created! */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "initEntityMap")
    public void initEntityMap(DLDag Dag) {
        int size = Dag.size();
        Helper.resize(EntityMap, size);
        EntityMap.set(0, null);
        EntityMap.set(1, null);
        for (int i = 2; i < size - 1; ++i) {
            EntityMap.set(i, getSingleEntity(Dag.get(i).getConcept()));
        }
    }

    /** build a set out of signature SIG w/o given ENTITY */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "buildSet")
    Set<NamedEntity> buildSet(TSignature sig, NamedEntity entity) {
        Set<NamedEntity> set = new HashSet<NamedEntity>();
        for (NamedEntity p : sig.begin()) {
            if (p != entity && p instanceof ConceptName) {
                set.add(p);
            }
        }
        // register all elements in the set in PossibleSignature
        PossibleSignature.addAll(set);
        return set;
    }

    /** init split as a set-of-sets */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "initSplit")
    void initSplit(TSplitVar split) {
        Entry p = split.getEntries().get(0);
        Set<NamedEntity> impSet = buildSet(p.sig, p.name);
        int bp = split.C.getpBody() + 1;
        // choose-rule stays next to a split-definition of C
        for (int i = 1; i < split.getEntries().size(); i++) {
            p = split.getEntries().get(i);
            if (p.Module.size() == 1) {
                addSplitRule(buildSet(p.sig, p.name), impSet, bp);
            } else {
                // make set of all the seed signatures of for p.Module
                Set<TSignature> Out = new HashSet<TSignature>();
                // prepare vector of available entities
                List<NamedEntity> Allowed = new ArrayList<NamedEntity>();
                List<Axiom> Module = new ArrayList<Axiom>(p.Module);
                // prepare signature for the process
                TSignature sig = p.sig;
                prepareStartSig(Module, sig, Allowed);
                // build all the seed sigs for p.sig
                BuildAllSeedSigs(Allowed, sig, Module, Out);
                for (TSignature q : Out) {
                    addSplitRule(buildSet(q, p.name), impSet, bp);
                }
            }
        }
    }

    /** prepare start signature */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "prepareStartSig")
    void prepareStartSig(List<Axiom> Module, TSignature sig, List<NamedEntity> Allowed) {
        // remove all defined concepts from signature
        for (Axiom p : Module) {
            if (p instanceof AxiomEquivalentConcepts) {
                // we don't need class names here
                for (ConceptExpression q : ((AxiomEquivalentConcepts) p).getArguments()) {
                    // FIXME!! check for the case A=B for named classes
                    if (q instanceof ConceptName) {
                        sig.remove((ConceptName) q);
                    }
                }
            } else {
                if (!(p instanceof AxiomConceptInclusion)) {
                    continue;
                }
                // don't need the left-hand part either if it is a name
                ConceptExpression c = ((AxiomConceptInclusion) p).getSubConcept();
                if (c instanceof ConceptName) {
                    sig.remove((ConceptName) c);
                }
            }
        }
        // now put every concept name into Allowed
        for (NamedEntity r : sig.begin()) {
            if (r instanceof ConceptName) {
                // concept name
                Allowed.add(r);
            }
        }
    }

    /** build all the seed signatures */
    @PortedFrom(file = "tSplitExpansionRules.h", name = "BuildAllSeedSigs")
    void BuildAllSeedSigs(List<NamedEntity> Allowed, TSignature StartSig,
            List<Axiom> Module, Set<TSignature> Out) {
        // copy the signature
        TSignature sig = StartSig;
        // create a set of allowed entities for the next round
        List<NamedEntity> RecAllowed = new ArrayList<NamedEntity>();
        List<NamedEntity> Keepers = new ArrayList<NamedEntity>();
        Set<Axiom> outModule = new HashSet<Axiom>();
        TModularizer mod = new TModularizer(config, new SyntacticLocalityChecker());
        for (NamedEntity p : Allowed) {
            if (sig.containsNamedEntity(p)) {
                sig.remove(p);
                outModule.addAll(mod.extractModule(Module, sig, ModuleType.M_STAR));
                if (mod.getModule().size() == Module.size()) {
                    // possible to remove one
                    RecAllowed.add(p);
                } else {
                    Keepers.add(p);
                }
                sig.add(p);
            }
        }
        if (RecAllowed.isEmpty())
        {
            // minimal seed signature
            Out.add(StartSig);
            return;
        }
        if (!Keepers.isEmpty()) {
            for (NamedEntity p : RecAllowed) {
                sig.remove(p);
            }
            outModule.addAll(mod.extractModule(Module, sig, ModuleType.M_STAR));
            if (mod.getModule().size() == Module.size()) {
                Out.add(sig);
                return;
            }
        }
        // need to try smaller sigs
        sig = StartSig;
        for (NamedEntity p : RecAllowed) {
            sig.remove(p);
            BuildAllSeedSigs(RecAllowed, sig, Module, Out);
            sig.add(p);
        }
    }
}
