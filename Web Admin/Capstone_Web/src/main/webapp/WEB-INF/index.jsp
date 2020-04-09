<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index Page</title>
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css" />
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/project.js"></script>
</head>
<body>
	<div class="bg">
		<div class="icon"
			style="background-image: url('${pageContext.request.contextPath}/images/background.png');">
			<div>
				<img src="${pageContext.request.contextPath}/images/icon.png" />
				<h2>Placeholder*</h2>
			</div>
		</div>
		<div class="content">

			<div class="content-header">
				<c:if test="${not empty sessionScope.building }">
					<p>Current building: ${sessionScope.building.name }</p>
					<a class="btn btn-primary" href="${pageContext.request.contextPath}/building">More info</a>
				</c:if>
				<c:if test="${empty sessionScope.building }">
					<p>No chosen building.</p>
				</c:if>
			</div>

			<div class="content-body">
				<c:if test="${not empty sessionScope.companyList }">
					<div id="companyUl" class="list-group">
						<div class="list-group-item">
							<strong>Company list</strong>
							<a href="${pageContext.request.contextPath}/getAllCompany">
								<i class="fas fa-sync-alt"></i>
							</a>
							<input style="float: right;" id="txtCompany" type="text" onkeyup="filterCompanyListByName()"
								placeholder="Search company">
						</div>
						<c:forEach var="companyDto" items="${sessionScope.companyList }" varStatus="counter">
							<div id="companyLi-row">
								<div id="companyLi-row-1">
									<button class="list-group-item list-group-item-action list-group-item-dark"
										aria-expanded="false" aria-controls="collapseCompany${counter.count }"
										data-target="#collapseCompany${counter.count }" data-toggle="collapse">
										${counter.count }. ${companyDto.name }
										<span class="badge badge-primary badge-pill">${companyDto.listBuilding.size() }</span>
									</button>
								</div>
								<div id="companyLi-row-2">
									<form action="${pageContext.request.contextPath}/building/createBuilding" method="get">
										<input type="hidden" name="companyId" value="${companyDto.id }" /> <input type="hidden"
											name="companyName" value="${companyDto.name }" />
										<button
											class="list-group-item list-group- list-group-item-action  list-group-item-primary"
											type="submit">
											<i class="far fa-plus-square"> New Building</i>
										</button>
									</form>
								</div>
							</div>
							<div class="collapse" id="collapseCompany${counter.count }">
								<div id="list-group">
									<c:forEach var="buildingDto" items="${companyDto.listBuilding }" varStatus="counter2">
										<a id="list-group-1"
											href="${pageContext.request.contextPath}/building/getBuilding?buildingId=${buildingDto.id}"
											class="list-group-item list-group-item-action">${counter.count}.${counter2.count}.
											${buildingDto.name } </a>
										<div id="list-group-2">
											<form action="${pageContext.request.contextPath}/downloadQrCode/${buildingDto.name}" method="get">
												<input type="hidden" name="buildingId" value="${buildingDto.id}" />
												<button type="submit"
													class="list-group-item list-group-item-action list-group-item-success">
													<i class="fas fa-download"> Download QR Codes</i>
												</button>
											</form>
										</div>
									</c:forEach>
								</div>
							</div>
						</c:forEach>
					</div>
				</c:if>
			</div>

			<form action="${pageContext.request.contextPath}/test" method="get">
				<button type="submit">TEST</button>
			</form>

			<%-- <div class="content-body">
				<c:if test="${not empty sessionScope.companyList }">
					<table class="table table-sm" style="width: 70%">
						<thead class="thead-dark">
							<tr>
								<th class="align-middle">#</th>
								<th class="align-middle">Tập đoàn</th>
								<th class="align-middle">
									<form action="${pageContext.request.contextPath}/getAllCompany" method="get">
										<button class="btn btn-info" type="submit">
											<i class="fas fa-sync-alt"> Làm mới</i>
										</button>
									</form>
								</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="companyDto" items="${sessionScope.companyList }" varStatus="counter">
								<tr class="table-info">
									<td>${counter.count }</td>
									<td>
										<b>${companyDto.name }</b>
									</td>
									<td>
										<button type="button" data-toggle="collapse"
											data-target="#collapseCompany${counter.count }" aria-expanded="false"
											aria-controls="collapseCompany${counter.count }">
											<i class="fas fa-caret-down"></i>
										</button>
									</td>
								</tr>
								<c:if test="${not empty companyDto.listBuilding }">
									<c:forEach var="buildingDto" items="${companyDto.listBuilding }">
										<tr class="collapse" id="collapseCompany${counter.count }">
											<td>ID: ${buildingDto.id }</td>
											<td>Tòa nhà: ${buildingDto.name }</td>
											<td>
												<form action="${pageContext.request.contextPath}/building/getBuilding" method="get">
													<input type="hidden" name="buildingId" value="${buildingDto.id }" />
													<button type="submit" class="btn btn-outline-primary">Lấy thông tin</button>
												</form>
											</td>
										<tr>
									</c:forEach>
									<tr>
								</c:if>
								<c:if test="${empty companyDto.listBuilding }">
									<tr class="collapse table-warning" id="collapseCompany${counter.count }">
										<td>Chưa có tòa nhà nào!</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</c:if>
			</div> --%>
		</div>
	</div>
</body>
</html>