/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

import java.util.Comparator;

import org.LexGrid.concepts.Concept;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

/**
  * 
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

public class SortComparator implements Comparator<Object>{

    private static int SORT_BY_NAME = 1;
    private static int SORT_BY_CODE = 2;
    private int sort_option = SORT_BY_NAME;

    public SortComparator()
    {

	}

    public SortComparator(int sort_option)
    {
		this.sort_option = sort_option;
	}


    private String getKey(Object c, int sort_option)
    {
		if (c == null) return "NULL";
	    if (c instanceof org.LexGrid.concepts.Concept)
	    {
			org.LexGrid.concepts.Concept concept = (org.LexGrid.concepts.Concept) c;
			if (sort_option == SORT_BY_CODE) return concept.getId();
			return concept.getEntityDescription().getContent();

		}

	    else if (c instanceof AssociatedConcept)
	    {
			AssociatedConcept ac = (AssociatedConcept) c;
			if (sort_option == SORT_BY_CODE) return ac.getConceptCode();
			return ac.getEntityDescription().getContent();
		}

	    else if (c instanceof ResolvedConceptReference)
	    {
			ResolvedConceptReference ac = (ResolvedConceptReference) c;
			if (sort_option == SORT_BY_CODE) return ac.getConceptCode();
			return ac.getEntityDescription().getContent();
		}

	    else if (c instanceof String)
	    {
			String s = (String) c;
			return s;
		}

	    return c.toString();
    }



    public int compare(Object object1, Object object2) {
        String key1 = getKey(object1, sort_option);
        String key2 = getKey(object2, sort_option);
        return key1.compareTo(key2);
    }
}
