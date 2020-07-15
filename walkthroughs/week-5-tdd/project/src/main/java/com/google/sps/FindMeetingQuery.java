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
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> times = new ArrayList<>();

    Collection<String> attendees = request.getAttendees();
    
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      // If the event is longer than a day, no time slots are available to book a meeting.
      return times; 
    } else if (attendees.size() == 0) {
      // If there are no attendees, the entire day is available to book a meeting.
      times.add(TimeRange.WHOLE_DAY);
      return times;
    }

    // check if person has another meeting, if they do, have to worry about 
    // overlap; if they don't, anytime = good --> Hashset?

    // what if there is an event that goes to the next day
    int currentEndTime = TimeRange.START_OF_DAY; // time will be calculated in minutes
    System.out.println(endTime);
    for (Event event : events) {
      TimeRange time = event.getWhen();
      if (time.start() < endTime
      System.out.println(event.getWhen());
    }
    return times;
    // output timerange based on duration and attendees
  }
}
