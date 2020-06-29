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
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['I\'m ready, I\'m ready, I\'m ready...', 
      'You know what\'s funnier than 24? 25!', 
      'East?! I thought you said Weast!', 
      'Squidward, I used your clarinet to unclog my toilet!'];

  // Picks a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Adds the random greeting to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Reveals hidden description upon mouseover and hides the project title
   text.
 * @param {object} hoveredItem An anchor tag containing the
      project image and text.
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
   project title text.
 * @param {object} hoveredItem An anchor tag containing the 
      project image and text.
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
