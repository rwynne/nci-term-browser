<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>

<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/yahoo-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/event-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/dom-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/animation-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/container-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/connection-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/autocomplete-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/treeview-min.js" ></script>
<%
  String basePath = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
  <title>Mapping Vocabulary</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/fonts.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/grids.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/code.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/tree.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>

  <script language="JavaScript">

  </script>
 
  
</head>
<body>
  <f:view>

    <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea">
        <table class="evsLogoBg" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top">
            <a href="http://evs.nci.nih.gov/" target="_blank" alt="Enterprise Vocabulary Services">
              <img src="<%=basePath%>/images/evs-popup-logo.gif" width="213" height="26" alt="EVS: Enterprise Vocabulary Services" title="EVS: Enterprise Vocabulary Services" border="0" />
            </a>
          </td>
          <td valign="top"><div id="closeWindow"><a href="javascript:window.close();"><img src="<%=basePath%>/images/thesaurus_close_icon.gif" width="10" height="10" border="0" alt="Close Window" />&nbsp;CLOSE WINDOW</a></div></td>
        </tr>
        </table>

<%
String mapping_dictionary = request.getParameter("dictionary");
String mapping_version = request.getParameter("version");

String mapping_schema = request.getParameter("schema");
if (mapping_dictionary != null && mapping_schema == null) mapping_schema = mapping_dictionary;

String term_browser_version = DataUtils.getMetadataValue(mapping_schema, mapping_version, "term_browser_version");
String display_name = DataUtils.getMetadataValue(mapping_schema, mapping_version, "display_name");


if (display_name == null || display_name.compareTo("null") == 0) {
   display_name = DataUtils.getLocalName(mapping_schema); 
}

if (mapping_schema.compareTo("NCI Thesaurus") == 0) {
%>
    <div><img src="<%=basePath%>/images/thesaurus_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" /></div>
<%
} else {
     String mapping_shortName = DataUtils.getLocalName(mapping_schema);
%>
    <div>
      <img src="<%=basePath%>/images/other_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
      <div class="vocabularynamepopupshort"><%=HTTPUtils.cleanXSS(display_name)%></div>
    </div>
<%
}
%>
        <div id="popupContentArea">
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <th>Source Code</th>
            <th>Source Name</th>
            <th>REL</th>
            <th>Map Rank</th>
            <th>Target Code</th>
            <th>Target Name</th>
            <tr>
            <%
                String source_scheme = "NCI_Thesaurus";
                String source_version = "10.06e";
                
                String target_scheme = "ICD-9-CM";
                String target_version = "2010";

                
                String source_code = "C25765";
                String source_name = "Secondary Acute Myeloid Leukemia";
                String rel = "RN";
                String score = "6";
                String target_code = "205";
                String target_name = "Myeloid leukemia, acute";
            %>
		    <td>
<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a> 

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a> 
		    
		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>
		    
<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=target_code%>
</a> 

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a> 		    
                    </td>
		    <td><%=target_name%></td>
            </tr> 
          </table>

          <!-- End of Tree control content -->
        </div>
      </div>
    </div>
  </f:view>
</body>
</html>