/**
 * 
 */

const ham_list = document.querySelector(".ham_list");
const nav = document.querySelector("nav");

//漢堡
document.querySelector(".hamburger").addEventListener("click", function() {
	this.classList.toggle("is-active");
});


ham_list.addEventListener("click", function(e) {
	e.stopPropagation();
});

$(".hamburger").on("click", function() {
	$(".ham_list").toggleClass("showList");

});

// 監控滾輪事件，讓導覽列變透明
window.addEventListener("scroll", (e) => {
	if (this.scrollY !== 0) {
		nav.style.backgroundColor = "white";
		nav.style.boxShadow = "0px 8px 10px lightgray";
	} else {
		nav.style.backgroundColor = "white";
		nav.style.boxShadow = "none";
	}
});
