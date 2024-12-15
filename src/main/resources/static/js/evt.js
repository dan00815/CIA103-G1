/**
 * 
 */

let currentIndex = 0;
let currentType = "";

function showSlider(type) {
  currentType = type;
  const slider = document.getElementById("slider");
  slider.classList.remove("hidden");

  const allSliders = document.querySelectorAll(".slider-images");
  allSliders.forEach((slider) => slider.classList.add("hidden"));

  const activeSlider = document.getElementById(`slider-${type}`);
  if (activeSlider) {
    activeSlider.classList.remove("hidden");
    currentIndex = 0; // 初始化第一張
    updateSlider(activeSlider);
  } else {
    console.error(`Slider type "${type}" not found!`);
  }
}

function updateSlider(activeSlider) {
  const images = activeSlider.querySelectorAll("img");
  images.forEach((img, index) => {
    img.style.display = index === currentIndex ? "block" : "none";
  });
}

function nextImage() {
  const activeSlider = document.getElementById(`slider-${currentType}`);
  if (activeSlider) {
    const images = activeSlider.querySelectorAll("img");
    currentIndex = (currentIndex + 1) % images.length;
    updateSlider(activeSlider);
  }
}

function prevImage() {
  const activeSlider = document.getElementById(`slider-${currentType}`);
  if (activeSlider) {
    const images = activeSlider.querySelectorAll("img");
    currentIndex = (currentIndex - 1 + images.length) % images.length;
    updateSlider(activeSlider);
  }
}

function closeSlider() {
  const slider = document.getElementById("slider");
  slider.classList.add("hidden");
}
