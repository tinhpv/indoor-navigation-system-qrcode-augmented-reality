function filterCompanyListByName() {
	var input, filter, div, a, txtValue;
	input = document.getElementById("txtCompany");
	filter = input.value.toUpperCase();
	a = document.getElementsByClassName("list-group-item list-group-item-action list-group-item-primary");
	for (i = 0; i < a.length; i++) {
		txtValue = a[i].textContent || a[i].innerText;
		if (txtValue.toUpperCase().indexOf(filter) > -1) {
			a[i].style.display = "";
		} else {
			a[i].style.display = "none";
		}
	}
}