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

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Finds potential meeting times. */
public final class FindMeetingQuery {

  /**
   * Returns available TimeRanges for the {@code request} given {@code events}, a Collection of
   * all events so far. 
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> timesForEveryone = new ArrayList<>();
    Collection<TimeRange> timesForNonOptional = new ArrayList<>();
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    System.out.println("HERE");
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      // If the event is longer than a day, no time slots are available to book a meeting.
      return timesForEveryone; 
    }
    if (attendees.size() == 0 && optionalAttendees.size() == 0) {
      // If there are no attendees, the entire day is available to book a meeting.
      timesForEveryone.add(TimeRange.WHOLE_DAY);
      return timesForEveryone;
    }
    System.out.println("HERE");
    int currentEndTime = TimeRange.START_OF_DAY;
    for (Event event : events) {
      // Ignores this event's time range if the people attending this event are 
      // not attending the requested meeting. 
      Set<String> eventAttendees = new HashSet<String>(event.getAttendees());
      eventAttendees.retainAll(attendees);
// List of optional attendees that are at this event. If there is one, make the time range from this event, usner
      // waAATI antendees doesn't include optional
      Set<String> optionalEventAttendeesForRequestedMeeting = new HashSet<String>(event.getAttendees());
      optionalEventAttendeesForRequestedMeeting.retainAll(optionalAttendees);
      if (eventAttendees.isEmpty() && optionalEventAttendeesForRequestedMeeting.isEmpty()) {
        System.out.println("both emtpy");
        continue;
      }

      // have intersection of attendees
      // intersect again to find optional vs event attendees --> optional attendees who are going to this
      // event, if there is at least 1 optional (add that time range to the timesForNonOptional)

      TimeRange time = event.getWhen();
      // Handles overlapping events and the case where the requested meeting duration is longer 
      // than the time gap.
      if ((time.start() <= currentEndTime) || (time.end() <= currentEndTime) ||
          (request.getDuration() > (time.start() - currentEndTime))) {
        System.out.println("if statement");
        // this time slot works for non optional (only optional people here at this event, no confirmed attendes)
        if (eventAttendees.isEmpty()) {
          TimeRange gapTimeRange = TimeRange.fromStartEnd(currentEndTime, time.start(), false);
          timesForNonOptional.add(gapTimeRange);
          System.out.println("eventAttendee is empty");
        }
        currentEndTime = Math.max(time.end(), currentEndTime);
        // Time doesn't work for either
        continue;
      }
      System.out.println("everyone attends");
      // for loop through optional attendees???? have to check which events the attendees are in....
      // each event has a list of attendees (no optional)
      TimeRange gapTimeRange = TimeRange.fromStartEnd(currentEndTime, time.start(), false);
      currentEndTime = time.end();
      timesForEveryone.add(gapTimeRange);
    }

    // Adds a timerange for after the latest event ends.
    if (currentEndTime < TimeRange.END_OF_DAY) {
      TimeRange latestTimeRange = TimeRange.fromStartEnd(currentEndTime, 
          TimeRange.END_OF_DAY, true);
      System.out.println("End of day");
      timesForEveryone.add(latestTimeRange);
    }

    if (timesForEveryone.size() == 0 && attendees.size() != 0) {
      System.out.println("non optional");
      return timesForNonOptional;
    }
    return timesForEveryone;
  }
}


// get optional attendees 
// if attendee can be accomodated, include just their event,
// if not, don't include any optionals
// have a second arraylist for events where optionals can attend (og arraylist for 
// everyone) 
// if the og al is empty, then return the optionals can't attend list (2nd one)