package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.util.PrintUtility;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.PropertyMatchValue;
import org.LexGrid.valueSets.PropertyReference;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.dto.ResolvedValueSetDefinition;


import java.io.*;
import java.net.URI;

import java.text.*;
import java.util.*;
import java.sql.*;
//import javax.faces.model.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.relations.Relations;
import org.LexGrid.versions.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;


import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;

//import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.relations.AssociationPredicate;


import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;


import org.apache.log4j.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
//import org.lexevs.property.PropertyExtension;
import org.json.*;



public class ValueSetHierarchy {

    public static String SOURCE_SCHEME = "Terminology Value Set";
    public static String SOURCE_VERSION = null;

	public static HashMap _source_hierarchy = null;
	public static HashMap _source_subconcept_map = null;
	public static HashMap _source_superconcept_map = null;


    static LocalNameList _noopList = Constructors.createLocalNameList("_noop_");
    private static Logger _logger = Logger.getLogger(ValueSetHierarchy.class);


    public static final String ONTOLOGY_NODE_ID = "ontology_node_id";

    public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";
    public static final String ONTOLOGY_NODE_PARENT_ASSOC =
        "ontology_node_parent_assoc";
    public static final String ONTOLOGY_NODE_CHILD_COUNT =
        "ontology_node_child_count";
    public static final String ONTOLOGY_NODE_DEFINITION =
        "ontology_node_definition";
    public static final String CHILDREN_NODES = "children_nodes";

    public static HashMap _valueSetDefinitionHierarchyHashMap = null;
    public static Vector  _availableValueSetDefinitionSources = null;
    public static Vector  _valueSetDefinitionHierarchyRoots = null;

	//private static String URL = "http://bmidev4:19280/lexevsapi60";
	//private static String URL = "http://ncias-d488-v.nci.nih.gov:29080/lexevsapi60";
	//private static String URL = "http://ncias-q532-v.nci.nih.gov:29080/lexevsapi60";

	private static String URL = "http://localhost:8080/lexevsapi60";


