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
const POLAROID_CLASS = 'polaroid';
const PROJECT_BACKGROUND = 'background';
const REDUCED_BRIGHTNESS = 'brightness(50%)';
const MAX_BRIGHTNESS = 'brightness(100%)';
const COMMENT_CONTAINER = 'comment-container';
const SELECTED_INDEX = 'selectedIndex';
const COMMENT_AMOUNT = 'amount';
const DATA_SERVLET = '/data';
const SORT = 'sort';
const AMOUNT_SELECTED_INDEX = 'amountSelectedIndex';
const SORT_SELECTED_INDEX = 'sortSelectedIndex';
const P_TAG = 'p';
const DIV_TAG = 'div';
const IMG_TAG = 'img';
const COMMENT_IMAGE_DESCRIPTION = "Comment Image";
const COMMENT_IMAGE_CLASS = "comment-image";
// After index 21 in the timestamp string is the milliseconds and 
// the timezone name. Including that for the comments is excessive
// and thus, ignored.
const END_OF_TIMESTAMP = 21;
const COMMENT_CLASS = 'comment';
const INFO_CLASS = 'info';

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
  div.setAttribute('class', POLAROID_CLASS);

  const imgTag = createImgTag(image);
  div.appendChild(imgTag);

  const imageName = image.split(SEPARATOR)[0];
  appendPTagToContainer(imageName, div);
  
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
  const responsePromise = fetch(DATA_SERVLET);
  
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
  const commentContainer = document.getElementById(COMMENT_CONTAINER);
  commentContainer.innerHTML = message;
}

/**
 * Adds {@code comments} to the DOM as list elements.
 */
function addMultipleMessagesToDom(comments) {
  const commentContainer = document.getElementById(COMMENT_CONTAINER);

  // Removes the ul tag in the container if there is one. This prevents having
  // multiple sets of ul tags every time the number of comments is changed.
  if (commentContainer.firstChild) {
    commentContainer.removeChild(commentContainer.firstChild);
  }
  const ulElement = document.createElement('ul');
  commentContainer.appendChild(ulElement);

  comments.forEach((comment) => {
    appendTextToList(comment, ulElement);
  });
}

/**
 * Fetches the comment from the JSON server /data and adds it to the DOM.
 *
 * <p>If {@code pageReloadBoolean} is true, then retrieves the previous
 * selected index from the localStorage. Otherwise, retrieves the 
 * current selected index.
 */
function getMessageFromJSON(pageReloadBoolean) {
  let selectedIndex;
  let amountSelector = document.getElementById(COMMENT_AMOUNT);

  let sortSelector = document.getElementById(SORT);
  let sortSelectedIndex;
  
  // Retrieves the selected index from local storage if there is a value for 
  // it. This is necessary because after a page reload, the selected index 
  // is set to its default value of 0. 
  if (pageReloadBoolean && localStorage.getItem(AMOUNT_SELECTED_INDEX) != null) {
    amountSelectedIndex = localStorage.getItem(AMOUNT_SELECTED_INDEX);
    sortSelectedIndex = localStorage.getItem(SORT_SELECTED_INDEX);
  } else {
    amountSelectedIndex = amountSelector.selectedIndex;
    sortSelectedIndex = sortSelector.selectedIndex;
  } 

  amountSelector.options[amountSelectedIndex].selected = true;
  let selectedAmount = amountSelector.options[amountSelectedIndex].value;

  sortSelector.options[sortSelectedIndex].selected = true;
  let selectedSort = sortSelector.options[sortSelectedIndex].value;
  
  // Saves the current selected index to the local storage to use in case
  // the page reloads.
  localStorage.setItem(AMOUNT_SELECTED_INDEX, amountSelectedIndex);
  localStorage.setItem(SORT_SELECTED_INDEX, sortSelectedIndex);

  fetch(DATA_SERVLET + '?' + COMMENT_AMOUNT + '=' 
        + selectedAmount + '&' + SORT + '=' + selectedSort)
      .then(response => response.json())
      .then((comments) => {
        addMultipleMessagesToDom(comments);
      });
}

