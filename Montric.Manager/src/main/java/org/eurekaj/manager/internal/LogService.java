/**
    EurekaJ Profiler - http://eurekaj.haagen.name
    
    Copyright (C) 2010-2011 Joachim Haagen Skeie

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.eurekaj.manager.internal;

import java.util.List;

public interface LogService {

	public LogEntry getLogEntry(String agentName, Long millisecond);
	
	public List<LogEntry> getLogEntry(String agentName, Long fromMillisecond, Long toMillisecond);
	
	public void persistLogEntry(LogEntry logEntry);
	
	public void storeLog(String agentName, Long millisecond, String logMessage);
}