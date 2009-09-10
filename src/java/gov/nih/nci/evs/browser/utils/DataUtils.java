package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import javax.faces.model.SelectItem;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.concepts.Concept;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.relations.Relations;
import org.LexGrid.versions.SystemRelease;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.naming.SupportedSource;
import org.LexGrid.naming.SupportedPropertyQualifier;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.naming.SupportedProperty;
import org.LexGrid.naming.SupportedRepresentationalForm;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedHierarchy;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;

import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;
import static gov.nih.nci.evs.browser.common.Constants.*;

import org.LexGrid.naming.SupportedNamespace;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
 * and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * "This product includes software developed by NGIT and the National Cancer Institute."
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
 * the recipient to use any trademarks owned by either NCI or NGIT
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 * NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history Initial implementation kim.ong@ngc.com
 *
 */
public class DataUtils {
    LocalNameList noopList_ = Constructors.createLocalNameList("_noop_");
    int maxReturn = 5000;
    Connection con;
    Statement stmt;
    ResultSet rs;

    private static List _ontologies = null;

    private static org.LexGrid.LexBIG.LexBIGService.LexBIGService lbSvc = null;
    public org.LexGrid.LexBIG.Utility.ConvenienceMethods lbConvMethods = null;
    public CodingSchemeRenderingList csrl = null;
    private static HashMap codingSchemeMap = null;

    private static HashMap csnv2codingSchemeNameMap = null;
    private static HashMap csnv2VersionMap = null;

    // ==================================================================================
    // For customized query use

    public static int ALL = 0;
    public static int PREFERRED_ONLY = 1;
    public static int NON_PREFERRED_ONLY = 2;

    static int RESOLVE_SOURCE = 1;
    static int RESOLVE_TARGET = -1;
    static int RESTRICT_SOURCE = -1;
    static int RESTRICT_TARGET = 1;

    public static final int SEARCH_NAME_CODE = 1;
    public static final int SEARCH_DEFINITION = 2;

    public static final int SEARCH_PROPERTY_VALUE = 3;
    public static final int SEARCH_ROLE_VALUE = 6;
    public static final int SEARCH_ASSOCIATION_VALUE = 7;

    static final List<String> STOP_WORDS = Arrays.asList(new String[] { "a",
            "an", "and", "by", "for", "of", "on", "in", "nos", "the", "to",
            "with" });

    public static String TYPE_ROLE = "type_role";
    public static String TYPE_ASSOCIATION = "type_association";
    public static String TYPE_SUPERCONCEPT = "type_superconcept";
    public static String TYPE_SUBCONCEPT = "type_subconcept";

    public String NCICBContactURL = null;
    public String terminologySubsetDownloadURL = null;
    public String term_suggestion_application_url = null;
    public String NCITBuildInfo = null;
    public String NCImURL = null;

	public static HashMap namespace2CodingScheme = null;


    // ==================================================================================

    public DataUtils() {
        // setCodingSchemeMap();
    }

    public static List getOntologyList() {
        if (_ontologies == null)
            setCodingSchemeMap();
        return _ontologies;
    }


