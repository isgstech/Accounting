package xbrlcore.instance;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import xbrlcore.exception.InstanceException;
import xbrlcore.taxonomy.Concept;

/**
 * This class represents a Tuple within an XBRL instance document as it is
 * described by the XBRL 2.1 Specification, which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/. <br/><br/>
 * 
 * @author Donny Zhang, Edward Wang
 */

public class Tuple implements Serializable {

    static final long serialVersionUID = -6417452592594243589L;

    private Concept concept;

    private Set<Fact> factSet = new HashSet<Fact>();

    private Set<Tuple> tupleSet = new HashSet<Tuple>();

    private String id;

    /**
     * Constructor.
     * 
     * @param concept
     *            Concept this tuple refers to.
     * @exception InstanceException
     *                This exception is thrown if the concept is NULL or
     *                abstract.
     */
    public Tuple(Concept concept) throws InstanceException {
        if (concept == null || concept.isAbstract()) {
            throw new InstanceException(
                    "Tuple cannot be created: Concept must be not null and not abstract");
        }
        this.concept = concept;
    }

    /**
     * @return Returns a hash code of this object.
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + concept.hashCode();
        hash = hash * 31 + (id != null ? id.hashCode() : 0);
        hash = hash * 31 + (factSet != null ? factSet.hashCode() : 0);
        hash = hash * 31 + (tupleSet != null ? tupleSet.hashCode() : 0);
        return hash;
    }

    /**
     * 
     * @return concept this tuple refers to.
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     * 
     * @return fact set of this tuple.
     */
    public Set<Fact> getFactSet() {
        return factSet;
    }

    /**
     * 
     * @param factSet
     *            fact set of this tuple.
     */
    public void setFactSet(Set<Fact> factSet) {
        this.factSet = factSet;
    }

    /**
     * 
     * @return tuple set of this tuple.
     */
    public Set<Tuple> getTupleSet() {
        return tupleSet;
    }

    /**
     * 
     * @param tupleSet
     *            tuple set of this tuple.
     */
    public void setTupleSet(Set<Tuple> tupleSet) {
        this.tupleSet = tupleSet;
    }

     /**
     * 
     * @return id of this tuple.
     */
    public String getID() {
        return id;
    }

    /**
     * 
     * @param id
     *            id of this tuple.
     */
    public void setID(String id) {
        this.id = id;
    }
}
