<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<f:view>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">

        <%
        //String msg_dictionary = gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getSession().getAttribute("dictionary"));
        String msg_dictionary = (String) request.getSession().getAttribute("dictionary");
        
        if (msg_dictionary == null) {
        %>
          <%@ include file="/pages/templates/content-header-termbrowser.jsp" %>
        <%
        } else if (msg_dictionary.compareTo("NCI Thesaurus") == 0) {
        %>
          <%@ include file="/pages/templates/content-header.jsp" %>
        <%
        } else {
                request.getSession().setAttribute("dictionary", msg_dictionary);
        %>
                <%@ include file="/pages/templates/content-header-other.jsp" %>
        <%
        }
        %>




      <!-- Page content -->
      <div class="pagecontent">
        <%
          String message = (String) request.getSession().getAttribute("message");
        %>
        <p class="textbodyred">&nbsp;<%=message%></p>
        <%@ include file="/pages/templates/nciFooter.html" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>