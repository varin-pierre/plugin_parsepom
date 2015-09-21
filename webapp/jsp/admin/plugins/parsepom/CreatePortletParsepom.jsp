
<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="ParsepomPortlet" scope="session" class="fr.paris.lutece.plugins.parsepom.web.portlet.ParsepomPortletJspBean" />

<% ParsepomPortlet.init( request, ParsepomPortlet.RIGHT_MANAGE_ADMIN_SITE); %>
<%= ParsepomPortlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>


