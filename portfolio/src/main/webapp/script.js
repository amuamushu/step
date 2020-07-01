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
const SEPARATOR = '.';

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
  const div = document.createElement('div');
  div.setAttribute('class', 'polaroid');

  // Creates a p tag that contains the image name.
  const imageName = image.split(SEPARATOR)[0];
  const imageText = createPTag(imageName);

  // Creates an img tag to store the image.
  const imgTag = createImgTag(image);

  div.appendChild(imgTag);
  div.appendChild(imageText);
  
  // Adds the polaroid div tag to the page.
  const imagesContainer = document.getElementById('images');
  imagesContainer.appendChild(div);
}

/**
 * Creates an img tag needed for storing the image.
 * @param {string} image The name of the image
 *     file to create an img tag for.
 * @return {object} Returns an img tag using
 *     the given image file name.
 */
function createImgTag(image) {
  const imgTag = document.createElement('img');
  imgTag.setAttribute('src', 'images/' + image);
  
  const imageName = image.split(SEPARATOR)[0];
  imgTag.setAttribute('alt', imageName);
  return imgTag;
}

/**
 * Creates a p tag to store the given text.
 * @param {string} text The text to include in
 *     the p tag.
 * @return {object} Returns an p tag using
 *     the given text.
 */
function createPTag(text) {
  const pTag = document.createElement('p');
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
  const toReveal = hoveredItem.getElementsByClassName('hidden')[0];
  const toHide = hoveredItem.getElementsByClassName('visible')[0];

  const background = hoveredItem.getElementsByClassName('background');

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
  const toHide = hoveredItem.getElementsByClassName('hidden')[0];
  const toReveal = hoveredItem.getElementsByClassName('visible')[0];

  const background = hoveredItem.getElementsByClassName('background');

  toReveal.style.visibility = VISIBLE;
  toHide.style.visibility = HIDDEN;

  // Loops through all the tags that make up the background image
  // and resets its brightness back to 100%.
  for (let tag of background) {
    tag.style.filter = 'brightness(100%)';
  }
}

/**
 * Fetches the comment from the server /data and adds it to the DOM.
 */
function getComment() {
  const responsePromise = fetch('/data');
  
  responsePromise.then(handleResponse);
}

/**
 * Handles response by converting it to text and passing the result
       to addMessageToDom().
 * @param {object} response A promise that was fetched from a URL.
 */
function handleResponse(response) {
  const textPromise = response.text();
  
  textPromise.then(addSingleCommentToDom);
}

/**
 * Adds the given single comment to the DOM.
 * @param {string} comment The text to be added inside of the div 
       comment-container. 
 */
function addSingleCommentToDom(comment) {
  const commentContainer = document.getElementById('comment-container');
  commentContainer.innerHTML = message;
}

/**
 * Adds multiple comments to the DOM as list elements.
 * @param {object} comments An array containing messages.
 */
function addMultipleMessagesToDom(comments) {
  const commentContainer = document.getElementById('comment-container');
  const ulElement = document.createElement('ul');
  commentContainer.appendChild(ulElement);

  comments.forEach((comment) => {
    appendTextToList(comment.text, ulElement);
  });
}

/**
 * Fetches the comment from the JSON server /data and adds it to the DOM.
 * Method is called everytime the page is refreshed.
 */
function getCommentFromJSON() {
  fetch('/data')
      .then(response => response.json())
      .then((comments) => {
        addMultipleMessagesToDom(comments);
      });
}

/**
 * Creates an <li> element containing text and appends it to the given
   ul tag.
   @param {string} text Text to be added as a list element.
   @param {object} ulElement UL element that the list element 
       will be appended to. 
 */
function appendTextToList(text, ulElement) {
  const liElement = document.createElement('li');
  liElement.innerText = text;

  ulElement.appendChild(liElement);
}