    public static String getVocabularyVersionByTag(String codingSchemeName,
        String ltag) {

        if (codingSchemeName == null)
            return null;
        String version = null;
        int knt = 0;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csra.length; i++) {
                CodingSchemeRendering csr = csra[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                if (css.getFormalName().compareTo(codingSchemeName) == 0
                    || css.getLocalName().compareTo(codingSchemeName) == 0
                    || css.getCodingSchemeURI().compareTo(codingSchemeName) == 0) {
					version = css.getRepresentsVersion();
                    knt++;

                    if (ltag == null)
                        return version;
                    RenderingDetail rd = csr.getRenderingDetail();
                    CodingSchemeTagList cstl = rd.getVersionTags();
                    java.lang.String[] tags = cstl.getTag();
                    // KLO, 102409
                    if (tags == null)
                        return version;

                    if (tags != null && tags.length > 0) {
                        for (int j = 0; j < tags.length; j++) {
                            String version_tag = (String) tags[j];

                            if (version_tag != null && version_tag.compareToIgnoreCase(ltag) == 0) {
                                return version;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //_logger.warn("Version corresponding to tag " + ltag + " is not found "
        //    + " in " + codingSchemeName);
        if (ltag != null && ltag.compareToIgnoreCase("PRODUCTION") == 0
            & knt == 1) {
            //_logger.warn("\tUse " + version + " as default.");
            return version;
        }
        return null;
    }
    public static String[] getHierarchyIDs(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        String[] hier = null;
        Set<String> ids = new HashSet<String>();
        SupportedHierarchy[] sh = null;
        try {
            sh = getSupportedHierarchies(codingScheme, versionOrTag);
            if (sh != null) {
                for (int i = 0; i < sh.length; i++) {
                    ids.add(sh[i].getLocalId());
                }

                // Cache and return the new value ...
                hier = ids.toArray(new String[ids.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hier;
    }

    protected static SupportedHierarchy[] getSupportedHierarchies(
        String codingScheme, CodingSchemeVersionOrTag versionOrTag)
            throws LBException {

        CodingScheme cs = null;
        try {
            cs = getCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {

        }
        if (cs == null) {
            throw new LBResourceUnavailableException(
                "Coding scheme not found -- " + codingScheme);
        }
        Mappings mappings = cs.getMappings();
        return mappings.getSupportedHierarchy();
    }

    protected static CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {
        CodingScheme cs = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }


    public static String getHierarchyID(String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            String[] ids = getHierarchyIDs(codingScheme, versionOrTag);
            if (ids.length > 0)
                return ids[0];
        } catch (Exception e) {

        }
        return null;
    }


    public static ResolvedConceptReferenceList getHierarchyRoots(
        String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            String hierarchyID = getHierarchyID(codingScheme, version);
            return lbscm.getHierarchyRoots(codingScheme, versionOrTag,
                hierarchyID);
        } catch (Exception e) {
            return null;
        }
    }


//===========================================================================================================================
// Value Set Hierarchy
//===========================================================================================================================


    public static ResolvedConceptReferenceList getValueSetHierarchyRoots() {
        String scheme = "Terminology Value Set";
        String version = getVocabularyVersionByTag(scheme, "PRODUCTION");
        return getHierarchyRoots(scheme, version);
	}

	public static void geValueSetHierarchy(HashMap hmap, Vector v) {

    }

	public static void geValueSetHierarchy() {
        HashMap hmap = new HashMap();
        ResolvedConceptReferenceList roots = getValueSetHierarchyRoots();
        Vector v = new Vector();
        for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
			v.add(rcr);
		}
        geValueSetHierarchy(hmap, v);
	}

    public static Vector<String> parseData(String line) {
		if (line == null) return null;
        String tab = "|";
        return parseData(line, tab);
    }

    public static Vector<String> parseData(String line, String tab) {
		if (line == null) return null;
        Vector data_vec = new Vector();
        StringTokenizer st = new StringTokenizer(line, tab);
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            if (value.compareTo("null") == 0)
                value = " ";
            data_vec.add(value);
        }
        return data_vec;
    }

    public static ValueSetDefinition findValueSetDefinitionByURI(String uri) {
		if (uri == null) return null;
	    if (uri.indexOf("|") != -1) {
			Vector u = parseData(uri);
			uri = (String) u.elementAt(1);
		}

		String valueSetDefinitionRevisionId = null;
		try {
			LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			return vsd;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public static Vector getValueSetDefinitionsBySource(String source) {
		if (_availableValueSetDefinitionSources != null) {
			if (!_availableValueSetDefinitionSources.contains(source)) return null;
		}
		Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
        if (list == null) return null;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();
            boolean found = false;
			while (sourceEnum.hasMoreElements()) {
				Source src = (Source) sourceEnum.nextElement();
				String src_str = src.getContent();
				if (src_str.compareTo(source) == 0) {
					v.add(vsd);
					break;
				}
			}
		}
		return v;
	}



    public static Vector getAvailableValueSetDefinitionSources() {
		if (_availableValueSetDefinitionSources != null) return _availableValueSetDefinitionSources;
		_availableValueSetDefinitionSources = new Vector();
		HashSet hset = new HashSet();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
        if (list == null) return null;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

			while (sourceEnum.hasMoreElements()) {
				Source src = (Source) sourceEnum.nextElement();
				String src_str = src.getContent();
				if (!hset.contains(src_str)) {
					hset.add(src_str);
					_availableValueSetDefinitionSources.add(src_str);
				}
			}
		}
		return _availableValueSetDefinitionSources;
	}


    public static String getValueSetDefinitionMetadata(ValueSetDefinition vsd) {
		if (vsd== null) return null;
		String name = "";
		String uri = "";
		String description = "";
		String domain = "";
		String src_str = "";

		uri = vsd.getValueSetDefinitionURI();
		name = vsd.getValueSetDefinitionName();
		if (name == null || name.compareTo("") == 0) {
			name = "<NOT ASSIGNED>";
		}

		domain = vsd.getConceptDomain();
		if (domain == null || domain.compareTo("") == 0) {
			domain = "<NOT ASSIGNED>";
		}

		java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

		while (sourceEnum.hasMoreElements()) {
			Source src = (Source) sourceEnum.nextElement();
			src_str = src_str + src.getContent() + ";";
		}
		if (src_str.length() > 0) {
			src_str = src_str.substring(0, src_str.length()-1);
		}

		if (src_str == null || src_str.compareTo("") == 0) {
			src_str = "<NOT ASSIGNED>";
		}

		if (vsd.getEntityDescription() != null) {
			description = vsd.getEntityDescription().getContent();
			if (description == null || description.compareTo("") == 0) {
				description = "<NO DESCRIPTION>";
			}
		} else {
			description = "<NO DESCRIPTION>";
		}

		return name + "|" + uri + "|" + description + "|" + domain + "|" + src_str;
	}



/*

    public static void printValueSetDefinitionHierarchyNode(int level, String root) {
		String indent = "";
		for (int i=0; i<level; i++) {
			indent = indent + "\t";
		}
		System.out.println(indent + root);
		Vector children = (Vector) _valueSetDefinitionHierarchyHashMap.get(root);
		if (children != null) {
			for (int j=0; j<children.size(); j++) {
				String child = (String) children.elementAt(j);
                printValueSetDefinitionHierarchyNode(level+1, child);
			}
		}
	}


    public static void printValueSetDefinitionHierarchy() {
		HashMap hmap = getValueSetDefinitionHierarchy();
		Vector roots = getValueSetDefinitionHierarchyRoots();
		for (int i=0; i<roots.size(); i++) {
			String root = (String) roots.elementAt(i);
			printValueSetDefinitionHierarchyNode(0, root);
		}

	}


    public static Vector getValueSetDefinitionHierarchyRoots() {
		if (_valueSetDefinitionHierarchyRoots != null) return _valueSetDefinitionHierarchyRoots;

		_valueSetDefinitionHierarchyRoots = new Vector();
		_valueSetDefinitionHierarchyRoots = getAvailableValueSetDefinitionSources();
		HashMap hmap = getValueSetDefinitionHierarchy();


		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Vector children = (Vector) hmap.get(key);
			for (int i=0; i<children.size(); i++) {
			    String child = (String) children.elementAt(i);
			    _valueSetDefinitionHierarchyRoots.remove(child);
			}
		}
		_valueSetDefinitionHierarchyRoots = SortUtils.quickSort(_valueSetDefinitionHierarchyRoots);
		return _valueSetDefinitionHierarchyRoots;
	}


    public static HashMap getValueSetDefinitionHierarchy() {
		if (_valueSetDefinitionHierarchyHashMap != null) return _valueSetDefinitionHierarchyHashMap;
		Vector v = getAvailableValueSetDefinitionSources();

		System.out.println("source size: " + v.size());
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			System.out.println("\tsource: " + t);
		}

		_valueSetDefinitionHierarchyHashMap = new HashMap();
		if (v.size() <= 1) return _valueSetDefinitionHierarchyHashMap;

		for (int i=0; i<v.size(); i++) {
			String s = (String) v.elementAt(i);
			for (int j=i+1; j<v.size(); j++) {
				String t = (String) v.elementAt(j);
				// if s is a substring of t
				//FDA  FDA_SPL

				System.out.println("s: " + s + "   ; t: " + t);
				System.out.println("\tt.indexOf(s): " + t.indexOf(s));
				System.out.println("\ts.indexOf(t): " + s.indexOf(t));

				if (t.indexOf(s) == -1 && s.indexOf(t) != -1) {
					Vector w = null;
					if (!_valueSetDefinitionHierarchyHashMap.containsKey(t)) {
						w = new Vector();
					} else {
						w = (Vector) _valueSetDefinitionHierarchyHashMap.get(t);
					}
					if (!w.contains(s)) w.add(s);
					_valueSetDefinitionHierarchyHashMap.put(t, w);
				} else if (t.indexOf(s) != -1 && s.indexOf(t) == -1) {
					Vector w = null;
					if (!_valueSetDefinitionHierarchyHashMap.containsKey(s)) {
						w = new Vector();
					} else {
						w = (Vector) _valueSetDefinitionHierarchyHashMap.get(s);
					}
					if (!w.contains(t)) w.add(t);
					_valueSetDefinitionHierarchyHashMap.put(s, w);
				}
			}
		}

		Iterator it = _valueSetDefinitionHierarchyHashMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Vector w = (Vector) _valueSetDefinitionHierarchyHashMap.get(key);
			w = SortUtils.quickSort(w);
			_valueSetDefinitionHierarchyHashMap.put(key, w);
		}
		return _valueSetDefinitionHierarchyHashMap;
	}


	public static Vector getValueSetDefinitionMetadata() {
		Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
        if (list == null) return null;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			String metadata = getValueSetDefinitionMetadata(vsd);
			v.add(metadata);
		}
		return SortUtils.quickSort(v);
	}

	public static String getValueSetDefinitionMetadata(String vsd_uri) {
		ValueSetDefinition vsd = findValueSetDefinitionByURI(vsd_uri);
		if (vsd == null) return null;
		return getValueSetDefinitionMetadata(vsd);
	}




    public static Vector getCodingSchemesInValueSetDefinition(String uri) {
		HashSet hset = new HashSet();
		try {
			java.net.URI valueSetDefinitionURI = new URI(uri);
			Vector v = new Vector();
			try {
				LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
				AbsoluteCodingSchemeVersionReferenceList codingSchemes =
					vsd_service.getCodingSchemesInValueSetDefinition(valueSetDefinitionURI);

				//output is all of the mapping ontologies that this code participates in.
				for(AbsoluteCodingSchemeVersionReference ref : codingSchemes.getAbsoluteCodingSchemeVersionReference()){
					String urn = ref.getCodingSchemeURN();
					System.out.println("URI: " + ref.getCodingSchemeURN());
					if (!hset.contains(urn)) {
					System.out.println("Version: " + ref.getCodingSchemeVersion());
					    v.add(ref.getCodingSchemeURN());
					    hset.add(urn);
				    }
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return v;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}





    public static Vector getCodingSchemeVersionsByURN(String urn) {
        try {
			Vector v = new Vector();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            if (lbSvc == null) {
                _logger
                    .warn("WARNING: Unable to connect to instantiate LexBIGService ???");
            }
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                _logger.error("lbSvc.getSupportedCodingSchemes() FAILED..."
                    + ex.getCause());
                return null;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                Boolean isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);

                if (isActive != null && isActive.equals(Boolean.TRUE)) {
                	String uri = css.getCodingSchemeURI();
                	if (uri.compareTo(urn) == 0) {
						String representsVersion = css.getRepresentsVersion();
						v.add(representsVersion);
					}
				}
			}
			return v;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


    public static Vector getCodingSchemeReferencesInValueSetDefinition(String uri) {
		System.out.println("getCodingSchemeReferencesInValueSetDefinition: " + uri);
	    if (uri.indexOf("|") != -1) {
			Vector u = DataUtils.parseData(uri);
			uri = (String) u.elementAt(1);
		}

		try {
			Vector w = new Vector();
			Vector urn_vec = getCodingSchemeURNsInValueSetDefinition(uri);
			if (urn_vec != null) {
				for (int i=0; i<urn_vec.size(); i++) {
					String urn = (String) urn_vec.elementAt(i);
					Vector v = getCodingSchemeVersionsByURN(urn);
					if (v != null) {
						for (int j=0; j<v.size(); j++) {
							String version = (String) v.elementAt(j);
							w.add(urn + "|" + version);
						}
					}
				}
				w = SortUtils.quickSort(w);
				return w;
		    } else {
				System.out.println("WARNING: DataUtils.getCodingSchemeReferencesInValueSetDefinition returns null? (URI: "
				   + uri + ").");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}



*/

    public HashMap getSubconcepts(String scheme, String version, String code) {
        return new TreeUtils().getSubconcepts(scheme, version, code);
    }


    public static JSONArray HashMap2JSONArray(HashMap hmap) {
        JSONObject json = new JSONObject();
        JSONArray nodesArray = null;
        try {
            nodesArray = new JSONArray();
            Set keyset = hmap.keySet();
            Object[] objs = keyset.toArray();
            String code = (String) objs[0];
            TreeItem ti = (TreeItem) hmap.get(code);
            for (String association : ti._assocToChildMap.keySet()) {
                List<TreeItem> children = ti._assocToChildMap.get(association);
                // Collections.sort(children);
                for (TreeItem childItem : children) {
                    // printTree(childItem, focusCode, depth + 1);
                    JSONObject nodeObject = new JSONObject();
                    nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
                    nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
                    int knt = 0;
                    if (childItem._expandable) {
                        knt = 1;
                    }
                    nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
                    nodesArray.put(nodeObject);
                }
            }
        } catch (Exception e) {

        }
        return nodesArray;
    }


    public static void printTree(HashMap hmap) {
        if (hmap == null) {
            System.out.println("ERROR printTree -- hmap is null.");
            return;
        }
        Object[] objs = hmap.keySet().toArray();
        String code = (String) objs[0];
        TreeItem ti = (TreeItem) hmap.get(code);
        printTree(ti, code, 0);
    }

    public static void printTree(TreeItem ti, String focusCode, int depth) {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < depth * 2; i++)
            indent.append("| ");

        StringBuffer codeAndText =
            new StringBuffer(indent).append(
                focusCode.equals(ti._code) ? ">>>>" : "").append(ti._code)
                .append(':').append(
                    ti._text.length() > 64 ? ti._text.substring(0, 62) + "..."
                        : ti._text).append(ti._expandable ? " [+]" : "");
        System.out.println(codeAndText.toString());

        indent.append("| ");
        for (String association : ti._assocToChildMap.keySet()) {
            System.out.println(indent.toString() + association);
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children) {
                printTree(childItem, focusCode, depth + 1);
			}
        }
    }


    public List getChildrenNodes(TreeItem ti) {
        List list = new ArrayList();
        getChildrenNodes(ti, list, 0, 1);
        return list;
    }

    public void getChildrenNodes(TreeItem ti, List list, int currLevel, int maxLevel) {
        if (list == null)
            list = new ArrayList();
        if (currLevel > maxLevel)
            return;
        if (ti._assocToChildMap.keySet().size() > 0) {
            if (ti._text.compareTo("Root node") != 0) {
                ResolvedConceptReference rcr = new ResolvedConceptReference();
                rcr.setConceptCode(ti._code);
                EntityDescription entityDescription = new EntityDescription();
                entityDescription.setContent(ti._text);
                rcr.setEntityDescription(entityDescription);
                // _logger.debug("Root: " + ti.text);
                list.add(rcr);
            }
        }

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children) {
                getChildrenNodes(childItem, list, currLevel + 1, maxLevel);
            }
        }
    }

//=========================================================================================

    public static HashMap getAssociatedConcepts(String scheme, String version,
        String code, String assocName, boolean direction) {
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            return getAssociatedConcepts(lbSvc, lbscm, scheme, version, code,
                assocName, direction);
        } catch (Exception ex) {
            return null;
        }
    }


    protected static Association processForAnonomousNodes(Association assoc) {
        if (assoc == null)
            return null;
        // clone Association except associatedConcepts
        Association temp = new Association();
        temp.setAssociatedData(assoc.getAssociatedData());
        temp.setAssociationName(assoc.getAssociationName());
        temp.setAssociationReference(assoc.getAssociationReference());
        temp.setDirectionalName(assoc.getDirectionalName());
        temp.setAssociatedConcepts(new AssociatedConceptList());

        for (int i = 0; i < assoc.getAssociatedConcepts()
            .getAssociatedConceptCount(); i++) {
            // Conditionals to deal with anonymous nodes and UMLS top nodes
            // "V-X"
            // The first three allow UMLS traversal to top node.
            // The last two are specific to owl anonymous nodes which can act
            // like false
            // top nodes.

            /*
             * if(assoc.getAssociatedConcepts().getAssociatedConcept(i).
             * getReferencedEntry() != null) {
             * _logger.debug(assoc.getAssociatedConcepts
             * ().getAssociatedConcept(i)
             * .getReferencedEntry().getEntityDescription().getContent() +
             * " === IsAnonymous? " +
             * assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry().getIsAnonymous()); } else {_logger.debug(
             * "assoc.getAssociatedConcepts().getAssociatedConcept(i).getReferencedEntry() == null"
             * ); }
             */
            /*
             * if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry() != null &&
             * assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry().getIsAnonymous() != false) { // do nothing
             * (NCI Thesaurus) }
             */
            if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
                .getReferencedEntry() != null
                && assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getReferencedEntry().getIsAnonymous() != null
                && assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getReferencedEntry().getIsAnonymous() != false
                && !assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getConceptCode().equals("@")
                && !assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getConceptCode().equals("@@")) {
                // do nothing
            } else {
                temp.getAssociatedConcepts().addAssociatedConcept(
                    assoc.getAssociatedConcepts().getAssociatedConcept(i));
            }
        }
        return temp;
    }


