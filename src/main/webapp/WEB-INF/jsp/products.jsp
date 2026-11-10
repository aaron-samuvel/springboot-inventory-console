<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inventory Console</title>
    <link rel="stylesheet" href="<c:url value='/assets/app.css'/>">
</head>
<body>
<main class="shell">
    <header class="hero">
        <div>
            <p class="eyebrow">SPRING MVC · JSP · JSTL · TOMCAT</p>
            <h1>Inventory Console</h1>
            <p class="muted">A server rendered inventory workspace with indexed search and bounded pages.</p>
        </div>
        <form method="post" action="/logout" class="logout-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <button class="secondary" type="submit">Sign out</button>
        </form>
    </header>

    <c:if test="${not empty notice}">
        <p class="notice" role="status"><c:out value="${notice}"/></p>
    </c:if>

    <section class="card">
        <h2>Find inventory</h2>
        <form method="get" action="/products" class="search-form">
            <label for="q">SKU or name prefix</label>
            <div class="form-row">
                <input id="q" name="q" maxlength="80" value="<c:out value='${query}'/>" placeholder="e.g. bearing">
                <button type="submit">Search</button>
            </div>
        </form>
        <div class="table-wrap">
            <table>
                <thead><tr><th>SKU</th><th>Product</th><th>Category</th><th>Price</th><th>Available</th></tr></thead>
                <tbody>
                <c:forEach items="${products.content}" var="product">
                    <tr>
                        <td><code><c:out value="${product.sku}"/></code></td>
                        <td><c:out value="${product.name}"/></td>
                        <td><c:out value="${product.category}"/></td>
                        <td><fmt:formatNumber value="${product.price}" type="currency" currencyCode="INR"/></td>
                        <td><c:out value="${product.quantity}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty products.content}"><tr><td colspan="5" class="empty">No matching products.</td></tr></c:if>
                </tbody>
            </table>
        </div>
        <nav class="pagination" aria-label="Inventory pages">
            <c:if test="${hasPrevious}"><a href="<c:url value='/products'><c:param name='q' value='${query}'/><c:param name='page' value='${currentPage - 1}'/></c:url>">Previous</a></c:if>
            <span>Page <c:out value="${currentPage + 1}"/></span>
            <c:if test="${hasNext}"><a href="<c:url value='/products'><c:param name='q' value='${query}'/><c:param name='page' value='${currentPage + 1}'/></c:url>">Next</a></c:if>
        </nav>
    </section>

    <section class="card">
        <h2>Add a product</h2>
        <form:form method="post" action="/products" modelAttribute="form" cssClass="product-form">
            <div class="field"><form:label path="sku">SKU</form:label><form:input path="sku" maxlength="32"/><form:errors path="sku" cssClass="error"/></div>
            <div class="field"><form:label path="name">Name</form:label><form:input path="name" maxlength="120"/><form:errors path="name" cssClass="error"/></div>
            <div class="field"><form:label path="category">Category</form:label><form:input path="category" maxlength="80"/><form:errors path="category" cssClass="error"/></div>
            <div class="field"><form:label path="price">Price (INR)</form:label><form:input path="price" type="number" min="0.01" step="0.01"/><form:errors path="price" cssClass="error"/></div>
            <div class="field"><form:label path="quantity">Available quantity</form:label><form:input path="quantity" type="number" min="0" max="1000000"/><form:errors path="quantity" cssClass="error"/></div>
            <button type="submit">Add to inventory</button>
        </form:form>
    </section>

    <footer>Spring Boot · Spring MVC · PostgreSQL · Apache Tomcat</footer>
</main>
</body>
</html>
