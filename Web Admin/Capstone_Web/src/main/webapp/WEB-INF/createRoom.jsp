<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/point.css" />
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<c:if test="${empty sessionScope.building }">
		<c:redirect url="${pageContext.request.contextPath}/"></c:redirect>
	</c:if>

	<img id="imgMap" src="${sessionScope.floor.linkMap }" />
	<input id="imgSrc" type="hidden" value="${sessionScope.floor.linkMap }" />

	<div>
		<div id="canvasWraper">
			<canvas id="canvas"></canvas>
		</div>
		<div id="content">
			<div id="content-header" class="shadow p-3 mb-3 bg-white rounded">
				<a class="btn btn-outline-success" href="${pageContext.request.contextPath}/floor">
					<i class="fas fa-arrow-circle-left"> Back</i>
				</a>
			</div>

			<div id="content-body" class="shadow p-3 mb-3 bg-white rounded">
				<form action="${pageContext.request.contextPath}/room/create" method="post">
					<div id="content-body-header">
						<h5>New room for "${sessionScope.location.name }"</h5>
					</div>
					<input type="hidden" value="${sessionScope.location.id }" name="locationId">
					<div class="form-group row">
						<label for="name" class="col-sm-3 col-form-label">Name</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" id="name" name="name" placeholder="Exp: Room 001"
								required="required" />
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Ratios</label>
						<div class="col-sm-7">
							<label class="col-form-label">(</label>
							<input type="number" step="0.001" class="form-control" id="ratioX" name="ratioX"
								placeholder="X" required="required" onkeydown="return false;" />
							<label class="col-form-label">,</label>
							<input type="number" step="0.001" class="form-control" id="ratioY" name="ratioY"
								placeholder="Y" required="required" onkeydown="return false;" />
							<label class="col-form-label">)</label>
						</div>
						<p class="col-sm-1">*</p>
					</div>
					<i class="fas fa-long-arrow-alt-left"> Click on the map to get the cordinates.</i>
					<div class="action-wraper">
						<button class="left btn btn-success" data-toggle="tooltip" data-placement="bottom"
							title="Draw all rooms of this location" onclick="drawAllRoom()" type="button">
							<i class="fas fa-search-location"></i>
						</button>
						<button class="left btn btn-secondary" data-toggle="tooltip" data-placement="bottom"
							title="Clear the map" onclick="clearCanvas()" type="button">
							<i class="fas fa-sync-alt"></i>
						</button>
						<button type="submit" class="right btn btn-primary">Create</button>
					</div>
				</form>
			</div>
			<p class="success">${requestScope.createSuccess }</p>
		</div>
	</div>

	<script>
		window.onload = function() {
			var canvas = document.getElementById("canvas");
			var ctx = canvas.getContext("2d");

			var img = document.getElementById("imgMap");

			ctx.canvas.width = img.width;
			ctx.canvas.height = img.height;

			ctx.drawImage(img, 0, 0, img.width, img.height);

			img.style.display = "none";

			/* document.getElementById("mapDiv").remove(); */
			drawLocation(${sessionScope.location.ratioX}, ${sessionScope.location.ratioY});
		}

		/* function drawRoom(ratioX, ratioY) {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			/* var ratioX = document.getElementById("ratioX").value;
			var ratioY = document.getElementById("ratioY").value;
			ctx.beginPath();
			ctx.arc(ratioX * canvas.width, ratioY * canvas.height, 5, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();
		} */

		function drawLocation(ratioX, ratioY) {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			ctx.beginPath();
			ctx.fillStyle = "#FF0000";
			ctx.arc(ratioX * canvas.width, ratioY * canvas.height, 3, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();
		}

		document.addEventListener("DOMContentLoaded", init, false);

		function init() {
			var canvas = document.getElementById("canvas");
			canvas.addEventListener("mousedown", getPosition, false);
		}
		
		function drawAllRoom() {
			var roomData = [], roomInfo = [], i;
			<c:forEach items="${sessionScope.location.listRoom}" var="roomDto">
			roomInfo.push("${roomDto.name}", ${roomDto.ratioX}, ${roomDto.ratioY});
			roomData.push(roomInfo);
			roomInfo = [];
			</c:forEach>
			
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			for (i = 0; i < roomData.length; i++) {
				ctx.beginPath();
				ctx.fillStyle = "#000000";
				ctx.arc(roomData[i][1] * canvas.width, roomData[i][2] * canvas.height, 3, 0, Math.PI * 2, true)
				ctx.closePath();
				ctx.fill();
				
				ctx.font = "10px Arial";
				ctx.textAlign = "center";
				ctx.fillText(roomData[i][0], (roomData[i][1] * canvas.width), (roomData[i][2] * canvas.height) + 15);
			}
		}

		function getPosition(event) {
			var x = new Number();
			var y = new Number();
			var canvas = document.getElementById("canvas");

			if (event.x != undefined && event.y != undefined) {
				x = event.x;
				y = event.y;
			} else // Firefox method to get the position
			{
				x = event.clientX + document.body.scrollLeft
						+ document.documentElement.scrollLeft;
				y = event.clientY + document.body.scrollTop
						+ document.documentElement.scrollTop;
			}

			x -= canvas.offsetLeft;
			y -= canvas.offsetTop;

			document.getElementById("ratioX").value = (x / canvas.width)
					.toFixed(3);
			document.getElementById("ratioY").value = (y / canvas.height)
					.toFixed(3);

			var ctx = document.getElementById("canvas").getContext("2d");
			
			/* draw location */
			ctx.beginPath();
			ctx.fillStyle = "#FF0000";
			ctx.arc(${sessionScope.location.ratioX} * canvas.width, ${sessionScope.location.ratioY} * canvas.height, 3, 0,
					Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();
			
			/* draw room */
			ctx.beginPath();
			ctx.fillStyle = "#000000";
			ctx.arc(x, y, 3, 0, Math.PI * 2, true)
			ctx.closePath();
			ctx.fill();
			
			/* draw line */
			/* ctx.beginPath();
			ctx.lineWidth = 3;
			ctx.moveTo(${sessionScope.location.ratioX} * canvas.width, ${sessionScope.location.ratioY} * canvas.height);
			ctx.lineTo(x, y);
			ctx.stroke();
			ctx.closePath(); */
		}

		function clearCanvas() {
			var canvas = document.getElementById("canvas");
			var ctx = document.getElementById("canvas").getContext("2d");
			ctx.clearRect(0, 0, canvas.width, canvas.height);

			var img = new Image;
			img.src = document.getElementById("imgSrc").value;
			img.style.maxWidth = "70vw";
			img.style.maxHeight = "99vh";

			ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
			
			drawLocation(${sessionScope.location.ratioX}, ${sessionScope.location.ratioY});
		}
		
		$(function() {
			$('[data-toggle="tooltip"]').tooltip()
		})
	</script>
</body>
</html>