    /**
     * Returns the entity description for the given resolved concept reference.
     */
    protected static String getCodeDescription(ResolvedConceptReference ref)
            throws LBException {
        EntityDescription desc = ref.getEntityDescription();
        if (desc != null)
            return desc.getContent();
        return "<Not assigned>";
    }

    protected static String getCodeDescription(LexBIGService lbsvc,
        String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {

        CodedNodeSet cns = lbsvc.getCodingSchemeConcepts(scheme, csvt);
        cns =
            cns.restrictToCodes(Constructors.createConceptReferenceList(code,
                scheme));
        ResolvedConceptReferenceList rcrl = null;
        try {
            rcrl = cns.resolveToList(null, _noopList, null, 1);
        } catch (Exception ex) {
            _logger
                .error("WARNING: TreeUtils getCodeDescription cns.resolveToList throws exceptions");
            return "null";
        }

        if (rcrl != null && rcrl.getResolvedConceptReferenceCount() > 0) {
            EntityDescription desc =
                rcrl.getResolvedConceptReference(0).getEntityDescription();
            if (desc != null)
                return desc.getContent();
        }
        return "<Not assigned>";
    }


    public static HashMap getAssociatedConcepts(LexBIGService lbSvc,
        LexBIGServiceConvenienceMethods lbscm, String scheme, String version,
        String code, String assocName, boolean direction) {
        HashMap hmap = new HashMap();
        TreeItem ti = null;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = Collections.EMPTY_SET;

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
			Entity concept = getConceptByCode(scheme, version, null, code);
			String entityCodeNamespace = concept.getEntityCodeNamespace();
			//System.out.println("getEntityCodeNamespace returns: " + concept.getEntityCodeNamespace());
			ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
			focus.setCodingSchemeName(entityCodeNamespace);
            String name = concept.getEntityDescription().getContent();//getCodeDescription(lbSvc, scheme, csvt, code);

            //String name = getCodeDescription(lbSvc, scheme, csvt, code);
            ti = new TreeItem(code, name);
            ti._expandable = false;

            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            //ConceptReference focus =
            //    Constructors.createConceptReference(code, scheme);
            cng =
                cng.restrictToAssociations(Constructors
                    .createNameAndValueList(assocName), null);
            boolean associationsNavigatedFwd = direction;

            // To remove anonymous classes (KLO, 091009), the
            // resolveCodedEntryDepth parameter cannot be set to -1.
            // Alternative -- use code to determine whether the class is
            // anonymous

            ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        // !associationsNavigatedFwd, 1, 2, noopList_, null,
                        // null, null, -1, false);
                        !associationsNavigatedFwd, -1, 2, _noopList, null,
                        null, null, -1, false);

            } catch (Exception e) {
                _logger
                    .error("TreeUtils getAssociatedConcepts throws exceptions.");
                return null;
            }

            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;

