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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['I\'m ready, I\'m ready, I\'m ready...', 
      'You know what\'s funnier than 24? 25!', 
      'East?! I thought you said Weest!', 
      'Squidwaard, I used your clarinet to unclog my toilet!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function revealOnMouseover(hoveredItem) {
  let toReveal = hoveredItem.getElementsByClassName('hidden')[0];
  let toHide = hoveredItem.getElementsByClassName('visible')[0];

  console.log(toReveal);
  console.log(toHide);
// double check location of declarations and logic
  let background = hoveredItem.getElementsByClassName('background');

  toReveal.style.visibility = 'visible';
  toHide.style.visibility = 'hidden';

  for (i = 0; i < background.length; i++) {
    background[i].style.filter = 'brightness(50%)';
    console.log(background[i]);
    console.log("this is background");
  }
  console.log("this is done")


}

function hideOnMouseOut(hoveredItem) {
  let toHide = hoveredItem.getElementsByClassName('hidden')[0];
  let toReveal = hoveredItem.getElementsByClassName('visible')[0];

  let background = hoveredItem.getElementsByClassName('background');

  toReveal.style.visibility = 'visible';
  toHide.style.visibility = 'hidden';

  for (i = 0; i < background.length; i++) {
    background[i].style.filter = 'brightness(100%)';
    console.log(background[i]);
    console.log("this is background");
  }
}