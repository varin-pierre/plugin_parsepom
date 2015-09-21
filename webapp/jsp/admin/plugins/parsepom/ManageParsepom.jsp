<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="manageparsepom" scope="session" class="fr.paris.lutece.plugins.parsepom.web.ManageParsepomJspBean" />

<% manageparsepom.init( request, manageparsepom.RIGHT_MANAGEPARSEPOM ); %>
<%= manageparsepom.getManageParsepomHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