                // AssociationList childAssociationList =
                // associationsNavigatedFwd ? node.getSourceOf():
                // node.getTargetOf();

                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();

                } else {
                    childAssociationList = node.getTargetOf();
                }

                if (childAssociationList != null) {
                    // Process each association defining children ...
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();
                        // KLO 091009 remove anonymous nodes

                        child = processForAnonomousNodes(child);

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, csvt, child,
                                associationsNavigatedFwd);

                        // Each association may have multiple children ...
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();

                        /*
                         * for (Iterator<AssociatedConcept> branchNodes =
                         * branchItemList.iterateAssociatedConcept();
                         * branchNodes .hasNext();) { AssociatedConcept
                         * branchItemNode = branchNodes.next();
                         */

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();
                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();

                            if (!branchItemCode.startsWith("@")) {
                                // Add here if not in the list of excluded
                                // codes.
                                // This is also where we look to see if another
                                // level
                                // was indicated to be available. If so, mark
                                // the
                                // entry with a '+' to indicate it can be
                                // expanded.
                                if (!codesToExclude.contains(branchItemCode)) {
                                    TreeItem childItem =
                                        new TreeItem(branchItemCode,
                                            getCodeDescription(branchItemNode));

                                    ti._expandable = true;
                                    AssociationList grandchildBranch =
                                        associationsNavigatedFwd ? branchItemNode
                                            .getSourceOf()
                                            : branchItemNode.getTargetOf();
                                    if (grandchildBranch != null)
                                        childItem._expandable = true;

                                    ti.addChild(childNavText, childItem);
                                }
                            }
                        }
                    }
                } else {
                    _logger.warn("WARNING: childAssociationList == null.");
                }
            }
            hmap.put(code, ti);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        return hmap;
    }

    public static HashMap getAssociatedConcepts(String scheme, String version,
        String code, String[] assocNames, boolean direction) {
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            return getAssociatedConcepts(lbSvc, lbscm, scheme, version, code,
                assocNames, direction);
        } catch (Exception ex) {
            return null;
        }
    }

    public static HashMap getAssociatedConcepts(LexBIGService lbSvc,
        LexBIGServiceConvenienceMethods lbscm, String scheme, String version,
        String code, String[] assocNames, boolean direction) {
        HashMap hmap = new HashMap();
        TreeItem ti = null;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = Collections.EMPTY_SET;

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {

			Entity concept = getConceptByCode(scheme, version, null, code);
			String entityCodeNamespace = concept.getEntityCodeNamespace();
			//System.out.println("getEntityCodeNamespace returns: " + concept.getEntityCodeNamespace());
			ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
			focus.setCodingSchemeName(entityCodeNamespace);
            String name = concept.getEntityDescription().getContent();//getCodeDescription(lbSvc, scheme, csvt, code);

            //String name = getCodeDescription(lbSvc, scheme, csvt, code);
            ti = new TreeItem(code, name);
            ti._expandable = false;

            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            //ConceptReference focus =
            //    Constructors.createConceptReference(code, scheme);
            cng =
                cng.restrictToAssociations(Constructors
                    .createNameAndValueList(assocNames), null);
            boolean associationsNavigatedFwd = direction;

            // To remove anonymous classes (KLO, 091009), the
            // resolveCodedEntryDepth parameter cannot be set to -1.
            // Alternative -- use code to determine whether the class is
            // anonymous

            ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        // !associationsNavigatedFwd, 1, 2, noopList_, null,
                        // null, null, -1, false);
                        !associationsNavigatedFwd, -1, 2, _noopList, null,
                        null, null, -1, false);

            } catch (Exception e) {
                _logger
                    .error("TreeUtils getAssociatedConcepts throws exceptions.");
                return null;
            }

            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;

                // AssociationList childAssociationList =
                // associationsNavigatedFwd ? node.getSourceOf():
                // node.getTargetOf();

                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();

                } else {
                    childAssociationList = node.getTargetOf();
                }

                if (childAssociationList != null) {
                    // Process each association defining children ...
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();
                        // KLO 091009 remove anonymous nodes

                        child = processForAnonomousNodes(child);

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, csvt, child,
                                associationsNavigatedFwd);

                        // Each association may have multiple children ...
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();

                        /*
                         * for (Iterator<AssociatedConcept> branchNodes =
                         * branchItemList.iterateAssociatedConcept();
                         * branchNodes .hasNext();) { AssociatedConcept
                         * branchItemNode = branchNodes.next();
                         */

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();
                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();

                            if (!branchItemCode.startsWith("@")) {
                                // Add here if not in the list of excluded
                                // codes.
                                // This is also where we look to see if another
                                // level
                                // was indicated to be available. If so, mark
                                // the
                                // entry with a '+' to indicate it can be
                                // expanded.
                                if (!codesToExclude.contains(branchItemCode)) {
                                    TreeItem childItem =
                                        new TreeItem(branchItemCode,
                                            getCodeDescription(branchItemNode));

                                    ti._expandable = true;
                                    AssociationList grandchildBranch =
                                        associationsNavigatedFwd ? branchItemNode
                                            .getSourceOf()
                                            : branchItemNode.getTargetOf();
                                    if (grandchildBranch != null)
                                        childItem._expandable = true;

                                    ti.addChild(childNavText, childItem);
                                }
                            }
                        }
                    }
                } else {
                    _logger.warn("WARNING: childAssociationList == null.");
                }
            }
            hmap.put(code, ti);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        return hmap;
    }


    public static ConceptReferenceList createConceptReferenceList(
        String[] codes, String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.length; i++) {
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(codes[i]);
            list.addConceptReference(cr);
        }
        return list;
    }

    public static boolean isBlank(String str) {
        if ((str == null) || str.matches("^\\s*$")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the label to display for the given association and directional
     * indicator.
     */
    protected static String getDirectionalLabel(
        LexBIGServiceConvenienceMethods lbscm, String scheme,
        CodingSchemeVersionOrTag csvt, Association assoc, boolean navigatedFwd)
            throws LBException {

        String assocLabel =
            navigatedFwd ? lbscm.getAssociationForwardName(assoc
                .getAssociationName(), scheme, csvt) : lbscm
                .getAssociationReverseName(assoc.getAssociationName(), scheme,
                    csvt);
        // if (StringUtils.isBlank(assocLabel))
        if (isBlank(assocLabel))
            assocLabel =
                (navigatedFwd ? "" : "[Inverse]") + assoc.getAssociationName();
        return assocLabel;
    }


    public static Entity getConceptByCode(String codingSchemeName,
        String vers, String ltag, String code) {
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            if (lbSvc == null) {
                _logger.warn("lbSvc == null???");
                return null;
            }

            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(vers);

            ConceptReferenceList crefs =
                createConceptReferenceList(new String[] { code },
                    codingSchemeName);

            CodedNodeSet cns = null;

            try {
                cns =
                    lbSvc.getCodingSchemeConcepts(codingSchemeName,
                        versionOrTag);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            cns = cns.restrictToCodes(crefs);
            ResolvedConceptReferenceList matches =
                cns.resolveToList(null, null, null, 1);

            if (matches == null) {
                _logger.warn("Concept not found.");
                return null;
            }

            // Analyze the result ...
            if (matches.getResolvedConceptReferenceCount() > 0) {
                ResolvedConceptReference ref =
                    (ResolvedConceptReference) matches
                        .enumerateResolvedConceptReference().nextElement();

                Entity entry = ref.getReferencedEntry();
                return entry;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static NameAndValueList createNameAndValueList(String[] names,
        String[] values) {
        NameAndValueList nvList = new NameAndValueList();
        for (int i = 0; i < names.length; i++) {
            NameAndValue nv = new NameAndValue();
            nv.setName(names[i]);
            if (values != null) {
                nv.setContent(values[i]);
            }
            nvList.addNameAndValue(nv);
        }
        return nvList;
    }

/*
    public void dumpTree(HashMap hmap, String focusCode, int level) {
        try {
            Set keyset = hmap.keySet();
            Object[] objs = keyset.toArray();
            String code = (String) objs[0];
            TreeItem ti = (TreeItem) hmap.get(code);
            for (String association : ti._assocToChildMap.keySet()) {
                System.out.println("\nassociation: " + association);
                List<TreeItem> children = ti._assocToChildMap.get(association);
                for (TreeItem childItem : children) {
                    System.out.println(childItem._text + "(" + childItem._code + ")");
                    int knt = 0;
                    if (childItem._expandable) {
                        knt = 1;
                        System.out.println("\tnode.expandable");

                        printTree(childItem, focusCode, level);

                        List list = getChildrenNodes(childItem);
                        for (int i = 0; i < list.size(); i++) {
                            Object obj = list.get(i);
                            String nd_code = "";
                            String nd_name = "";
                            if (obj instanceof ResolvedConceptReference) {
                                ResolvedConceptReference node =
                                    (ResolvedConceptReference) list.get(i);
                                nd_code = node.getConceptCode();
                                nd_name =
                                    node.getEntityDescription().getContent();
                            } else if (obj instanceof Entity) {
                                Entity node = (Entity) list.get(i);
                                nd_code = node.getEntityCode();
                                nd_name =
                                    node.getEntityDescription().getContent();
                            }
                            System.out.println("TOP NODE: " + nd_name + " ("
                                + nd_code + ")");
                        }

                    } else {
                        System.out.println("\tnode.NOT expandable");
                    }
                }
            }
        } catch (Exception e) {

        }
    }
*/


    //public static void


    public List getSuperconceptList(String scheme, String version, String code) {
        ArrayList superconceptList = new ArrayList();
		HashMap hmap_super = TreeUtils.getSuperconcepts(scheme, version, code);
		if (hmap_super != null) {
			TreeItem ti = (TreeItem) hmap_super.get(code);
			if (ti != null) {
				for (String association : ti._assocToChildMap.keySet()) {
					List<TreeItem> children =
						ti._assocToChildMap.get(association);
					for (TreeItem childItem : children) {
						superconceptList.add(childItem._text + "|"
							+ childItem._code);
					}
				}
			}
		}
		Collections.sort(superconceptList);
		return superconceptList;
	}

    public static List getSubconceptList(String scheme, String version, String code) {
		return TreeUtils.getSubconceptNamesAndCodes(scheme, version, code);
	}

	public static void populateChildrenNodes(String scheme, String version, TreeItem ti, HashSet visited_nodes) {
        if (visited_nodes.contains(ti._code)) {
			//System.out.println("Loop detected -- code: " + ti._code);
			return;
		}

		visited_nodes.add(ti._code);
		List subconcepts = getSubconceptList(scheme, version, ti._code);
		ti._expandable = false;
		if (subconcepts.size() == 0) {
			return;
		}
		ti._expandable = true;
		List<TreeItem> children = new ArrayList();
		for (int lcv=0; lcv<subconcepts.size(); lcv++) {
			String nv = (String) subconcepts.get(lcv);
			System.out.println(nv);
			Vector u = parseData(nv);
			String name = (String) u.elementAt(0);
			String code = (String) u.elementAt(1);

			TreeItem children_node = new TreeItem(code, name);
			populateChildrenNodes(scheme, version, children_node, visited_nodes);
			children.add(children_node);
		}
		ti.addAll("[inverse_is_a]", children);
	}


    public static void preprocessSourceHierarchyData() {
		_source_hierarchy = getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);

		_source_subconcept_map = new HashMap();
		populateSubconceptHashMap(_source_hierarchy, _source_subconcept_map);//, root);

		_source_superconcept_map = new HashMap();
		populateSuperconceptHashMap(_source_hierarchy, _source_superconcept_map);//, root);

	}

	public static HashMap getValueSetSourceHierarchy() {
		return getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);
	}



	public static HashMap getValueSetSourceHierarchy(String scheme, String version) {
		if (_source_hierarchy != null) return _source_hierarchy;

		_source_hierarchy = new HashMap();
		// value set source coding scheme
		ResolvedConceptReferenceList roots = getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);

		HashSet visited_nodes = new HashSet();

		TreeItem ti = new TreeItem("<Root>", "Root node");
		ti._expandable = false;
		if (roots != null) {
			for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
				System.out.println("\tRoot: " + rcr.getEntityDescription().getContent() + " (" + rcr.getConceptCode() + ")");

				TreeItem root_node = new TreeItem(rcr.getConceptCode(), rcr.getEntityDescription().getContent());

				populateChildrenNodes(SOURCE_SCHEME, SOURCE_VERSION, root_node, visited_nodes);
				ti.addChild("inverse_is_a", root_node);
				ti._expandable = true;
			}
		}
		_source_hierarchy.put("<Root>", ti);

		_source_subconcept_map = new HashMap();
		populateSubconceptHashMap(_source_hierarchy, _source_subconcept_map);//, root);

		_source_superconcept_map = new HashMap();
		populateSuperconceptHashMap(_source_hierarchy, _source_superconcept_map);//, root);


		return _source_hierarchy;
	}

    public static void populateSubconceptHashMap(HashMap hmap, TreeItem ti) {

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);

            for (TreeItem childItem : children) {

				if(hmap.containsKey(ti._code)) {
					Vector v = (Vector) hmap.get(ti._code);
					if (!v.contains(childItem._code)) {
						v.add(childItem._code);
						hmap.put(ti._code, v);
					}
				} else {
					Vector v = new Vector();
					v.add(childItem._code);
					hmap.put(ti._code, v);
				}

				populateSubconceptHashMap(hmap, childItem);
		    }
		}
	}

    public static void populateSuperconceptHashMap(HashMap hmap, TreeItem ti) {

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            String superconcept_key = ti._code;
            for (TreeItem childItem : children) {
                String subconcept_key = childItem._code;
				if(hmap.containsKey(subconcept_key)) {
					Vector v = (Vector) hmap.get(subconcept_key);
					if (!v.contains(superconcept_key)) {
						v.add(superconcept_key);
						hmap.put(subconcept_key, v);
					}
				} else {
					Vector v = new Vector();
					v.add(superconcept_key);
					hmap.put(subconcept_key, v);
				}

				populateSuperconceptHashMap(hmap, childItem);
		    }
		}
	}


    public static void populateSuperconceptHashMap(HashMap source_hierarchy, HashMap hmap) {
		TreeItem root = (TreeItem) source_hierarchy.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
            List<TreeItem> children = root._assocToChildMap.get(association);
            for (TreeItem childItem : children) {
            	populateSuperconceptHashMap(hmap, childItem);
			}
		}
	}

    public static void populateSubconceptHashMap(HashMap source_hierarchy, HashMap hmap) {
		TreeItem root = (TreeItem) source_hierarchy.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
            List<TreeItem> children = root._assocToChildMap.get(association);
            for (TreeItem childItem : children) {
            	populateSubconceptHashMap(hmap, childItem);
			}
		}
	}




    public static Vector getCodeList(String scheme, String version) {
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

            if (lbSvc == null) {
                return null;
            }

            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(version);

            CodedNodeSet cns = null;

            try {
                cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
                //LocalNameList propertyNames = null;
                SortOptionList sortOptions = null;
                LocalNameList filterOptions = null;
                boolean resolveObjects = false; // needs to be set to true
                int maxToReturn = -1;

                ResolvedConceptReferenceList rcrl =
                    cns.resolveToList(sortOptions, filterOptions,
                        null, null, resolveObjects,
                        maxToReturn);
                Vector v = new Vector();
                if (rcrl.getResolvedConceptReferenceCount() > 0) {
                    for (int i = 0; i < rcrl.getResolvedConceptReferenceCount(); i++) {
                        ResolvedConceptReference rcr =
                            rcrl.getResolvedConceptReference(i);
                        v.add(rcr.getConceptCode() + "|" + rcr.getEntityDescription().getContent());
					}
				}
				return v;
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}



    public static Vector getCodingSchemeURNsInValueSetDefinition(String uri) {
		try {
			java.net.URI valueSetDefinitionURI = new URI(uri);
			Vector v = new Vector();
			try {
				LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
				AbsoluteCodingSchemeVersionReferenceList codingSchemes =
					vsd_service.getCodingSchemesInValueSetDefinition(valueSetDefinitionURI);

                if (codingSchemes != null) {
					//output is all of the mapping ontologies that this code participates in.
					for(AbsoluteCodingSchemeVersionReference ref : codingSchemes.getAbsoluteCodingSchemeVersionReference()){
						v.add(ref.getCodingSchemeURN());
					}
					return SortUtils.quickSort(v);
			    } else {
					System.out.println("WARNING: DataUtils.getCodingSchemeURNsInValueSetDefinition returns null (URI: "
					   + uri + ").");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("WARNING: DataUtils.getCodingSchemeURNsInValueSetDefinition throws exceptions.");
			}
			return v;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("WARNING: DataUtils.getCodingSchemeURNsInValueSetDefinition throws exceptions.");
		}
		return null;
	}

	public static HashMap getRootValueSets(String scheme) {
		String formalName = DataUtils.getFormalName(scheme);
		String codingSchemeURN = (String) DataUtils._codingSchemeName2URIHashMap.get(formalName);

		System.out.println("ValueSetHierarchy getRootValueSets codingSchemeURN: " + codingSchemeURN);

		HashMap source_hier = getValueSetSourceHierarchy();
        Vector source_in_cs_vsd_vec = new Vector();
        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();

		//Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			//
            Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
            if (cs_vec.contains(codingSchemeURN)) {
				ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);

				uri2VSD_map.put(uri, vsd);

				java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

				while (sourceEnum.hasMoreElements()) {
					Source src = (Source) sourceEnum.nextElement();
					String src_str = src.getContent();
					if (!source_in_cs_vsd_vec.contains(src_str)) {
						source_in_cs_vsd_vec.add(src_str);
					}
					if (source2VSD_map.containsKey(src_str)) {
						Vector vsd_v = (Vector) source2VSD_map.get(src_str);
						if (!vsd_v.contains(uri)) {
							vsd_v.add(uri);
						}
						source2VSD_map.put(src_str, vsd_v);
					} else {
						Vector vsd_v = new Vector();
						vsd_v.add(uri);
						source2VSD_map.put(src_str, vsd_v);
					}
				}

				//v.add(uri);
			}
		}

		TreeItem root = new TreeItem("<Root>", "Root node");

        List <TreeItem> children = new ArrayList();
        Vector root_source_vec = new Vector();
		for (int k=0; k<source_in_cs_vsd_vec.size(); k++) {
			String src = (String) source_in_cs_vsd_vec.elementAt(k);
			System.out.println("\nChecking " + src);

			// check has children
			boolean has_children = false;
			if (_source_subconcept_map.containsKey(src)) {
				Vector sub_vec = (Vector) _source_subconcept_map.get(src);
				for (int j=0; j<source_in_cs_vsd_vec.size(); j++) {
					String source = (String) source_in_cs_vsd_vec.elementAt(j);
					if (source.compareTo(src) != 0 && sub_vec.contains(source)) {
						has_children = true;
						break;
					}
				}

			}


			boolean has_parent = false;
			if (_source_superconcept_map.containsKey(src)) {
				Vector super_vec = (Vector) _source_superconcept_map.get(src);
				for (int j=0; j<source_in_cs_vsd_vec.size(); j++) {
					String source = (String) source_in_cs_vsd_vec.elementAt(j);
					if (source.compareTo(src) != 0 && super_vec.contains(source)) {
						has_parent = true;
						break;
					}
				}
			}

			// orphan
			if (!has_children && !has_parent) {
				// find all VSDs has the source
				Vector vsd_v = (Vector) source2VSD_map.get(src);
				for(int i=0; i<vsd_v.size(); i++) {
					//ValueSetDefinition vsd = (ValueSetDefinition) vsd_v.elementAt(i);
					String vsd_uri = (String) vsd_v.elementAt(i);
					ValueSetDefinition vsd = (ValueSetDefinition) uri2VSD_map.get(vsd_uri);

					String name = vsd.getValueSetDefinitionName();
					if (name == null || name.compareTo("") == 0) {
						name = "<NOT ASSIGNED>";
					}

					TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), name);
					ti._expandable = false;
					children.add(ti);
				}

			} else if (has_children && !has_parent) {
				Vector vsd_v = (Vector) source2VSD_map.get(src);
				for(int i=0; i<vsd_v.size(); i++) {
					//ValueSetDefinition vsd = (ValueSetDefinition) vsd_v.elementAt(i);
					String vsd_uri = (String) vsd_v.elementAt(i);
					ValueSetDefinition vsd = (ValueSetDefinition) uri2VSD_map.get(vsd_uri);

					String name = vsd.getValueSetDefinitionName();
					if (name == null || name.compareTo("") == 0) {
						name = "<NOT ASSIGNED>";
					}

					TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), name);
					ti._expandable = true;
					children.add(ti);
				}
			}

		}

		root._expandable = false;
		root.addAll("[inverse_is_a]", children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}



	public static void main(String[] args) throws Exception {
		ValueSetHierarchy test = new ValueSetHierarchy();
/*

        Vector source_vec = getAvailableValueSetDefinitionSources();
        if (source_vec != null) {
			System.out.println("Available sources: " + source_vec.size());
			for (int i=0; i<source_vec.size(); i++) {
				String src = (String) source_vec.elementAt(i);
				int j = i+1;
				System.out.println("(" + j + ") " + src);

				Vector vsd_vec = getValueSetDefinitionsBySource(src);
				for (int k=0; k<vsd_vec.size(); k++) {
					ValueSetDefinition vsd = (ValueSetDefinition) vsd_vec.elementAt(k);
					String metadata = getValueSetDefinitionMetadata(vsd);
					System.out.println("\t" + metadata);
				}
				System.out.println("\n");
			}
	    } else {
			System.out.println("Available sources not found.$");
		}

		String scheme = "ChEBI";
		//scheme = "automobiles";
		String version = null;

		System.out.println(scheme + "Roots: " );
		ResolvedConceptReferenceList roots = getHierarchyRoots(scheme, version);
		if (roots != null) {
			for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
				System.out.println("Root: " + rcr.getEntityDescription().getContent() + " (" + rcr.getConceptCode() + ")");
				List superconcepts = test.getSubconceptList(scheme, version, rcr.getConceptCode());
				for (int lcv=0; lcv<superconcepts.size(); lcv++) {
					String nv = (String) superconcepts.get(lcv);
					System.out.println(nv);
				}

				HashMap hmap = test.getSubconcepts(scheme, version, rcr.getConceptCode());


System.out.println("\nTreeUtils().printTree...\n");
                printTree(hmap);


System.out.println("\nexit TreeUtils().printTree...\n");



				JSONArray json = test.HashMap2JSONArray(hmap);

				System.out.println(json.toString());


			}

	    } else {
			System.out.println("Unable to resolve roots for " + scheme);
		}


System.out.println("=============================================");


		Vector v1 = new Vector();

		v1.add("s1");
		v1.add("s2");

		Vector v2 = null;


		//Collections.copy(v1,v2);

		v2 = (Vector) v1.clone();



		//v2 = v1;
		for (int i=0; i<v1.size(); i++) {
			String s = (String) v1.elementAt(i);
			System.out.println(s);
		}

        System.out.println("\nV1:");
		for (int i=0; i<v1.size(); i++) {
			String s = (String) v1.elementAt(i);
			System.out.println(s);
		}

        System.out.println("\nV2:");
		for (int i=0; i<v2.size(); i++) {
			String s = (String) v2.elementAt(i);
			System.out.println(s);
		}

		v2.remove("s1");

System.out.println("after removing s1 from V2:");

        System.out.println("\nV1:");
		for (int i=0; i<v1.size(); i++) {
			String s = (String) v1.elementAt(i);
			System.out.println(s);
		}

        System.out.println("\nV2:");
		for (int i=0; i<v2.size(); i++) {
			String s = (String) v2.elementAt(i);
			System.out.println(s);
		}

*/
        //HashMap root_hierarchy = test.getValueSetSourceHierarchy("Terminology Value Set", null);

/*
String scheme = "automobiles";
String version = "1.0";



scheme = "Terminology Value Set";
version = "03.24.2011";


        HashMap source_hierarchy = test.getValueSetSourceHierarchy(scheme, version);
        //HashMap root_hierarchy = test.getValueSetSourceHierarchy("conceptDemainCodingScheme", "1.0");

		//HashMap source_hierarchy = test.getValueSetSourceHierarchy("Terminology Value Set", "03.24.2011");

		printTree(source_hierarchy);



		//TreeItem root = (TreeItem) source_hierarchy.get("<Root>");

        System.out.println("Calling populateSubconceptHashMap ...");
        HashMap sub_hashmap = new HashMap();
		populateSubconceptHashMap(source_hierarchy, sub_hashmap);//, root);
		Iterator it = sub_hashmap.keySet().iterator();
		while (it.hasNext()) {
			String t = (String) it.next();
			System.out.println(t);
			Vector v = (Vector) sub_hashmap.get(t);
			for (int i=0; i<v.size(); i++) {
				String s = (String) v.elementAt(i);
				System.out.println("\tsub: " + s);
			}
			System.out.println("\n");
		}


System.out.println("=============================================");

        System.out.println("Calling populateSuperconceptHashMap ...");
        HashMap super_hashmap = new HashMap();
		populateSuperconceptHashMap(source_hierarchy, super_hashmap);//, root);

		it = super_hashmap.keySet().iterator();
		while (it.hasNext()) {
			String t = (String) it.next();
			System.out.println(t);
			Vector v = (Vector) super_hashmap.get(t);
			for (int i=0; i<v.size(); i++) {
				String s = (String) v.elementAt(i);
				System.out.println("\tsuper: " + s);
			}
			System.out.println("\n");
		}

System.out.println("=============================================");
System.out.println("Calling getCodeList ...");

		Vector concept_vec = test.getCodeList(scheme, version);

		for (int i=0; i<concept_vec.size(); i++) {
			int j=i+1;
			String s = (String) concept_vec.elementAt(i);
			System.out.println("(" + j + ") " + s);
		}


System.out.println("=============================================");
System.out.println("Find orphans and regular roots: ");

        Vector orphan_vec = new Vector();
        Vector regular_root_vec = new Vector();

		for (int i=0; i<concept_vec.size(); i++) {
			int j=i+1;
			String s = (String) concept_vec.elementAt(i);
			System.out.println("(" + j + ") " + s);
			if (!super_hashmap.containsKey(s) && !sub_hashmap.containsKey(s)) {
				System.out.println("\tOrphan");
				orphan_vec.add(s);
			} else if (!super_hashmap.containsKey(s)) {
				System.out.println("\tRegular root concept");
				regular_root_vec.add(s);
			}
		}


System.out.println("=============================================");
System.out.println("Orphans: ");

		for (int i=0; i<orphan_vec.size(); i++) {
			int j=i+1;
			String s = (String) orphan_vec.elementAt(i);
			System.out.println("(" + j + ") " + s);
		}

System.out.println("=============================================");
System.out.println("Regular roots: ");

		for (int i=0; i<regular_root_vec.size(); i++) {
			int j=i+1;
			String s = (String) regular_root_vec.elementAt(i);
			System.out.println("(" + j + ") " + s);
		}

*/

String cs_url = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";

	    HashMap root_hmap = ValueSetHierarchy.getRootValueSets(cs_url);
	    if (root_hmap != null) {
			printTree(root_hmap);
		}

	}




}


