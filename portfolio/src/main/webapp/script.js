// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const VISIBLE = 'visible';
const HIDDEN = 'hidden';

/**
 * Adds a random polaroid image to the page.
 */
function addRandomPolaroid() {
  const images =
      ['arches.JPG','cruise.jpg','dalat.jpg', 'mexico.jpg', 
      'pittsburghSunset.jpg', 'saigonLightening.jpg', 
      'saigonTower.jpg', 'thailand.jpg', 'yellowstone.JPG'];

  // Picks a random image.
  const image = images[Math.floor(Math.random() * images.length)];
  
  // Creates a div tag to store the polaroid image and description.
  let div = document.createElement("div");
  div.setAttribute('class', 'polaroid');

  // Creates a p tag that contains the image name.
  const imageName = image.split(".")[0];
  let imageText = createPTag(imageName);

  // Creates an img tag to store the image.
  let imgTag = createImgTag(image);

  div.appendChild(imgTag);
  div.appendChild(imageText);
  
  // Adds the polaroid div tag to the page.
  const imagesContainer = document.getElementById('images');
  imagesContainer.appendChild(div);
}

/**
 * Creates an img tag to store the image.
 * @param {string} image The name of the image
 *     file to create an img tag for.
 * @return {object} This returns an img tag using
 *     the given image.
 */
function createImgTag(image) {
  let imgTag = document.createElement("img");
  imgTag.setAttribute("src", 'images/' + image);
  
  const imageName = image.split(".")[0];
  imgTag.setAttribute("alt", imageName);
  return imgTag;
}

/**
 * Creates an p tag to store the given text.
 * @param {string} text The text to include in
 *     the p tag.
 * @return {object} This returns an p tag using
 *     the given image.
 */
function createPTag(text) {
  let pTag = document.createElement("p");
  pTag.innerText = text;
  return pTag;
}

/**
 * Reveals hidden description upon mouseover and hides the project title
 *  text.
 * @param {object} hoveredItem An anchor tag containing the
 *     project image and text.
 */
function revealOnMouseover(hoveredItem) {
  let toReveal = hoveredItem.getElementsByClassName('hidden')[0];
  let toHide = hoveredItem.getElementsByClassName('visible')[0];

  let background = hoveredItem.getElementsByClassName('background');

  toReveal.style.visibility = VISIBLE;
  toHide.style.visibility = HIDDEN;

  // Loops through all the tags that make up the background image
  // and lowers its brightness.
  for (let tag of background) {
    tag.style.filter = 'brightness(50%)';
  }
}

/**
 * Hides the description text upon mouseout and reveals hidden 
 *     project title text.
 * @param {object} hoveredItem An anchor tag containing the 
 *     project image and text.
 */
function hideOnMouseout(hoveredItem) {
  let toHide = hoveredItem.getElementsByClassName('hidden')[0];
  let toReveal = hoveredItem.getElementsByClassName('visible')[0];

  let background = hoveredItem.getElementsByClassName('background');

  toReveal.style.visibility = VISIBLE;
  toHide.style.visibility = HIDDEN;

  // Loops through all the tags that make up the background image
  // and resets its brightness back to 100%.
  for (let tag of background) {
    tag.style.filter = 'brightness(100%)';
  }
}
