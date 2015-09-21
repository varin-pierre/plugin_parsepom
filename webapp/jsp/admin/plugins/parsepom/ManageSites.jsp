<jsp:useBean id="manageparsepomSite" scope="session" class="fr.paris.lutece.plugins.parsepom.web.SiteJspBean" />
<% String strContent = manageparsepomSite.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
