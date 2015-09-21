<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="ParsepomPortlet" scope="session" class="fr.paris.lutece.plugins.parsepom.web.portlet.ParsepomPortletJspBean" />

<%
    ParsepomPortlet.init( request, ParsepomPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( ParsepomPortlet.doModify( request ) );
%>


