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

    int currentEndTime = TimeRange.START_OF_DAY;
    for (Event event : events) {
      // Ignores this event's time range if the people attending this event are 
      // not attending the requested meeting. 
      Set<String> eventAttendees = new HashSet<String>(event.getAttendees());
      eventAttendees.retainAll(attendees);
      if (eventAttendees.isEmpty()) {
        continue;
      }

      TimeRange time = event.getWhen();
      // Skips this event because it ended before or at the same time as
      // the latest event.
      if (time.end() <= currentEndTime) {
        continue;
      } else if (time.start() <= currentEndTime) {
        // Handles overlapping events that end after the current latest time.
        currentEndTime = time.end();
        continue;
      }else if (request.getDuration() > (time.start() - currentEndTime)) {
        // Does not add the time range if the time gap is smaller than the duration of
        // the requested meeting.
        currentEndTime = time.end();
        continue;
      }
      TimeRange gapTimeRange = TimeRange.fromStartEnd(currentEndTime, time.start(), false);
      currentEndTime = time.end();
      times.add(gapTimeRange);
    }

    // Adds a timerange for after the latest event ends.
    if (currentEndTime < TimeRange.END_OF_DAY) {
      TimeRange latestTimeRange = TimeRange.fromStartEnd(currentEndTime, 
          TimeRange.END_OF_DAY, true);
      times.add(latestTimeRange);
    }
    return times;
  }
}
