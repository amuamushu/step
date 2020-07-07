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
const IMAGES_FOLDER = 'images/';
const IMAGES_DIV = 'images';
const PROJECT_BACKGROUND = 'background';
const REDUCED_BRIGHTNESS = 'brightness(50%)';
const MAX_BRIGHTNESS = 'brightness(100%)';


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
  const imagesContainer = document.getElementById(IMAGES_DIV);
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
  imgTag.setAttribute('src', IMAGES_FOLDER + image);
  
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
 * text.
 * @param {object} hoveredItem An anchor tag containing the
 *     project image and text.
 */
function revealOnMouseover(hoveredItem) {
  const toReveal = hoveredItem.getElementsByClassName(HIDDEN)[0];
  const toHide = hoveredItem.getElementsByClassName(VISIBLE)[0];

  const background = hoveredItem.getElementsByClassName(PROJECT_BACKGROUND);

  toReveal.style.visibility = VISIBLE;
  toHide.style.visibility = HIDDEN;

  // Loops through all the tags that make up the background image
  // and lowers its brightness.
  for (let tag of background) {
    tag.style.filter = REDUCED_BRIGHTNESS;
  }
}

/**
 * Hides the description text upon mouseout and reveals hidden 
 * project title text.
 * @param {object} hoveredItem An anchor tag containing the 
 *     project image and text.
 */
function hideOnMouseout(hoveredItem) {
  const toHide = hoveredItem.getElementsByClassName(HIDDEN)[0];
  const toReveal = hoveredItem.getElementsByClassName(VISIBLE)[0];

  const background = hoveredItem.getElementsByClassName(PROJECT_BACKGROUND);

  toReveal.style.visibility = VISIBLE;
  toHide.style.visibility = HIDDEN;

  // Loops through all the tags that make up the background image
  // and resets its brightness back to 100%.
  for (let tag of background) {
    tag.style.filter = MAX_BRIGHTNESS;
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
 * Obtains the reponse's text and adds it to the DOM.
 * @param {object} response A promise that was fetched from a URL.
 */
function handleResponse(response) {
  const textPromise = response.text();
  
  textPromise.then(addSingleCommentToDom);
}

/**
 * Adds a single {@code comment} inside of the div
 * message-container. 
 */
function addSingleCommentToDom(comment) {
  const commentContainer = document.getElementById('comment-container');
  commentContainer.innerHTML = message;
}

/**
 * Adds {@code comments} to the DOM as list elements.
 */
function addMultipleMessagesToDom(comments) {
  const commentContainer = document.getElementById('comment-container');

  // Removes the ul tag in the container if there is one to prevent having
  // multiple sets of ul tags every time the number of comments is changed.
  if (commentContainer.firstChild) {
    commentContainer.removeChild(commentContainer.firstChild);
  }
  const ulElement = document.createElement('ul');
  commentContainer.appendChild(ulElement);

  comments.forEach((comment) => {
    appendTextToList(comment.text, ulElement);
  });
}

/**
 * Fetches the comment from the JSON server /data and adds it to the DOM.
 * 
 * <p>{@code pageReloadBoolean} indicates whether the method is called
 * when the page refreshes or when a new comment amount is inputted.
 */
function getMessageFromJSON(pageReloadBoolean) {
  let selectedIndex;
  let amountSelector = document.getElementById("amount");
  
  // Retrieves the selected index from local storage if there is a value for it
  // because after a page reload, the selected index is set back to its default
  // value of 0. 
  if (pageReloadBoolean && localStorage.getItem('selectedIndex') !== null) {
    selectedIndex = localStorage.getItem('selectedIndex');
  } else {
    selectedIndex = amountSelector.selectedIndex;
  } 

  amountSelector.options[selectedIndex].selected = true;
  let selectedAmount = amountSelector.options[selectedIndex].value;
  
  // Saves the current selected index to the local storage to use in case
  // the page reloads.
  localStorage.setItem("selectedIndex", selectedIndex);
  fetch('/data?amount=' + selectedAmount)
      .then(response => response.json())
      .then((comments) => {
        addMultipleMessagesToDom(comments);
      });
}

/**
 * Creates an <li> element containing {@code text} and appends it to 
 * {@code ulElement}.
 */
function appendTextToList(text, ulElement) {
  const liElement = document.createElement('li');
  liElement.innerText = text;

  ulElement.appendChild(liElement);
}
