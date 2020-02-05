package xbrlcore.taxonomy;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a RoleType within an XBRL Taxonomy as it is
 * described by the XBRL 2.1 Specification, which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/. <br/><br/>
 * 
 * @author Donny Zhang, Edward Wang
 */

public class RoleType implements Serializable {

	static final long serialVersionUID = 6207994631304824560L;

	private String roleURI;

    private String id;

    private List<String> definition;

    private List<String> usedOn;

    /**
     * 
     * @return roleURI of the RoleType.
     */
    public String getRoleURI() {
		return roleURI;
	}

    /**
     * @param roleURI
     *            roleURI of the RoleType.
     */
	public void setRoleURI(String roleURI) {
		this.roleURI = roleURI;
	}

    /**
     * 
     * @return ID of the RoleType.
     */
	public String getId() {
		return id;
	}

    /**
     * @param id
     *            id of the RoleType.
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * 
     * @return definition list of the RoleType.
     */
	public List<String> getDefinition() {
		return definition;
	}

    /**
     * @param definition
     *            definition list of the RoleType.
     */
	public void setDefinition(List<String> definition) {
		this.definition = definition;
	}

    /**
     * 
     * @return usedOn list of the RoleType.
     */
	public List<String> getUsedOn() {
		return usedOn;
	}

    /**
     * @param usedOn
     *            usedOn list of the RoleType.
     */
	public void setUsedOn(List<String> usedOn) {
		this.usedOn = usedOn;
	}
}