    private static void setCodingSchemeMap()
	{
        _ontologies = new ArrayList();
        codingSchemeMap = new HashMap();
        csnv2codingSchemeNameMap = new HashMap();
        csnv2VersionMap = new HashMap();

        Vector nv_vec = new Vector();
		boolean includeInactive = true;

        try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodingSchemeRenderingList csrl = null;
            try {
				csrl = lbSvc.getSupportedCodingSchemes();
			} catch (LBInvocationException ex) {
				ex.printStackTrace();
				System.out.println("lbSvc.getSupportedCodingSchemes() FAILED..." + ex.getCause() );
                return;
			}


			CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
			for (int i=0; i<csrs.length; i++)
			{
				int j = i+1;
				CodingSchemeRendering csr = csrs[i];

				CodingSchemeSummary css = csr.getCodingSchemeSummary();
				String formalname = css.getFormalName();
				String representsVersion = css.getRepresentsVersion();
System.out.println("(" + j + ") " + formalname + "  version: " + representsVersion);
				Boolean isActive = null;
				if (csr == null) {
					System.out.println("\tcsr == null???");
				} else if (csr.getRenderingDetail() == null) {
					System.out.println("\tcsr.getRenderingDetail() == null");
				} else if (csr.getRenderingDetail().getVersionStatus() == null) {
					System.out.println("\tcsr.getRenderingDetail().getVersionStatus() == null");
				} else {

					isActive = csr.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE);
				}

System.out.println("\n\tActive? " + isActive);

				if ((includeInactive && isActive == null) || (isActive != null && isActive.equals(Boolean.TRUE))
				     || (includeInactive && (isActive != null && isActive.equals(Boolean.FALSE))))
				{
						String value = formalname + " (version: " + representsVersion + ")";
						nv_vec.add(value);
						csnv2codingSchemeNameMap.put(value, formalname);
						csnv2VersionMap.put(value, representsVersion);
				}
			}
	    } catch (Exception e) {
			//e.printStackTrace();
			//return null;
		}
        if (nv_vec.size() > 0) {
			nv_vec = SortUtils.quickSort(nv_vec);
			for (int k=0; k<nv_vec.size(); k++) {
				String value = (String) nv_vec.elementAt(k);
				_ontologies.add(new SelectItem(value, value));
			}
		}
	}

	/*
    private static void setCodingSchemeMap() {
        // if (_ontologies != null) return;
        _ontologies = new ArrayList();
        codingSchemeMap = new HashMap();
        csnv2codingSchemeNameMap = new HashMap();
        csnv2VersionMap = new HashMap();

        Vector nv_vec = new Vector();

        try {
            RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

            CodingSchemeRenderingList csrl = lbSvc.getSupportedCodingSchemes();
            if (csrl == null) {
                System.out.println("WARNING: csrl is NULL");
			}

            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            System.out.println("csrs.length: " + csrs.length);
            for (int i = 0; i < csrs.length; i++) {
                CodingSchemeRendering csr = csrs[i];
                Boolean isActive = csr.getRenderingDetail().getVersionStatus()
                        .equals(CodingSchemeVersionStatus.ACTIVE);
                //if (isActive != null && isActive.equals(Boolean.TRUE)) {
                    CodingSchemeSummary css = csr.getCodingSchemeSummary();
                    String formalname = css.getFormalName();

                    //if (formalname.indexOf("MetaThesaurus") == -1) { // exclude NCI MetaThesaurus for the time being
						String representsVersion = css.getRepresentsVersion();

						String value = formalname + " (version: " + representsVersion + ")";
						nv_vec.add(value);

						csnv2codingSchemeNameMap.put(value, formalname);

						csnv2VersionMap.put(value, representsVersion);
					//}
                 //}
            }
        } catch (Exception e) {
           System.out.println("setCodingSchemeMap exception??? ");
        }
        if (nv_vec.size() > 0) {
			nv_vec = SortUtils.quickSort(nv_vec);
			for (int k=0; k<nv_vec.size(); k++) {
				String value = (String) nv_vec.elementAt(k);
				_ontologies.add(new SelectItem(value, value));
			}
		}
    }
    */

    public static Vector<String> getSupportedAssociationNames(String key) {
        if (csnv2codingSchemeNameMap == null) {
            setCodingSchemeMap();
            return getSupportedAssociationNames(key);
        }
        String codingSchemeName = (String) csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getSupportedAssociationNames(codingSchemeName, version);
    }

    public static Vector<String> getSupportedAssociationNames(
            String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }

        CodingScheme scheme = null;
        try {
            // RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null) {
                System.out.println("scheme is NULL");
                return null;
            }

            Vector<String> v = new Vector<String>();
            SupportedAssociation[] assos = scheme.getMappings()
                    .getSupportedAssociation();
            for (int i = 0; i < assos.length; i++) {
                SupportedAssociation sa = (SupportedAssociation) assos[i];
                v.add(sa.getLocalId());
            }
            return v;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getPropertyNameListData(String key) {
        if (csnv2codingSchemeNameMap == null) {
            setCodingSchemeMap();
        }

        String codingSchemeName = (String) csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null) {
            return null;
        }
        String version = (String) csnv2VersionMap.get(key);
        if (version == null) {
            return null;
        }
        return getPropertyNameListData(codingSchemeName, version);
    }

    public static Vector<String> getPropertyNameListData(
            String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            // RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> propertyNameListData = new Vector<String>();
            SupportedProperty[] properties = scheme.getMappings()
                    .getSupportedProperty();
            for (int i = 0; i < properties.length; i++) {
                SupportedProperty property = properties[i];
                propertyNameListData.add(property.getLocalId());
            }
            return propertyNameListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static Vector<String> getRepresentationalFormListData(String key) {
        String codingSchemeName = (String) csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getRepresentationalFormListData(codingSchemeName, version);
    }

    public static Vector<String> getRepresentationalFormListData(
            String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            // RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> propertyNameListData = new Vector<String>();
            SupportedRepresentationalForm[] forms = scheme.getMappings()
                    .getSupportedRepresentationalForm();
            if (forms != null) {
                for (int i = 0; i < forms.length; i++) {
                    SupportedRepresentationalForm form = forms[i];
                    propertyNameListData.add(form.getLocalId());
                }
            }
            return propertyNameListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getPropertyQualifierListData(String key) {
        String codingSchemeName = (String) csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getPropertyQualifierListData(codingSchemeName, version);
    }

    public static Vector<String> getPropertyQualifierListData(
            String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            // RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> propertyQualifierListData = new Vector<String>();
            SupportedPropertyQualifier[] qualifiers = scheme.getMappings()
                    .getSupportedPropertyQualifier();
            for (int i = 0; i < qualifiers.length; i++) {
                SupportedPropertyQualifier qualifier = qualifiers[i];
                propertyQualifierListData.add(qualifier.getLocalId());
            }

            return propertyQualifierListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getSourceListData(String key) {
        if (csnv2codingSchemeNameMap == null) {
            setCodingSchemeMap();
            return getSourceListData(key);
        }
        String codingSchemeName = (String) csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getSourceListData(codingSchemeName, version);
    }

    public static Vector<String> getSourceListData(String codingSchemeName,
            String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            // RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> sourceListData = new Vector<String>();

            // Insert your code here
            SupportedSource[] sources = scheme.getMappings()
                    .getSupportedSource();
            for (int i = 0; i < sources.length; i++) {
                SupportedSource source = sources[i];
                sourceListData.add(source.getLocalId());
            }

            return sourceListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String int2String(Integer int_obj) {
        if (int_obj == null) {
            return null;
        }

        String retstr = Integer.toString(int_obj);
        return retstr;
    }

    // ==================================================================================================================================

    public static Concept getConceptByCode(String codingSchemeName,
            String vers, String ltag, String code) {
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            if (lbSvc == null) {
                System.out.println("lbSvc == null???");
                return null;
            }
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(vers);

            ConceptReferenceList crefs = createConceptReferenceList(
                    new String[] { code }, codingSchemeName);

            CodedNodeSet cns = null;
            try {
                cns = lbSvc.getCodingSchemeConcepts(codingSchemeName,
                        versionOrTag);
                cns = cns.restrictToCodes(crefs);
                //ResolvedConceptReferenceList matches = cns.resolveToList(null, null, null, 1);
 				ResolvedConceptReferenceList matches = null;
				try {
					matches = cns.resolveToList(null, null, null, 1);
				} catch (Exception e) {
					System.out.println("cns.resolveToList failed???");
				}

                if (matches == null) {
                    System.out.println("Concep not found.");
                    return null;
                }
                int count = matches.getResolvedConceptReferenceCount();
                // Analyze the result ...
                if (count == 0)
                    return null;
                if (count > 0) {
                    try {
                        ResolvedConceptReference ref = (ResolvedConceptReference) matches
                                .enumerateResolvedConceptReference()
                                .nextElement();
                        Concept entry = ref.getReferencedEntry();
                        return entry;
                    } catch (Exception ex1) {
                        System.out.println("Exception entry == null");
                        return null;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
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

    public ResolvedConceptReferenceList getNext(
            ResolvedConceptReferencesIterator iterator) {
        return iterator.getNext();
    }

    public Vector getParentCodes(String scheme, String version, String code) {
        Vector hierarchicalAssoName_vec = getHierarchyAssociationId(scheme,
                version);
        if (hierarchicalAssoName_vec == null
                || hierarchicalAssoName_vec.size() == 0) {
            return null;
        }
        String hierarchicalAssoName = (String) hierarchicalAssoName_vec
                .elementAt(0);
        // KLO, 01/23/2009
        // Vector<Concept> superconcept_vec = util.getAssociationSources(scheme,
        // version, code, hierarchicalAssoName);
        Vector superconcept_vec = getAssociationSourceCodes(scheme, version,
                code, hierarchicalAssoName);
        if (superconcept_vec == null)
            return null;
        // SortUtils.quickSort(superconcept_vec, SortUtils.SORT_BY_CODE);
        return superconcept_vec;

    }

    public Vector getAssociationSourceCodes(String scheme, String version,
            String code, String assocName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            // EVSApplicationService lbSvc = new
            // RemoteServerUtil().createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);

            NameAndValueList nameAndValueList = createNameAndValueList(
                    new String[] { assocName }, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng = cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);

            matches = cng.resolveAsList(ConvenienceMethods
                    .createConceptReference(code, scheme), false, true, 1, 1,
                    new LocalNameList(), null, null, maxReturn);

            if (matches.getResolvedConceptReferenceCount() > 0) {
                Enumeration<ResolvedConceptReference> refEnum = matches
                        .enumerateResolvedConceptReference();

                while (refEnum.hasMoreElements()) {
                    ResolvedConceptReference ref = refEnum.nextElement();
                    AssociationList targetof = ref.getTargetOf();
                    Association[] associations = targetof.getAssociation();


					for (int i = 0; i < associations.length; i++) {
						Association assoc = associations[i];
						// KLO
						assoc = processForAnonomousNodes(assoc);
						AssociatedConcept[] acl = assoc.getAssociatedConcepts()
								.getAssociatedConcept();
						for (int j = 0; j < acl.length; j++) {
							AssociatedConcept ac = acl[j];
							v.add(ac.getReferencedEntry().getEntityCode());
						}
					}
				}
				SortUtils.quickSort(v);
			}


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
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


    public Vector getSubconceptCodes(String scheme, String version, String code) { // throws
        // LBException{
        Vector v = new Vector();
        try {
            // EVSApplicationService lbSvc = new
            // RemoteServerUtil().createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);

            CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
            csvt.setVersion(version);
            String desc = null;
            try {
                desc = lbscm.createCodeNodeSet(new String[] { code }, scheme,
                        csvt).resolveToList(null, null, null, 1)
                        .getResolvedConceptReference(0).getEntityDescription()
                        .getContent();

            } catch (Exception e) {
                desc = "<not found>";

            }

            // Iterate through all hierarchies and levels ...
            String[] hierarchyIDs = lbscm.getHierarchyIDs(scheme, csvt);
            for (int k = 0; k < hierarchyIDs.length; k++) {
                String hierarchyID = hierarchyIDs[k];
                AssociationList associations = null;
                associations = null;
                try {
                    associations = lbscm.getHierarchyLevelNext(scheme, csvt,
                            hierarchyID, code, false, null);
                } catch (Exception e) {
                    System.out.println("getSubconceptCodes - Exception lbscm.getHierarchyLevelNext  ");
                    return v;
                }

                for (int i = 0; i < associations.getAssociationCount(); i++) {
                    Association assoc = associations.getAssociation(i);
                    AssociatedConceptList concepts = assoc
                            .getAssociatedConcepts();
                    for (int j = 0; j < concepts.getAssociatedConceptCount(); j++) {
                        AssociatedConcept concept = concepts
                                .getAssociatedConcept(j);
                        String nextCode = concept.getConceptCode();
                        v.add(nextCode);
                    }
                }
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return v;
    }

    public Vector getSuperconceptCodes(String scheme, String version,
            String code) { // throws LBException{
        long ms = System.currentTimeMillis();
        Vector v = new Vector();
        try {
            // EVSApplicationService lbSvc = new
            // RemoteServerUtil().createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
            csvt.setVersion(version);
            String desc = null;
            try {
                desc = lbscm.createCodeNodeSet(new String[] { code }, scheme,
                        csvt).resolveToList(null, null, null, 1)
                        .getResolvedConceptReference(0).getEntityDescription()
                        .getContent();
            } catch (Exception e) {
                desc = "<not found>";
            }

            // Iterate through all hierarchies and levels ...
            String[] hierarchyIDs = lbscm.getHierarchyIDs(scheme, csvt);
            for (int k = 0; k < hierarchyIDs.length; k++) {
                String hierarchyID = hierarchyIDs[k];
                AssociationList associations = lbscm.getHierarchyLevelPrev(
                        scheme, csvt, hierarchyID, code, false, null);
                for (int i = 0; i < associations.getAssociationCount(); i++) {
                    Association assoc = associations.getAssociation(i);
                    AssociatedConceptList concepts = assoc
                            .getAssociatedConcepts();
                    for (int j = 0; j < concepts.getAssociatedConceptCount(); j++) {
                        AssociatedConcept concept = concepts
                                .getAssociatedConcept(j);
                        String nextCode = concept.getConceptCode();
                        v.add(nextCode);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            System.out.println("Run time (ms): "
                    + (System.currentTimeMillis() - ms));
        }
        return v;
    }

    public Vector getHierarchyAssociationId(String scheme, String version) {

        Vector association_vec = new Vector();
        try {
            // EVSApplicationService lbSvc = new
            // RemoteServerUtil().createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();


            // Will handle secured ontologies later.
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			versionOrTag.setVersion(version);
			CodingScheme cs = lbSvc.resolveCodingScheme(scheme, versionOrTag);
			Mappings mappings = cs.getMappings();
			SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
			java.lang.String[] ids = hierarchies[0].getAssociationNames();


            for (int i = 0; i < ids.length; i++) {
                if (!association_vec.contains(ids[i])) {
                    association_vec.add(ids[i]);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return association_vec;
    }

    public static String getVersion() {
		return getVersion(CODING_SCHEME_NAME);
	}

    public static String getVersion(String coding_scheme_name) {
        String info = getReleaseDate(coding_scheme_name);

        String version = getVocabularyVersionByTag(coding_scheme_name,
                "PRODUCTION");
        if (version == null)
            version = getVocabularyVersionByTag(coding_scheme_name, null);

        if (version != null && version.length() > 0)
            info += " (" + version + ")";
        return info;
    }

    public static String getReleaseDate() {
		return getReleaseDate(CODING_SCHEME_NAME);
    }


    public static String getReleaseDate(String coding_scheme_name) {
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
            HistoryService hs = null;
            try {
            	hs = lbSvc.getHistoryService(coding_scheme_name);
			} catch (Exception ex) {
				System.out.println("WARNING: HistoryService is not available for " + coding_scheme_name);
			}
			if (hs != null) {
				SystemRelease release = hs.getLatestBaseline();
				Date date = release.getReleaseDate();
				return formatter.format(date);
		    }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVocabularyVersionByTag(String codingSchemeName,
            String ltag) {
        if (codingSchemeName == null)
            return null;
        try {
           LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csra.length; i++) {
                CodingSchemeRendering csr = csra[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                if (css.getFormalName().compareTo(codingSchemeName) == 0
                        || css.getLocalName().compareTo(codingSchemeName) == 0) {
                    if (ltag == null) return css.getRepresentsVersion();
                    RenderingDetail rd = csr.getRenderingDetail();
                    CodingSchemeTagList cstl = rd.getVersionTags();
                    java.lang.String[] tags = cstl.getTag();
                    for (int j = 0; j < tags.length; j++) {
                        String version_tag = (String) tags[j];
                        if (version_tag.compareToIgnoreCase(ltag) == 0) {
                            return css.getRepresentsVersion();
                        }
                    }
                }
            }
        } catch (Exception e) {
			System.out.println("Version corresponding to tag " + ltag
					+ " is not found " + " in " + codingSchemeName);

            //e.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getVersionListData(String codingSchemeName) {

        Vector<String> v = new Vector();
        try {
            // RemoteServerUtil rsu = new RemoteServerUtil();
            // EVSApplicationService lbSvc = rsu.createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodingSchemeRenderingList csrl = lbSvc.getSupportedCodingSchemes();
            if (csrl == null)
                System.out.println("csrl is NULL");

            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                CodingSchemeRendering csr = csrs[i];
                Boolean isActive = csr.getRenderingDetail().getVersionStatus()
                        .equals(CodingSchemeVersionStatus.ACTIVE);
                //if (isActive != null && isActive.equals(Boolean.TRUE)) {
                    CodingSchemeSummary css = csr.getCodingSchemeSummary();
                    String formalname = css.getFormalName();
                    if (formalname.compareTo(codingSchemeName) == 0) {
                        String representsVersion = css.getRepresentsVersion();
                        v.add(representsVersion);
                    }
                //}
            }
        } catch (Exception ex) {

        }
        return v;
    }

    public static String getFileName(String pathname) {
        File file = new File(pathname);
        String filename = file.getName();
        return filename;
    }

    protected static Association processForAnonomousNodes(Association assoc) {
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

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static LocalNameList vector2LocalNameList(Vector<String> v) {
        if (v == null)
            return null;
        LocalNameList list = new LocalNameList();
        for (int i = 0; i < v.size(); i++) {
            String vEntry = (String) v.elementAt(i);
            list.addEntry(vEntry);
        }
        return list;
    }

    protected static NameAndValueList createNameAndValueList(Vector names,
            Vector values) {
        if (names == null)
            return null;
        NameAndValueList nvList = new NameAndValueList();
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.elementAt(i);
            String value = (String) values.elementAt(i);
            NameAndValue nv = new NameAndValue();
            nv.setName(name);
            if (value != null) {
                nv.setContent(value);
            }
            nvList.addNameAndValue(nv);
        }
        return nvList;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    public static Vector<SupportedProperty> getSupportedProperties(
            CodingScheme cs) {
        if (cs == null)
            return null;
        Vector<SupportedProperty> v = new Vector<SupportedProperty>();
        SupportedProperty[] properties = cs.getMappings()
                .getSupportedProperty();
        for (int i = 0; i < properties.length; i++) {
            SupportedProperty sp = (SupportedProperty) properties[i];
            v.add(sp);
        }
        return v;
    }

    public static Vector<String> getSupportedPropertyNames(CodingScheme cs) {
        Vector w = getSupportedProperties(cs);
        if (w == null)
            return null;

        Vector<String> v = new Vector<String>();
        for (int i = 0; i < w.size(); i++) {
            SupportedProperty sp = (SupportedProperty) w.elementAt(i);
            v.add(sp.getLocalId());
        }
        return v;
    }

    public static Vector<String> getSupportedPropertyNames(String codingScheme,
            String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            CodingScheme cs = getCodingScheme(codingScheme, versionOrTag);
            return getSupportedPropertyNames(cs);
        } catch (Exception ex) {
        }
        return null;
    }

    public static Vector getPropertyNamesByType(Concept concept,
            String property_type) {
        Vector v = new Vector();
        org.LexGrid.commonTypes.Property[] properties = null;


		if (property_type.compareToIgnoreCase("GENERIC") == 0) {
			properties = concept.getProperty();
		} else if (property_type.compareToIgnoreCase("PRESENTATION") == 0) {
			properties = concept.getPresentation();
		//} else if (property_type.compareToIgnoreCase("INSTRUCTION") == 0) {
		//	properties = concept.getInstruction();
		} else if (property_type.compareToIgnoreCase("COMMENT") == 0) {
			properties = concept.getComment();
		} else if (property_type.compareToIgnoreCase("DEFINITION") == 0) {
			properties = concept.getDefinition();
		}


        if (properties == null || properties.length == 0)
            return v;
        for (int i = 0; i < properties.length; i++) {
            Property p = (Property) properties[i];
            // v.add(p.getText().getContent());
            v.add(p.getPropertyName());
        }
        return v;
    }

    public static Vector getPropertyValues(Concept concept,
            String property_type, String property_name) {
        Vector v = new Vector();
        org.LexGrid.commonTypes.Property[] properties = null;


		if (property_type.compareToIgnoreCase("GENERIC") == 0) {
			properties = concept.getProperty();
		} else if (property_type.compareToIgnoreCase("PRESENTATION") == 0) {
			properties = concept.getPresentation();
		//} else if (property_type.compareToIgnoreCase("INSTRUCTION") == 0) {
		//	properties = concept.getInstruction();
		} else if (property_type.compareToIgnoreCase("COMMENT") == 0) {
			properties = concept.getComment();
		} else if (property_type.compareToIgnoreCase("DEFINITION") == 0) {
			properties = concept.getDefinition();
		} else {

            System.out.println("WARNING: property_type not found -- "
                    + property_type);

        }


		if (properties == null || properties.length == 0)
			return v;
		for (int i = 0; i < properties.length; i++) {
			Property p = (Property) properties[i];
			if (property_name.compareTo(p.getPropertyName()) == 0) {
				String t = p.getValue().getContent();
				Source[] sources = p.getSource();
				if (sources != null && sources.length > 0) {
					Source src = sources[0];
					t = t + "|" + src.getContent();
				}
				v.add(t);
			}
		}
		return v;
	}


    // =====================================================================================

    public List getSupportedRoleNames(LexBIGService lbSvc, String scheme,
            String version) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);


		List list = new ArrayList();
		try {
			CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
			Relations[] relations = cs.getRelations();
			for (int i = 0; i < relations.length; i++) {
				Relations relation = relations[i];
				if (relation.getContainerName().compareToIgnoreCase("roles") == 0) {
					org.LexGrid.relations.Association[] asso_array = relation
							.getAssociation();
					for (int j = 0; j < asso_array.length; j++) {
						org.LexGrid.relations.Association association = (org.LexGrid.relations.Association) asso_array[j];
						list.add(association.getAssociationName());
					}
				}
			}
		} catch (Exception ex) {


        }
        return list;
    }

    public static void sortArray(ArrayList list) {
        String tmp;
        if (list.size() <= 1)
            return;
        for (int i = 0; i < list.size(); i++) {
            String s1 = (String) list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                String s2 = (String) list.get(j);
                if (s1.compareToIgnoreCase(s2) > 0) {
                    tmp = s1;
                    list.set(i, s2);
                    list.set(j, tmp);
                }
            }
        }
    }

    public static void sortArray(String[] strArray) {
        String tmp;
        if (strArray.length <= 1)
            return;
        for (int i = 0; i < strArray.length; i++) {
            for (int j = i + 1; j < strArray.length; j++) {
                if (strArray[i].compareToIgnoreCase(strArray[j]) > 0) {
                    tmp = strArray[i];
                    strArray[i] = strArray[j];
                    strArray[j] = tmp;
                }
            }
        }
    }

    public String[] getSortedKeys(HashMap map) {
        if (map == null)
            return null;
        Set keyset = map.keySet();
        String[] names = new String[keyset.size()];
        Iterator it = keyset.iterator();
        int i = 0;
        while (it.hasNext()) {
            String s = (String) it.next();
            names[i] = s;
            i++;
        }
        sortArray(names);
        return names;
    }

    public String getPreferredName(Concept c) {


		Presentation[] presentations = c.getPresentation();
		for (int i = 0; i < presentations.length; i++) {
			Presentation p = presentations[i];
			if (p.getPropertyName().compareTo("Preferred_Name") == 0) {
				return p.getValue().getContent();
			}
		}
		return null;
	}


    public HashMap getRelationshipHashMap(String scheme, String version,
            String code) {
        // EVSApplicationService lbSvc = new
        // RemoteServerUtil().createLexBIGService();
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        // Perform the query ...
        ResolvedConceptReferenceList matches = null;

        List list = getSupportedRoleNames(lbSvc, scheme, version);

        ArrayList roleList = new ArrayList();
        ArrayList associationList = new ArrayList();
        ArrayList superconceptList = new ArrayList();
        ArrayList subconceptList = new ArrayList();


        HashMap map = new HashMap();

		String[] associationsToNavigate = TreeUtils.getAssociationsToNavigate(scheme, version);
		Vector w = new Vector();
		for (int k=0; k<associationsToNavigate.length; k++) {
			w.add(associationsToNavigate[k]);
		}


		HashMap hmap_super = TreeUtils.getSuperconcepts(scheme, version, code);
		if (hmap_super != null) {
			TreeItem ti = (TreeItem) hmap_super.get(code);
			for (String association : ti.assocToChildMap.keySet()) {
				List<TreeItem> children = ti.assocToChildMap.get(association);
				for (TreeItem childItem : children) {
					superconceptList.add(childItem.text + "|" + childItem.code);
				}
			}
		}
		Collections.sort(superconceptList);
		map.put(TYPE_SUPERCONCEPT, superconceptList);

		HashMap hmap_sub = TreeUtils.getSubconcepts(scheme, version, code);
		if (hmap_sub != null) {
			TreeItem ti = (TreeItem) hmap_sub.get(code);
			for (String association : ti.assocToChildMap.keySet()) {
				List<TreeItem> children = ti.assocToChildMap.get(association);
				for (TreeItem childItem : children) {
					subconceptList.add(childItem.text + "|" + childItem.code);
				}
			}
		}

		Collections.sort(subconceptList);
		map.put(TYPE_SUBCONCEPT, subconceptList);

        try {
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);

            // ResolvedConceptReferenceList branch = cng.resolveAsList(focus,
            // associationsNavigatedFwd,
            // !associationsNavigatedFwd, -1, 2, noopList_, null, null, null,
            // -1, false);

            matches = cng.resolveAsList(ConvenienceMethods
                    .createConceptReference(code, scheme),
            // true, false, 1, 1, new LocalNameList(), null, null, 1024);
                    true, false, 1, 1, noopList_, null, null, null, -1, false);

            if (matches.getResolvedConceptReferenceCount() > 0) {
                Enumeration<ResolvedConceptReference> refEnum = matches
                        .enumerateResolvedConceptReference();

                while (refEnum.hasMoreElements()) {
                    ResolvedConceptReference ref = refEnum.nextElement();
                    AssociationList sourceof = ref.getSourceOf();
                    Association[] associations = sourceof.getAssociation();

                    for (int i = 0; i < associations.length; i++) {
                        Association assoc = associations[i];
                        String associationName = assoc.getAssociationName();

                        boolean isRole = false;
                        if (list.contains(associationName)) {
                            isRole = true;
                        }

                        AssociatedConcept[] acl = assoc.getAssociatedConcepts()
                                .getAssociatedConcept();
                        for (int j = 0; j < acl.length; j++) {
                            AssociatedConcept ac = acl[j];
                            EntityDescription ed = ac.getEntityDescription();

                            String name = "No Description";
                            if (ed != null)
                                name = ed.getContent();

                            // String pt =
                            // getPreferredName(ac.getReferencedEntry());
                            String pt = name;
                            if (associationName.compareToIgnoreCase("equivalentClass") != 0) {

								//
								//String[] sortedArray = new String[]{"ant", "bat", "cat", "dog"};

								    // Search for the word "cat"
								//    int index = Arrays.binarySearch(sortedArray, "cat");
								//


								if (!w.contains(associationName)) {
									// printAssocation(scheme, version, code,
									// assoc.getAssociationName(),
									// ac.getConceptCode());
									String s = associationName + "|" + pt + "|"
											+ ac.getConceptCode();
									if (isRole) {
										if (associationName.compareToIgnoreCase("hasSubtype") != 0) {
											// System.out.println("Adding role: " +
											// s);
											roleList.add(s);
										}
									} else {
										// System.out.println("Adding association: "
										// + s);
										associationList.add(s);
									}
							    }
                            }
                        }
                    }
                }
            }

            if (roleList.size() > 0) {
                Collections.sort(roleList);
            }

            if (associationList.size() > 0) {
                Collections.sort(associationList);

            }

            map.put(TYPE_ROLE, roleList);
            map.put(TYPE_ASSOCIATION, associationList);

/*
NCI Thesaurus:

			Vector superconcept_vec = getSuperconcepts(scheme, version, code);
			for (int i = 0; i < superconcept_vec.size(); i++) {
				Concept c = (Concept) superconcept_vec.elementAt(i);
				String pt = getPreferredName(c);
				superconceptList.add(pt + "|" + c.getEntityCode());
			}

            Collections.sort(superconceptList);
            map.put(TYPE_SUPERCONCEPT, superconceptList);


			Vector subconcept_vec = getSubconcepts(scheme, version, code);
			for (int i = 0; i < subconcept_vec.size(); i++) {
				Concept c = (Concept) subconcept_vec.elementAt(i);
				String pt = getPreferredName(c);
				subconceptList.add(pt + "|" + c.getEntityCode());
			}
			Collections.sort(subconceptList);
			map.put(TYPE_SUBCONCEPT, subconceptList);
*/




        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Vector getSuperconcepts(String scheme, String version, String code) {
        // String assocName = "hasSubtype";
        String hierarchicalAssoName = "hasSubtype";
        Vector hierarchicalAssoName_vec = getHierarchyAssociationId(scheme,
                version);
        if (hierarchicalAssoName_vec != null
                && hierarchicalAssoName_vec.size() > 0) {
            hierarchicalAssoName = (String) hierarchicalAssoName_vec
                    .elementAt(0);
        }
        return getAssociationSources(scheme, version, code,
                hierarchicalAssoName);
    }

    public Vector getAssociationSources(String scheme, String version,
            String code, String assocName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            // EVSApplicationService lbSvc = new
            // RemoteServerUtil().createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            NameAndValueList nameAndValueList = createNameAndValueList(
                    new String[] { assocName }, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng = cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);
            ConceptReference graphFocus = ConvenienceMethods
                    .createConceptReference(code, scheme);

            boolean resolveForward = false;
            boolean resolveBackward = true;

            int resolveAssociationDepth = 1;
            int maxToReturn = 1000;

            ResolvedConceptReferencesIterator iterator = codedNodeGraph2CodedNodeSetIterator(
                    cng, graphFocus, resolveForward, resolveBackward,
                    resolveAssociationDepth, maxToReturn);

            v = resolveIterator(iterator, maxToReturn, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    public Vector getSubconcepts(String scheme, String version, String code) {
        // String assocName = "hasSubtype";
        String hierarchicalAssoName = "hasSubtype";
        Vector hierarchicalAssoName_vec = getHierarchyAssociationId(scheme,
                version);
        if (hierarchicalAssoName_vec != null
                && hierarchicalAssoName_vec.size() > 0) {
            hierarchicalAssoName = (String) hierarchicalAssoName_vec
                    .elementAt(0);
        }
        return getAssociationTargets(scheme, version, code,
                hierarchicalAssoName);
    }

    public Vector getAssociationTargets(String scheme, String version,
            String code, String assocName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            // EVSApplicationService lbSvc = new
            // RemoteServerUtil().createLexBIGService();
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            NameAndValueList nameAndValueList = createNameAndValueList(
                    new String[] { assocName }, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng = cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);
            ConceptReference graphFocus = ConvenienceMethods
                    .createConceptReference(code, scheme);

            boolean resolveForward = true;
            boolean resolveBackward = false;

            int resolveAssociationDepth = 1;
            int maxToReturn = 1000;

            ResolvedConceptReferencesIterator iterator = codedNodeGraph2CodedNodeSetIterator(
                    cng, graphFocus, resolveForward, resolveBackward,
                    resolveAssociationDepth, maxToReturn);

            v = resolveIterator(iterator, maxToReturn, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    public ResolvedConceptReferencesIterator codedNodeGraph2CodedNodeSetIterator(
            CodedNodeGraph cng, ConceptReference graphFocus,
            boolean resolveForward, boolean resolveBackward,
            int resolveAssociationDepth, int maxToReturn) {
        CodedNodeSet cns = null;
        try {
            cns = cng.toNodeList(graphFocus, resolveForward, resolveBackward,
                    resolveAssociationDepth, maxToReturn);

            if (cns == null) {
                System.out.println("cng.toNodeList returns null???");
                return null;
            }

            SortOptionList sortCriteria = null;
            // Constructors.createSortOptionList(new String[]{"matchToQuery",
            // "code"});

            LocalNameList propertyNames = null;
            CodedNodeSet.PropertyType[] propertyTypes = null;
            ResolvedConceptReferencesIterator iterator = null;
            try {
                iterator = cns.resolve(sortCriteria, propertyNames,
                        propertyTypes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (iterator == null) {
                System.out.println("cns.resolve returns null???");
            }
            return iterator;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Vector resolveIterator(ResolvedConceptReferencesIterator iterator,
            int maxToReturn) {
        return resolveIterator(iterator, maxToReturn, null);
    }


	public Vector resolveIterator(ResolvedConceptReferencesIterator iterator,
			int maxToReturn, String code) {
		Vector v = new Vector();
		if (iterator == null) {
			System.out.println("No match.");
			return v;
		}
		try {
			int iteration = 0;
			while (iterator.hasNext()) {
				iteration++;
				iterator = iterator.scroll(maxToReturn);
				ResolvedConceptReferenceList rcrl = iterator.getNext();
				ResolvedConceptReference[] rcra = rcrl
						.getResolvedConceptReference();
				for (int i = 0; i < rcra.length; i++) {
					ResolvedConceptReference rcr = rcra[i];
					org.LexGrid.concepts.Concept ce = rcr.getReferencedEntry();
					// System.out.println("Iteration " + iteration + " " +
					// ce.getEntityCode() + " " +
					// ce.getEntityDescription().getContent());
					if (code == null) {
						v.add(ce);
					} else {
						if (ce.getEntityCode().compareTo(code) != 0)
							v.add(ce);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	public static Vector<String> parseData(String line) {
		String tab = "|";
		return parseData(line, tab);
	}


    public static Vector<String> parseData(String line, String tab) {
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

    public static String getHyperlink(String url, String codingScheme,
            String code) {
        codingScheme = codingScheme.replace(" ", "%20");
        String link = url + "/ConceptReport.jsp?dictionary=" + codingScheme
                + "&code=" + code;
        return link;
    }

    public List getHierarchyRoots(String scheme, String version,
            String hierarchyID) throws LBException {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        return getHierarchyRoots(scheme, csvt, hierarchyID);
    }

    public List getHierarchyRoots(String scheme, CodingSchemeVersionOrTag csvt,
            String hierarchyID) throws LBException {
        int maxDepth = 1;
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbSvc
                .getGenericExtension("LexBIGServiceConvenienceMethods");
        lbscm.setLexBIGService(lbSvc);

        ResolvedConceptReferenceList roots = lbscm.getHierarchyRoots(scheme,
                csvt, hierarchyID);

        for (int i = 0; i < roots.getResolvedConceptReferenceCount(); i++) {
             ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
             if (rcr.getEntityDescription() == null) {
				 String name = TreeUtils.getCodeDescription(lbSvc, scheme, csvt, rcr.getConceptCode());
				 if (name == null) name = rcr.getConceptCode();//HL7
				 EntityDescription e = new EntityDescription();
				 e.setContent(name);
				 rcr.setEntityDescription(e);
			 } else if (rcr.getEntityDescription().getContent() == null) {
				 String name = TreeUtils.getCodeDescription(lbSvc, scheme, csvt, rcr.getConceptCode());
				 if (name == null) name = rcr.getConceptCode();//HL7
				 EntityDescription e = new EntityDescription();
				 e.setContent(name);
				 rcr.setEntityDescription(e);
			 }
		 }

        List list = ResolvedConceptReferenceList2List(roots);
        SortUtils.quickSort(list);
        return list;
    }

    public List ResolvedConceptReferenceList2List(
            ResolvedConceptReferenceList rcrl) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < rcrl.getResolvedConceptReferenceCount(); i++) {
            ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
            list.add(rcr);
        }
        return list;
    }


	/*
	 * protected List getAncestors( String scheme, CodingSchemeVersionOrTag
	 * csvt, String hierarchyID, String code, int maxDistance) throws
	 * LBException { LexBIGService lbSvc =
	 * RemoteServerUtil.createLexBIGService(); LexBIGServiceConvenienceMethods
	 * lbscm = (LexBIGServiceConvenienceMethods)
	 * lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
	 * lbscm.setLexBIGService(lbSvc);
	 *
	 * ArrayList list = new ArrayList(); int currentDistance = 0; try {
	 * addAncestorsToList(lbscm, scheme, csvt, hierarchyID, code, maxDistance,
	 * currentDistance, list); } catch (Exception ex) { ex.printStackTrace(); }
	 * return list; }
	 *
	 *
	 * protected void addAncestorsToList( LexBIGServiceConvenienceMethods lbscm,
	 * String scheme, CodingSchemeVersionOrTag csvt, String hierarchyID, String
	 * code, int maxDistance, int currentDistance, List list) throws LBException {
	 * if (maxDistance < 0 || currentDistance < maxDistance) { AssociationList
	 * associations = lbscm.getHierarchyLevelNext(scheme, csvt, hierarchyID,
	 * code, false, null); for (int i = 0; i <
	 * associations.getAssociationCount(); i++) { Association assoc =
	 * associations.getAssociation(i); AssociatedConceptList concepts =
	 * assoc.getAssociatedConcepts();
	 *
	 * if (concepts.getAssociatedConceptCount() == 0) { Concept c =
	 * getConceptByCode(scheme, null, null, code); org.LexGrid.concepts.Concept
	 * ce = new org.LexGrid.concepts.Concept(); ce.setId(c.getEntityCode());
	 * //ce.setEntityDescription(c.getEntityDescription().getContent());
	 * ce.setEntityDescription(c.getEntityDescription()); list.add(ce); }
	 *
	 * for (int j = 0; j < concepts.getAssociatedConceptCount(); j++) {
	 * AssociatedConcept concept = concepts.getAssociatedConcept(j); String
	 * nextCode = concept.getConceptCode(); String nextDesc =
	 * concept.getEntityDescription().getContent();
	 *
	 * if (currentDistance == maxDistance) { org.LexGrid.concepts.Concept ce =
	 * new org.LexGrid.concepts.Concept(); ce.setId(nextCode); EntityDescription
	 * ed = new EntityDescription(); ed.setContent(nextDesc);
	 * ce.setEntityDescription(ed); list.add(ce); } addAncestorsToList(lbscm,
	 * scheme, csvt, hierarchyID, nextCode, maxDistance, currentDistance + 1,
	 * list); } } } }
	 */
	public static Vector getSynonyms(String scheme, String version, String tag,
			String code) {
		Vector v = new Vector();
		Concept concept = getConceptByCode(scheme, version, tag, code);
		// KLO, 091009
		//getSynonyms(concept);
		return getSynonyms(scheme, concept);
	}


	public static Vector getSynonyms(Concept concept) {
		if (concept == null)
			return null;
		Vector v = new Vector();
		Presentation[] properties = concept.getPresentation();
		int n = 0;
		for (int i = 0; i < properties.length; i++) {
			Presentation p = properties[i];
			//if (p.getPropertyName().compareTo("FULL_SYN") == 0) {
				String term_name = p.getValue().getContent();
				String term_type = "null";
				String term_source = "null";
				String term_source_code = "null";

				PropertyQualifier[] qualifiers = p.getPropertyQualifier();
				if (qualifiers != null) {
					for (int j = 0; j < qualifiers.length; j++) {
						PropertyQualifier q = qualifiers[j];
						String qualifier_name = q.getPropertyQualifierName();
						String qualifier_value = q.getValue().getContent();
						if (qualifier_name.compareTo("source-code") == 0) {
							term_source_code = qualifier_value;
							break;
						}
					}
				}
				term_type = p.getRepresentationalForm();
				Source[] sources = p.getSource();
				if (sources != null && sources.length > 0) {
					Source src = sources[0];
					term_source = src.getContent();
				}
				v.add(term_name + "|" + term_type + "|" + term_source + "|"
						+ term_source_code);
			//}
		}
		SortUtils.quickSort(v);
		return v;
	}


	public static Vector getSynonyms(String scheme, Concept concept) {
		if (concept == null)
			return null;
		Vector v = new Vector();
		Presentation[] properties = concept.getPresentation();
		int n = 0;
		boolean inclusion = true;
		for (int i = 0; i < properties.length; i++) {
			Presentation p = properties[i];
			// for NCI Thesaurus or Pre-NCI Thesaurus, show FULL_SYNs only
			if (scheme != null && scheme.indexOf(CODING_SCHEME_NAME) != -1) {
				inclusion = false;
				if (p.getPropertyName().compareTo("FULL_SYN") == 0) {
					inclusion = true;
				}
			}
			if (inclusion) {
				String term_name = p.getValue().getContent();
				String term_type = "null";
				String term_source = "null";
				String term_source_code = "null";

				PropertyQualifier[] qualifiers = p.getPropertyQualifier();
				if (qualifiers != null) {
					for (int j = 0; j < qualifiers.length; j++) {
						PropertyQualifier q = qualifiers[j];
						String qualifier_name = q.getPropertyQualifierName();
						String qualifier_value = q.getValue().getContent();
						if (qualifier_name.compareTo("source-code") == 0) {
							term_source_code = qualifier_value;
							break;
						}
					}
				}
				term_type = p.getRepresentationalForm();
				Source[] sources = p.getSource();
				if (sources != null && sources.length > 0) {
					Source src = sources[0];
					term_source = src.getContent();
				}
				v.add(term_name + "|" + term_type + "|" + term_source + "|"
						+ term_source_code);
			}
		}
		SortUtils.quickSort(v);
		return v;
	}


    public String getNCICBContactURL() {
        if (NCICBContactURL != null) {
            return NCICBContactURL;
        }
        String default_url = "ncicb@pop.nci.nih.gov";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            NCICBContactURL = properties
                    .getProperty(NCItBrowserProperties.NCICB_CONTACT_URL);
            if (NCICBContactURL == null) {
                NCICBContactURL = default_url;
            }
        } catch (Exception ex) {

        }

        //System.out.println("getNCICBContactURL returns " + NCICBContactURL);
        return NCICBContactURL;
    }

    public String getTerminologySubsetDownloadURL() {
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            terminologySubsetDownloadURL = properties
                    .getProperty(NCItBrowserProperties.TERMINOLOGY_SUBSET_DOWNLOAD_URL);
        } catch (Exception ex) {

        }
        return terminologySubsetDownloadURL;
    }


    public String getNCITBuildInfo() {
        if (NCITBuildInfo != null) {
            return NCITBuildInfo;
        }
        String default_info = "N/A";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            NCITBuildInfo = properties
                    .getProperty(NCItBrowserProperties.NCIT_BUILD_INFO);
            if (NCITBuildInfo == null) {
                NCITBuildInfo = default_info;
            }
        } catch (Exception ex) {
			ex.printStackTrace();
        }

        //System.out.println("getNCITBuildInfo returns " + NCITBuildInfo);
        return NCITBuildInfo;
    }

    public String getNCImURL() {
        if (NCImURL != null) {
            return NCImURL;
        }
        String default_info = "N/A";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            NCImURL = properties
                    .getProperty(NCItBrowserProperties.NCIM_URL);
            if (NCImURL == null) {
                NCImURL = default_info;
            }
        } catch (Exception ex) {

        }
        return NCImURL;
    }

    public String getTermSuggestionURL() {
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            term_suggestion_application_url = properties
                    .getProperty(NCItBrowserProperties.TERM_SUGGESTION_APPLICATION_URL);
        } catch (Exception ex) {

        }
        return term_suggestion_application_url;
    }

    public static String getTermSuggestionURL(String codingSchemeName, String version) {
		String propertyName = "term_suggestion_application_url";
		String term_suggestion_application_url = "";
		String urn = null;

        if (version == null) {
            version = getVocabularyVersionByTag(codingSchemeName, "PRODUCTION");
            if (version == null) version = getVocabularyVersionByTag(codingSchemeName, null);
		}

        Vector v = MetadataUtils.getMetadataValues(codingSchemeName, version, urn, propertyName);
        if (v != null && v.size() > 0) return (String) v.elementAt(0);

        return term_suggestion_application_url;
    }


///////////////////////////////////////////////////////////////////////////////
/*
    public static Vector getSupportedCodingSchemeNames()
	{
        LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		if (lbSvc == null)
		{
			System.out.println("lbSvc == null..???" );
			return null;
		}

		Vector v = new Vector();
        try {
            CodingSchemeRenderingList csrl = null;
            try {
				csrl = lbSvc.getSupportedCodingSchemes();
			} catch (LBInvocationException ex) {
				ex.printStackTrace();
				System.out.println("lbSvc.getSupportedCodingSchemes() FAILED..." + ex.getCause() );
				return null;
			}

			CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
			for (int i=0; i<csrs.length; i++)
			{
				CodingSchemeRendering csr = csrs[i];
            	Boolean isActive = csr.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE);
				//System.out.println("\nActive? " + isActive);

				if (isActive != null && isActive.equals(Boolean.TRUE))
				{
					 CodingSchemeSummary css = csr.getCodingSchemeSummary();
					 String formalname = css.getFormalName();
					 v.add(formalname);

					 System.out.println("\tformalname: " + formalname);
					 EntityDescription ed = css.getCodingSchemeDescription();
					 System.out.println("\tcodingSchemeDescription: " + ed.getContent());
					 java.lang.String uri = css.getCodingSchemeURI();
					 System.out.println("\tURI: " + uri);
					 java.lang.String localName = css.getLocalName();
					 System.out.println("\tlocalName: " + localName);
					 java.lang.String version = css.getRepresentsVersion();
					 System.out.println("\tversion: " + version + "\n");
					 CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
					 CodingScheme cs = lbSvc.resolveCodingScheme(formalname, versionOrTag);
					 SupportedNamespace[] namespaces = cs.getMappings().getSupportedNamespace();
					 for (int j=0; j<namespaces.length; j++) {
                         SupportedNamespace ns =  namespaces[j];
                         java.lang.String ns_name = ns.getEquivalentCodingScheme();
                         java.lang.String ns_id = ns.getContent() ;
                         System.out.println("\tns_name: " + ns_name + " ns_id:" + ns_id);
					 }

			    }
			}
	    } catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return SortUtils.quickSort(v);
	}
*/

    public static CodingScheme resolveCodingScheme(LexBIGService lbSvc, String formalname, CodingSchemeVersionOrTag versionOrTag) {
		try {
			CodingScheme cs = lbSvc.resolveCodingScheme(formalname, versionOrTag);

			return cs;
		} catch (Exception ex) {
			System.out.println("(*) Unable to resolveCodingScheme " +  formalname);
			System.out.println("(*) \tMay require security token. " );
	    }
        return null;
	}




    public static HashMap getNamespaceId2CodingSchemeFormalNameMapping()
	{
		if (namespace2CodingScheme != null) {
			return namespace2CodingScheme;
		}

		HashMap hmap = new HashMap();
        LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		if (lbSvc == null)
		{
			System.out.println("setCodingSchemeMap..??????????????" );
			return null;
		}

		CodingSchemeRenderingList csrl = null;
		CodingSchemeRendering[] csrs = null;

		try {
			csrl = lbSvc.getSupportedCodingSchemes();
			csrs = csrl.getCodingSchemeRendering();
		} catch (LBInvocationException ex) {
			System.out.println("lbSvc.getSupportedCodingSchemes() FAILED..." + ex.getCause() );
			return null;
		}

		for (int i=0; i<csrs.length; i++)
		{
			CodingSchemeRendering csr = csrs[i];
			if (csr != null && csr.getRenderingDetail() != null) {
				Boolean isActive = null;
				if (csr == null) {
					System.out.println("\tcsr == null???");
				} else if (csr.getRenderingDetail() == null) {
					System.out.println("\tcsr.getRenderingDetail() == null");
				} else if (csr.getRenderingDetail().getVersionStatus() == null) {
					System.out.println("\tcsr.getRenderingDetail().getVersionStatus() == null");
				} else {

					isActive = csr.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE);
				}

				//System.out.println("\nActive? " + isActive);

				//if (isActive != null && isActive.equals(Boolean.TRUE))
				{
					CodingSchemeSummary css = csr.getCodingSchemeSummary();
					String formalname = css.getFormalName();

					 java.lang.String version = css.getRepresentsVersion();
					 CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
					 if (version != null) versionOrTag.setVersion(version);

					 CodingScheme cs = null;
					 cs = resolveCodingScheme(lbSvc, formalname, versionOrTag);
					 if (cs != null) {
						 Mappings mapping = cs.getMappings();
						 if (mapping != null) {
							 SupportedNamespace[] namespaces = mapping.getSupportedNamespace();
							 if (namespaces != null) {
								 for (int j=0; j<namespaces.length; j++) {
									 SupportedNamespace ns =  namespaces[j];
									 if (ns != null) {
										 java.lang.String ns_name = ns.getEquivalentCodingScheme();
										 java.lang.String ns_id = ns.getContent() ;
										 //System.out.println("\tns_name: " + ns_name + " ns_id:" + ns_id);
										 if (ns_id != null && ns_id.compareTo("") != 0) {
											 hmap.put(ns_id, formalname);
										 }
									 }
								 }
							 }
						 }
					 } else {
						 System.out.println("??? Unable to resolveCodingScheme " + formalname);
					 }
				}
		    }
		}

		namespace2CodingScheme = hmap;
		return hmap;
	}

    public static String getCodingSchemeName(String key) {
		return key2CodingSchemeName(key);
	}

    public static String getCodingSchemeVersion(String key) {
		return key2CodingSchemeVersion(key);
	}

    public static String key2CodingSchemeName(String key) {
		if (key == null) {
			System.out.println("key2CodingSchemeName key == NULL???");
			return null;
		}
		if (csnv2codingSchemeNameMap == null) {
			System.out.println("setCodingSchemeMap");
			setCodingSchemeMap();
		}

		if (key.indexOf("%20") != -1) {
			key.replaceAll("%20", " ");
		}

		if (csnv2codingSchemeNameMap == null) {
			System.out.println("csnv2codingSchemeNameMap == NULL???");
			return key;
		}

		String value = (String) csnv2codingSchemeNameMap.get(key);
		if (value == null) {
			System.out.println("key2CodingSchemeName returns " + key);
			return key;
		}
		return value;
	}

    public static String key2CodingSchemeVersion(String key) {
		if (key == null) {
			return null;
		}
		if (csnv2VersionMap == null) setCodingSchemeMap();
		if (key.indexOf("%20") != -1) {
			key.replaceAll("%20", " ");
		}

		if (csnv2VersionMap == null) {
			System.out.println("csnv2VersionMap == NULL???");
			return key;
		}

		String value = (String) csnv2VersionMap.get(key);
		return value;
	}


    public static String replaceAll(String s, String t1, String t2) {
		s = s.replaceAll(t1, t2);
		return s;
	}
//////////////////////////////////////////////////////////////////

    public static String getDownloadLink(String url) {
       String t = "<a href=\"" + url + "\" target=\"_blank\" alt=\"Download Site\">" + url + "</a>";
       return t;
    }
}
