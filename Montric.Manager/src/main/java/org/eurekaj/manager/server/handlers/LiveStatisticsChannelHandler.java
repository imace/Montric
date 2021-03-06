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
package org.eurekaj.manager.server.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eurekaj.api.datatypes.AccessToken;
import org.eurekaj.api.datatypes.Account;
import org.eurekaj.api.datatypes.LiveStatistics;
import org.eurekaj.api.enumtypes.UnitType;
import org.eurekaj.api.enumtypes.ValueType;
import org.eurekaj.manager.datatypes.ManagerLiveStatistics;
import org.eurekaj.manager.json.BuildJsonObjectsUtil;
import org.eurekaj.manager.json.ParseJsonObjects;
import org.eurekaj.manager.plugin.ManagerProcessIncomingStatisticsPluginService;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: jhs
 * Date: 5/18/11
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class LiveStatisticsChannelHandler extends EurekaJGenericChannelHandler {
	private static final Logger log = Logger.getLogger(LiveStatisticsChannelHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String jsonResponse = "";
        boolean errorRaised = false;
        String messageContent = getHttpMessageContent(e);
        try {
            JSONObject jsonObject = BuildJsonObjectsUtil.extractJsonContents(messageContent);
            //log.info("LiveStatistics: " + jsonObject);
            String accountName = getAccountForAccessToken(jsonObject);
            //log.info("Belongs to Account: " + accountName);
            
            if (jsonObject.has("storeLiveStatistics") && accountName != null) {
            	
                JSONArray statList = jsonObject.getJSONArray("storeLiveStatistics");
                List<LiveStatistics> liveStatList = new ArrayList<LiveStatistics>();

                for (int index = 0; index < statList.length(); index++) {
                    ManagerLiveStatistics liveStatistics = ParseJsonObjects.parseLiveStatistics(statList.getJSONObject(index), accountName);
                    liveStatList.add(liveStatistics);
                    
                    String agent = liveStatistics.getGuiPath().substring(0, liveStatistics.getGuiPath().indexOf(":"));
                    ManagerLiveStatistics agentStats = new ManagerLiveStatistics();
                    agentStats.setGuiPath(agent + ":Agent Statistics:API Call Count");
                    agentStats.setAccountName(accountName);
                    agentStats.setTimeperiod(liveStatistics.getTimeperiod());
                    agentStats.setUnitType(UnitType.N.value());
                    agentStats.setValueType(ValueType.AGGREGATE.value());
                    agentStats.setValue(1d);
                    
                    liveStatList.add(agentStats);
                }

                //Send to available plugins for processing
                ManagerProcessIncomingStatisticsPluginService.getInstance().processStatistics(liveStatList);
                
            } else {
            	errorRaised = true;
            	write401ToBuffer(ctx);
            }
        } catch (JSONException jsonException) {
        	log.info(messageContent);
            throw new IOException("Unable to process JSON Request", jsonException);
        }

        if (!errorRaised) {
        	writeContentsToBuffer(ctx, jsonResponse, "text/json");
        }
    }

    private String getAccountForAccessToken(JSONObject jsonObject) throws JSONException {
    	String accountName = null;

        if (jsonObject.has("liveStatisticsToken") && jsonObject.getString("liveStatisticsToken").length() >= 16) {
        	AccessToken accessToken = getAccountService().getAccessToken(jsonObject.getString("liveStatisticsToken"));
        	if (accessToken != null) {
        		accountName = accessToken.getAccountName();
        	}
        }
        
        return accountName;
    }
}