/**
 * Creates an <li> element containing {@code comment}'s text, timestamp,
 *  and submitter's name and appends it to {@code ulElement}.
 */
function appendTextToList(comment, ulElement) {
  const liElement = document.createElement('li');

  const infoDivElement = document.createElement('div');
  infoDivElement.className = INFO_CLASS;

  const date = (new Date(comment.timestamp)).toString()
      .substring(0, END_OF_TIMESTAMP);

  appendPTagToContainer(comment.nickname, infoDivElement);
  appendPTagToContainer("Feeling " + comment.mood, infoDivElement);
  appendPTagToContainer(date, infoDivElement);

  liElement.appendChild(infoDivElement);
  const textPElement = appendPTagToContainer(comment.text, liElement);
  textPElement.className = COMMENT_CLASS;
  
  if (comment.imageUrl.hasOwnProperty('value') && comment.imageUrl.value != '') {
    liElement.appendChild(createCommentImage(comment.imageUrl));
  }
  

  updateBackgroundColorBasedOnSentiment(comment.sentiment, liElement);
  // Separates each comment with a horizontal bar.
  liElement.appendChild(document.createElement('hr'));
  ulElement.appendChild(liElement);
}

/**
 * Updates the background color of {@code elementToColor} to be a variant of 
 * green or red based on {@code score}.
 */
function updateBackgroundColorBasedOnSentiment(score, elementToColor) {
  // The original sentiment score is from -1 to 1, so shifting the score
  // by 1 and dividing by 2 will yield a ratio where 1 is positive and 0
  // is negative.
  const scoreAsRatio = (score + 1) / 2 
  // In HSL, 120 is green and 0 is red so a positive score will be green.
  const HSLColor = scoreAsRatio * 120
  // The second parameter in HSL indicates the saturation and the third
  // parameter indicates the lightness.
  elementToColor.style.backgroundColor = "hsl(" + HSLColor + ", 80%, 80%)"
}
var test;
/**
 * Creates an <img> tag using {@code imageUrl} and returns it.
 */
async function createCommentImage(imageUrl) {
  const image = await getBlob(imageUrl.value);
  test = image;
  const imgTag = document.createElement(IMG_TAG);
  imgTag.setAttribute('src', image);
  imgTag.setAttribute('alt', COMMENT_IMAGE_DESCRIPTION);
  imgTag.className = COMMENT_IMAGE_CLASS;
  return imgTag;
}

/**
 * Creates an <img> tag using {@code imageUrl} and returns it.
 */
async function getBlob(imageUrl) {
  const myHeaders = new Headers();
  myHeaders.append('blob-key', imageUrl);

  const request = new Request('/blob', {method: 'GET', headers: myHeaders});

  const response = await fetch(request);
  const responseBlob = await response.blob();
  const url = await URL.createObjectURL(responseBlob).toString();

  return url;
}

/**
 * Creates a <p> tag to store the given {@code text} inside the
 * {@code container} and returns the <p> tag using the given text.
 */
function appendPTagToContainer(text, container) {
  const pTag = document.createElement(P_TAG);
  pTag.innerText = text;
  container.appendChild(pTag);
  return pTag;
}

/**
 * Deletes all of the comments from the page.
 */
function deleteAllComments() {
  const params = new URLSearchParams();
  fetch('/delete-data', {method: 'POST', body: params})
      // Fetches from /data. That way, /data is always
      // called whenever the page changes.
      .then((getMessageFromJSON(false)));
}

/**
 * Fetches the HTML from the /login server and 
 * appends it to the DOM.
 */
function fetchLogin() {
  fetch('/login')
      .then(response => response.text())
      .then(appendToLogin);
}

/**
 * Appends {@code text} to login div container.
 */
function appendToLogin(text) {
  const login = document.getElementById('login');
  login.innerHTML = text;
}

/** 
 * Fetches the HTML form from /nickname and appends
 * it to the DOM.
 */
function changeNickname() {
  fetch('/nickname')
      .then(response => response.text())
      .then(appendToLogin);
}

/** 
 * Updates the form action to be the blobstore Url and reveals the form.
 */
function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('comment-form');
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
      });
}