/*

Available sources: 7
(1) FDA_Device
        FDA Patient Problem Codes Terminology|http://ncit:C54450|<NO DESCRIPTION
>|<NOT ASSIGNED>|FDA_Device
        FDA Medical Device Problem Codes Terminology|http://ncit:C54451|<NO DESC
RIPTION>|<NOT ASSIGNED>|FDA_Device
        FDA Medical Device Component Or Accessory Terminology|http://ncit:C54577
|<NO DESCRIPTION>|<NOT ASSIGNED>|FDA_Device
        FDA Medical Device Evaluation Methods Terminology|http://ncit:C91800|<NO
 DESCRIPTION>|<NOT ASSIGNED>|FDA_Device
        FDA Medical Device Evaluation Results Terminology|http://ncit:C91801|<NO
 DESCRIPTION>|<NOT ASSIGNED>|FDA_Device
        FDA Medical Device Evaluation Conclusions Terminology|http://ncit:C91802
|<NO DESCRIPTION>|<NOT ASSIGNED>|FDA_Device


(2) FDA
        FDA SPL Terminology|http://ncit:C54452|<NO DESCRIPTION>|<NOT ASSIGNED>|F
DA


(3) FDA_SPL
        FDA SPL Color Terminology|http://ncit:C54453|<NO DESCRIPTION>|<NOT ASSIG
NED>|FDA_SPL
        FDA SPL Shape Terminology|http://ncit:C54454|<NO DESCRIPTION>|<NOT ASSIG
NED>|FDA_SPL
        FDA SPL Drug Route of Administration Terminology|http://ncit:C54455|<NO
DESCRIPTION>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Pharmaceutical Dosage Form Terminology|http://ncit:C54456|<NO DE
SCRIPTION>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Potency Terminology|http://ncit:C54458|<NO DESCRIPTION>|<NOT ASS
IGNED>|FDA_SPL
        FDA SPL DEA Schedule Terminology|http://ncit:C54459|<NO DESCRIPTION>|<NO
T ASSIGNED>|FDA_SPL
        FDA SPL Limitation of Use Terminology|http://ncit:C54599|<NO DESCRIPTION
>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Pharmacokinetic Effect Consequences Terminology|http://ncit:C546
00|<NO DESCRIPTION>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Type of Drug Interaction Terminology|http://ncit:C54618|<NO DESC
RIPTION>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Medical Product Intent of Use Terminology|http://ncit:C54619|<NO
 DESCRIPTION>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Flavor Terminology|http://ncit:C73339|<NO DESCRIPTION>|<NOT ASSI
GNED>|FDA_SPL
        FDA SPL Marketing Category Terminology|http://ncit:C73582|<NO DESCRIPTIO
N>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Business Operation Terminology|http://ncit:C73600|<NO DESCRIPTIO
N>|<NOT ASSIGNED>|FDA_SPL
        FDA SPL Document Type Terminology|http://ncit:C87299|<NO DESCRIPTION>|<N
OT ASSIGNED>|FDA_SPL
        FDA SPL Unit of Measure Terminology|http://ncit:C92951|<NO DESCRIPTION>|
<NOT ASSIGNED>|FDA_SPL


(4) CDISC
        CDISC Terminology|http://ncit:C61410|<NO DESCRIPTION>|<NOT ASSIGNED>|CDI
SC
        CDISC SDTM Terminology|http://ncit:C66830|<NO DESCRIPTION>|<NOT ASSIGNED
>|CDISC
        CDISC Glossary Terminology|http://ncit:C67497|<NO DESCRIPTION>|<NOT ASSI
GNED>|CDISC
        CDISC SEND Terminology|http://ncit:C77526|<NO DESCRIPTION>|<NOT ASSIGNED
>|CDISC
        CDISC CDASH Terminology|http://ncit:C77527|<NO DESCRIPTION>|<NOT ASSIGNE
D>|CDISC
        CDISC ADaM Terminology|http://ncit:C81222|<NO DESCRIPTION>|<NOT ASSIGNED
>|CDISC
        CDISC Observation Class Terminology|http://ncit:C82590|<NO DESCRIPTION>|
<NOT ASSIGNED>|CDISC
        CDISC Variable Terminology|http://ncit:C83187|<NO DESCRIPTION>|<NOT ASSI
GNED>|CDISC


(5) DICOM
        DICOM Terminology|http://ncit:C69186|<NO DESCRIPTION>|<NOT ASSIGNED>|DIC
OM


(6) NCI
        NCIt Neoplasm Tree|http://ncit:Neoplasm|<NO DESCRIPTION>|<NOT ASSIGNED>|
NCI


(7) NDFRT
        NDFRT Mechanism of Action |http://ndfrt:MoA|<NO DESCRIPTION>|<NOT ASSIGN
ED>|NDFRT

*/
