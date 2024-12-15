/**
 * 
 */

const home_prev = document.querySelector("#home_prev");
const click_into_btn = document.querySelector("#click_into");
const prev_container = document.querySelector("#prev_container");
const carousel = document.querySelector("#carousel");
const tour_area = document.querySelector("#tour_area");
const tour_aTag = document.querySelectorAll(".fun_container a");
const menu_aTag = document.querySelectorAll(".menu-container a");



window.addEventListener("DOMContentLoaded", function () {
  const home_prev_ani = sessionStorage.getItem("hasplayed");

  if (!home_prev_ani) {
    prev_container.style.display = "block";
    nav.style.opacity = 0;
    carousel.style.opacity = 0;
    tour_area.style.opacity = 0;
  }
});

click_into_btn.addEventListener("click", function () {
  this.style.display = "none";
  prev_container.classList.add("click_into_page");

  setTimeout(() => {
    prev_container.style.display = "none";
    nav.style.opacity = 1;
    carousel.style.opacity = 1;
    tour_area.style.opacity = 1;
  }, 2000);

  sessionStorage.setItem("hasplayed", true);
});

$("#home_prev")
  .slick({
    fade: true,
    autoplay: true,
    autoplaySpeed: 3000,
    speed: 1000,
    cssEase: "linear",
    draggable: false,
    arrows: false,
    pauseOnHover: false,
    pauseOnFocus: false,
  })
  .on("beforeChange", function (event, slick, currentSlide, nextSlide) {
    $("#home_prev .slick-slide").removeClass("focus_closer");

    $("#home_prev .slick-slide").eq(currentSlide).addClass("focus_closer");
    $("#home_prev .slick-slide").eq(nextSlide).addClass("focus_closer");
  });

$("#carousel").slick({
  dots: true,
  speed: 1000,
  autoplay: true,
  autoplaySpeed: 5000,
  slidesToShow: 1,
  pauseOnHover: false,
  pauseOnFocus: false,
  prevArrow:
    '<button type="button" class="slick-prev custom-prev"><i class="fa-solid fa-chevron-left"></i></button>',
  nextArrow:
    '<button type="button" class="slick-next custom-next"><i class="fa-solid fa-chevron-right"></i></button>',
});

// 停掉tour_area a Tag的預設行為
tour_aTag.forEach((i) =>
  i.addEventListener("click", (e) => e.preventDefault())
);

menu_aTag.forEach((i) =>
  i.addEventListener("click", (e) => e.preventDefault())
);




